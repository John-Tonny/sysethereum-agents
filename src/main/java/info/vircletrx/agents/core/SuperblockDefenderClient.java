package info.vircletrx.agents.core;

import lombok.extern.slf4j.Slf4j;
import info.vircletrx.agents.constants.AgentConstants;
import info.vircletrx.agents.constants.AgentRole;
import info.vircletrx.agents.constants.TrxAddresses;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.core.bridge.BattleContractApi;
import info.vircletrx.agents.core.bridge.ClaimContractApi;
import info.vircletrx.agents.core.bridge.Superblock;
import info.vircletrx.agents.core.bridge.SuperblockContractApi;
import info.vircletrx.agents.core.bridge.battle.NewBattleEvent;
import info.vircletrx.agents.core.bridge.battle.SuperblockSuccessfulEvent;
import info.vircletrx.agents.core.vircle.*;
import info.vircletrx.agents.core.trx.TrxWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.service.ChallengeEmailNotifier;
import info.vircletrx.agents.service.PersistentFileStore;
import info.vircletrx.agents.util.RandomizationCounter;

import java.io.*;
import java.math.BigInteger;
import java.util.*;

/**
 * Monitors the Tronx blockchain for superblock-related events
 * and defends/confirms the ones submitted by the agent.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockDefenderClient")
public class SuperblockDefenderClient extends SuperblockBaseClient {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockDefenderClient");

    private final SystemProperties config;
    private final PersistentFileStore persistentFileStore;
    private final BattleContractApi battleContractApi;
    private final SuperblockChain localSuperblockChain;
    private final RandomizationCounter randomizationCounter;
    private final BigInteger superblockTimeout;

    public SuperblockDefenderClient(
            SystemProperties config,
            AgentConstants agentConstants,
            PersistentFileStore persistentFileStore,
            TrxWrapper trxWrapper,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            SuperblockChain superblockChain,
            RandomizationCounter randomizationCounter,
            BigInteger superblockTimeout,
            TrxAddresses trxAddresses,
            ChallengeEmailNotifier challengeEmailNotifier
    ) {
        super(AgentRole.SUBMITTER, config, agentConstants, trxWrapper, superblockContractApi, battleContractApi, claimContractApi, challengeEmailNotifier);

        this.config = config;
        this.persistentFileStore = persistentFileStore;
        this.battleContractApi = battleContractApi;
        this.localSuperblockChain = superblockChain;
        this.randomizationCounter = randomizationCounter;
        this.superblockTimeout = superblockTimeout;
        this.myAddress = trxAddresses.generalPurposeAddress;
    }

    @Override
    public long reactToEvents(long fromBlock, long toBlock) {
        try {
            respondToNewBattles(fromBlock, toBlock);
            respondToHeaders(fromBlock, toBlock);
            submitterWonBattles(fromBlock, toBlock);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return fromBlock - 1;
        }
        return toBlock;
    }

    @Override
    protected void reactToElapsedTime() {
        try {
            confirmEarliestApprovableSuperblock();
            System.out.println( "defender超块延时:"+new Date().toString() );   // john
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }


    /* ---- CONFIRMING/DEFENDING ---- */

    /* - Reacting to earePendinglapsed time - */
    /**
     * Responds to ongoing battle by responding with 16 headers 3 times and 12 headers the last time for a total of 60 headers over 4 transactions/blocks
     * @throws Exception
     */
    void respondToHeaders(long fromBlock, long toBlock) throws Exception {
        List<TrxWrapper.RespondHeadersEvent> respondHeaderEvents =
                trxWrapper.getNewRespondHeadersEvents(fromBlock, toBlock);

        for (TrxWrapper.RespondHeadersEvent respondHeaderEvent : respondHeaderEvents) {
            if (isMine(respondHeaderEvent) && battleContractApi.sessionExists(respondHeaderEvent.superblockHash)) {
                int numMerkleHashesBySession = battleContractApi.getNumMerkleHashesBySession(respondHeaderEvent.superblockHash);

                // only respond if the event is the one you are looking for (it matches the number of hashes the contract thinks is the latest)

                if (respondHeaderEvent.merkleHashCount == numMerkleHashesBySession) {
                    logger.info("Header response detected for superblock {}. Merkle hash count: {}. Responding with next set now.", respondHeaderEvent.superblockHash, respondHeaderEvent.merkleHashCount);
                    trxWrapper.respondBlockHeaders(respondHeaderEvent.superblockHash, respondHeaderEvent.merkleHashCount);
                }
            }
        }
    }
    /**
     * Finds earliest superblock that's not invalid and stored locally,
     * but not confirmed in Sysethereum Contracts, and confirms it if its timeout has passed
     * and it either received no challenges or won all battles.
     * If the superblock is indeed confirmed, its status in Sysethereum Contracts
     * is set to Approved if it received no challenges and SemiApproved otherwise.
     * @throws Exception
     */
    private void confirmEarliestApprovableSuperblock() throws Exception {
        Keccak256Hash bestSuperblockId = superblockContractApi.getBestSuperblockId();
        Superblock chainHead = localSuperblockChain.getChainHead();

        if (chainHead.getHash().equals(bestSuperblockId)) {
            // Contract and local db best superblocks are the same, do nothing.
            return;
        }


        Superblock toConfirm = localSuperblockChain.getFirstDescendant(bestSuperblockId);
        if (toConfirm == null) {
            logger.info("Best superblock from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash toConfirmId = toConfirm.getHash();
        Superblock highestDescendant = trxWrapper.getHighestApprovableOrNewDescendant(toConfirm, bestSuperblockId);
        if (highestDescendant == null) {
            logger.info("Highest descendent from contracts, {}, not found in local database. Stopping.", bestSuperblockId);
            return;
        }
        Keccak256Hash highestDescendantId = highestDescendant.getHash();


        // deal with your own superblock claims or if it has become unresponsive we allow someone else to check the claim or confirm it
        if (!isMine(highestDescendantId) && !unresponsiveTimeoutPassed(highestDescendantId)) return;

        if (trxWrapper.semiApprovedAndApprovable(toConfirm, highestDescendant)) {
            // The superblock is semi approved and it can be approved if it has enough confirmations
            logger.info("Confirming semi-approved superblock {} with descendant {}", toConfirmId, highestDescendantId);
            claimContractApi.confirmClaim(toConfirmId, highestDescendantId);
        }
        else if (trxWrapper.newAndTimeoutPassed(highestDescendantId) || claimContractApi.getInBattleAndSemiApprovable(highestDescendantId)) {
            // Either the superblock is unchallenged or it won all the battles;
            // it will get approved or semi-approved depending on the situation
            // (look at VircleClaimManager contract source code for more details)
            logger.info("Confirming superblock {}", highestDescendantId);
            claimContractApi.checkClaimFinished(highestDescendantId, false);
        // we need submitter to timeout or finish a challenge if parties involved in challenges dissappear, we ensure that unresponsiveTimeoutPassed time has passed before submitters complete or timeout challenges on behalf of those involved
        } else if(unresponsiveTimeoutPassed(toConfirmId) && claimContractApi.getClaimExists(toConfirmId) && !claimContractApi.getClaimDecided(toConfirmId) && battleContractApi.sessionExists(toConfirmId) && battleContractApi.getSubmitterHitTimeout(toConfirmId)){
            if(claimContractApi.getClaimInvalid(toConfirmId)) {
                logger.info("Superblock {} was invalid. Submitter superblock was rejected but challenger not around. Invalidating.", toConfirmId);
                claimContractApi.checkClaimFinished(toConfirmId, false);
            }
            else{
                logger.info("Superblock {} was invalid. Submitter did not respond in time and challenger not around. Timing out.", toConfirmId);
                trxWrapper.timeout(toConfirmId, battleContractApi.getBattleManagerForChallenges());
            }
        }

    }



    /* - Reacting to events - */

    /**
     * Filters battles where this defender defended the superblock, won
     * and deletes them from active battle set and pays out the deposit if configuration is set to do so
     * @param fromBlock
     * @param toBlock
     * @throws Exception
     */
    protected void submitterWonBattles(long fromBlock, long toBlock) throws Exception {
        List<SuperblockSuccessfulEvent> events = claimContractApi.getSuperblockClaimSuccessfulEvents(fromBlock, toBlock);
        boolean removeFromContract = false;
        for (SuperblockSuccessfulEvent event : events) {
            if (isMine(event)) {
                if (sessionToSuperblockMap.contains(event.superblockHash)) {
                    logger.info("Submitter won battle on superblock {} process counter {}",
                            event.superblockHash, event.processCounter.getValue().intValue());
                    sessionToSuperblockMap.remove(event.superblockHash);
                    removeFromContract = true;
                }
            }
        }
        if (removeFromContract && config.isWithdrawFundsEnabled()) {
            claimContractApi.withdrawAllFundsExceptLimit(AgentRole.SUBMITTER, myAddress);
        }
    }

    private void respondToNewBattles(long fromBlock, long toBlock) throws Exception {
        List<NewBattleEvent> queryBattleEvents = battleContractApi.getNewBattleEvents(fromBlock, toBlock);

        for (NewBattleEvent queryBattleEvent : queryBattleEvents) {
            if (isMyBattleEvent(queryBattleEvent) && battleContractApi.sessionExists(queryBattleEvent.superblockHash)) {
                logger.info("Battle detected for superblock {}. Responding now with first set of headers.", queryBattleEvent.superblockHash);
                trxWrapper.respondBlockHeaders(queryBattleEvent.superblockHash, 0);
            }
        }
    }

    private boolean unresponsiveTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        double delay = superblockTimeout.floatValue() * randomizationCounter.getValue();
        int timeout = superblockTimeout.intValue() + (int)delay;
        return claimContractApi.getNewEventTimestampDate(superblockId).before(SuperblockUtils.getNSecondsAgo(timeout));
    }

    private boolean isMine(TrxWrapper.RespondHeadersEvent respondHeadersEvent) {
        return respondHeadersEvent.submitter.equalsIgnoreCase(myAddress);
    }

    private boolean isMine(SuperblockSuccessfulEvent superblockSuccessfulEvent) {
        return superblockSuccessfulEvent.submitter.equalsIgnoreCase(myAddress);
    }
    @Override
    protected void restoreFiles() throws ClassNotFoundException, IOException {
        latestEthBlockProcessed = persistentFileStore.restore(latestEthBlockProcessed, latestEthBlockProcessedFile);
        sessionToSuperblockMap = persistentFileStore.restore(sessionToSuperblockMap, sessionToSuperblockMapFile);
    }

    @Override
    protected void flushFiles() throws IOException {
        persistentFileStore.flush(latestEthBlockProcessed, latestEthBlockProcessedFile);
        persistentFileStore.flush(sessionToSuperblockMap, sessionToSuperblockMapFile);
    }

}
