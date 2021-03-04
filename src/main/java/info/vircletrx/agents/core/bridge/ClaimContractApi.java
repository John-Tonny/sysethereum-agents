package info.vircletrx.agents.core.bridge;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.constants.AgentRole;
import info.vircletrx.agents.constants.TrxAddresses;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.contract.VircleClaimManager;
import info.vircletrx.agents.contract.VircleClaimManagerExtended;
import info.vircletrx.agents.core.bridge.battle.SuperblockFailedEvent;
import info.vircletrx.agents.core.bridge.battle.SuperblockSuccessfulEvent;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import info.vircletrx.agents.core.vircle.SuperblockUtils;
import org.tron.tronj.abi.datatypes.Address;
import org.tron.tronj.abi.datatypes.generated.Bytes32;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;
import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;
import static info.vircletrx.agents.constants.AgentRole.SUBMITTER;

import info.vircletrx.agents.addition.DefaultBlockParameter;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.abi.datatypes.generated.Uint32;
import org.tron.tronj.proto.Response.TransactionReturn;


@Service
public class ClaimContractApi {

    private static final Logger logger = LoggerFactory.getLogger("ClaimContractApi");

    private final SystemProperties config;
    private final TrxAddresses trxAddresses;
    private final BigInteger minProposalDeposit;
    private final BigInteger superblockTimeout;
    private final VircleClaimManagerExtended claimManager;
    private final VircleClaimManagerExtended claimManagerForChallenges;

    public ClaimContractApi(
            SystemProperties config,
            TrxAddresses trxAddresses,
            BigInteger minProposalDeposit,
            BigInteger superblockTimeout,
            VircleClaimManagerExtended claimManager,
            VircleClaimManagerExtended claimManagerForChallenges
    ) {
        this.config = config;
        this.trxAddresses = trxAddresses;
        this.minProposalDeposit = minProposalDeposit;
        this.superblockTimeout = superblockTimeout;
        this.claimManager = claimManager;
        this.claimManagerForChallenges = claimManagerForChallenges;
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
    }

    /**
     * Looks up a superblock's submission time in VircleClaimManager.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public Date getNewEventTimestampDate(Keccak256Hash superblockId) throws Exception {
        return new Date(getNewEventTimestampBigInteger(superblockId).longValue() * 1000);
    }

    public boolean submittedTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        Date timeoutDate = SuperblockUtils.getNSecondsAgo(superblockTimeout.intValue());

        return getNewEventTimestampDate(superblockId).before(timeoutDate);
    }

    /**
     * Looks up a superblock's submission time in VircleClaimManager.
     * @param superblockId Superblock hash.
     * @return When the superblock was submitted.
     * @throws Exception
     */
    public BigInteger getNewEventTimestampBigInteger(Keccak256Hash superblockId) throws Exception {
        return claimManager.getNewSuperblockEventTimestamp(new Bytes32(superblockId.getBytes())).getValue();
    }


    public long getSuperblockConfirmations() throws Exception {
        return claimManager.superblockConfirmations().getValue().longValue();
    }

    public Address getClaimChallenger(Keccak256Hash superblockId) throws Exception {
        return new Address(claimManager.getClaimChallenger(new Bytes32(superblockId.getBytes())).getValue());
    }

    public boolean getClaimExists(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimExists(new Bytes32(superblockId.getBytes())).getValue();
    }

    public String getClaimSubmitter(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimSubmitter(new Bytes32(superblockId.getBytes())).getValue();
    }

    public boolean getClaimDecided(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimDecided(new Bytes32(superblockId.getBytes())).getValue();
    }

    public boolean getClaimInvalid(Keccak256Hash superblockId) throws Exception {
        return claimManager.getClaimInvalid(new Bytes32(superblockId.getBytes())).getValue();
    }

    public boolean getInBattleAndSemiApprovable(Keccak256Hash superblockId) throws Exception {
        return claimManager.getInBattleAndSemiApprovable(new Bytes32(superblockId.getBytes())).getValue();
    }

    private BigInteger getDeposit(String account, VircleClaimManager myClaimManager) throws Exception {
        return myClaimManager.getDeposit(new Address(account)).getValue();
    }
    /**
     * Gets in process count of superblocks, this cannot be greator than 10 as submitters will not be allowed to submit, to keep
     * the amount of bonded deposits capped to 10 per every 10 minutes.
     *
     * @return BigInteger value representing the current count
     * @throws Exception
     */
    public BigInteger getProcessCounter() throws Exception {
        return claimManager.inProcessCounter().getValue();
    }

    private void withdrawDeposit(VircleClaimManager myClaimManager, BigInteger weiValue) {
        TransactionReturn futureReceipt = myClaimManager.withdrawDeposit(new Uint256(weiValue));
        logger.info("Withdrew {} wei.", weiValue);
        logger.info("withdrawDeposit receipt {}", futureReceipt.toString());
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param account Caller's address.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @throws Exception
     */
    private void withdrawAllFundsExceptLimit(String account, VircleClaimManager myClaimManager) throws Exception {
        BigInteger currentDeposit = getDeposit(account, myClaimManager);
        BigInteger limit = BigInteger.valueOf(config.depositedFundsLimit());
        if (currentDeposit.compareTo(limit) > 0) {
            withdrawDeposit(myClaimManager, currentDeposit.subtract(limit));
        }
    }

    /**
     * Withdraw deposits so that only the maximum amount of funds (as determined by user configuration)
     * is left in the contract.
     * To be called after battles or when a superblock is approved/invalidated.
     * @param agentRole Agent role
     * @param account Caller's address.
     * @throws Exception
     */
    public void withdrawAllFundsExceptLimit(AgentRole agentRole, String account) throws Exception {
        VircleClaimManager myClaimManager;
        if (agentRole == CHALLENGER) {
            myClaimManager = claimManagerForChallenges;
        } else {
            myClaimManager = claimManager;
        }

        withdrawAllFundsExceptLimit(account, myClaimManager);
    }

    /**
     * Makes the minimum necessary deposit for reaching a given amount.
     * @param account Caller's address.
     * @param weiValue Deposit to be reached. This should be the caller's total deposit in the end.
     * @throws Exception
     */
    public void makeDepositIfNeeded(AgentRole agentRole, String account, BigInteger weiValue) throws Exception {

        VircleClaimManager myClaimManager = (agentRole == SUBMITTER) ? claimManager : claimManagerForChallenges;
        BigInteger currentDeposit = getDeposit(account, myClaimManager);
        if (currentDeposit.compareTo(weiValue) < 0) {
            BigInteger diff = weiValue.subtract(currentDeposit);
            makeDeposit(myClaimManager, diff);
        }
    }

    /**
     * Makes a deposit.
     * @param weiValue Wei to be deposited.
     * @param myClaimManager this.claimManager if proposing/defending, this.claimManagerForChallenges if challenging.
     * @throws InterruptedException
     */
    private void makeDeposit(VircleClaimManager myClaimManager, BigInteger weiValue) throws InterruptedException {
        TransactionReturn futureReceipt = myClaimManager.makeDeposit(weiValue);
        logger.info("Deposited {} wei.", weiValue);

        logger.info("makeClaimDeposit receipt {}", futureReceipt.toString());
        Thread.sleep(200); // in case the transaction takes some time to complete
    }

    /**
     * Proposes a superblock to VircleClaimManager. To be called from sendStoreSuperblock.
     * @param superblock Superblock to be proposed.
     * @return
     */

    public TransactionReturn proposeSuperblock(Superblock superblock) {
        return claimManager.proposeSuperblock(
                new Bytes32(superblock.getMerkleRoot().getBytes()),
                new Uint256(superblock.getLastVircleBlockTime()),
                new Uint256(superblock.getLastVircleBlockMedianTime()),
                new Bytes32(superblock.getLastVircleBlockHash().getBytes()),
                new Uint32(superblock.getlastVircleBlockBits()),
                new Bytes32(superblock.getParentId().getBytes()));
    }


    /**
     * Approves, semi-approves or invalidates a superblock depending on its situation.
     * See VircleClaimManager source code for further reference.
     * @param superblockId Superblock to be approved, semi-approved or invalidated.
     * @param isChallenger Whether the caller is challenging. Used to determine
     *                     which VircleClaimManager should be used for withdrawing funds.
     */
    public void checkClaimFinished(Keccak256Hash superblockId, boolean isChallenger) {

        VircleClaimManagerExtended myClaimManager;
        if (isChallenger) {
            myClaimManager = claimManagerForChallenges;
        } else {
            myClaimManager = claimManager;
        }

        TransactionReturn futureReceipt =
                myClaimManager.checkClaimFinished(new Bytes32(superblockId.getBytes()));
        logger.info("checkClaimFinished receipt {}", futureReceipt.toString());
    }

    /**
     * Confirms a semi-approved superblock with a high enough semi-approved descendant;
     * 'high enough' means that superblock.height - descendant.height is greater than or equal
     * to the number of confirmations necessary for appoving a superblock.
     * See VircleClaimManager source code for further reference.
     * @param superblockId Superblock to be confirmed.
     * @param descendantId Its highest semi-approved descendant.
     */
    public void confirmClaim(Keccak256Hash superblockId, Keccak256Hash descendantId) {
        TransactionReturn futureReceipt =
                claimManager.confirmClaim(new Bytes32(superblockId.getBytes()), new Bytes32(descendantId.getBytes()));
        logger.info("confirmClaim receipt {}", futureReceipt.toString());
    }

    /**
     * Rejects a claim.
     * See VircleClaimManager source code for further reference.
     * @param superblockId ID of superblock to be rejected.
     */
    public void rejectClaim(Keccak256Hash superblockId) {
        TransactionReturn futureReceipt =
                claimManagerForChallenges.rejectClaim(new Bytes32(superblockId.getBytes()));
        logger.info("rejectClaim receipt {}", futureReceipt.toString());
    }

    /**
     * Challenges a superblock.
     * @param superblockId Hash of superblock to be challenged.
     * @throws Exception
     */
    public void challengeSuperblock(Keccak256Hash superblockId) throws Exception {
        if(!getClaimExists(superblockId) || getClaimDecided(superblockId) || getClaimInvalid(superblockId)) {
            logger.info("Superblock has already been decided upon or claim doesn't exist, skipping...{}", superblockId.toString());
            return;
        }

        if(getClaimSubmitter(superblockId).equalsIgnoreCase(trxAddresses.challengerAddress)){
            logger.info("You cannot challenge a superblock you have submitted yourself, skipping...{}", superblockId.toString());
            return;
        }

        // Make necessary deposit to cover reward
        // Note: initial deposit for challenging a superblock, just a best guess based on
        //       60 requests max for block headers and the final verify superblock cost
        makeDepositIfNeeded(CHALLENGER, trxAddresses.challengerAddress, minProposalDeposit);

        TransactionReturn futureReceipt =
                claimManagerForChallenges.challengeSuperblock(new Bytes32(superblockId.getBytes()));
        logger.info("challengeSuperblock receipt {}", futureReceipt.toString());

    }

    /**
     * Listens to SuperblockClaimSuccessful events from a given VircleClaimManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockSuccessfulEvent objects.
     *
     * @param startBlock First Tronx block to poll.
     * @param endBlock   Last Tronx block to poll.
     * @return All SuperblockClaimSuccessful events from VircleClaimManager as SuperblockSuccessfulEvent objects.
     * @throws IOException
     */
    public List<SuperblockSuccessfulEvent> getSuperblockClaimSuccessfulEvents(long startBlock, long endBlock) throws IOException {
        List<VircleClaimManager.SuperblockClaimSuccessfulEventResponse> superblockSuccessfulEvents =
                claimManager.getSuperblockClaimSuccessfulEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        return superblockSuccessfulEvents.stream().map(response ->
                new SuperblockSuccessfulEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.submitter.getValue(),
                        response.processCounter
                )
        ).collect(toList());

    }

    /**
     * Listens to SuperblockClaimFailed events from a given VircleClaimManager contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockSuccessfulEvent objects.
     *
     * @param startBlock First Tronx block to poll.
     * @param endBlock   Last Tronx block to poll.
     * @return All SuperblockClaimFailed events from VircleClaimManager as SuperblockFailedEvent objects.
     * @throws IOException
     */
    public List<SuperblockFailedEvent> getSuperblockClaimFailedEvents(long startBlock, long endBlock) throws IOException {
        List<VircleClaimManager.SuperblockClaimFailedEventResponse> superblockFailedEvents =
                claimManagerForChallenges.getSuperblockClaimFailedEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        return superblockFailedEvents.stream().map(response ->
                new SuperblockFailedEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.challenger.getValue(),
                        response.processCounter
                )
        ).collect(toList());
    }
}
