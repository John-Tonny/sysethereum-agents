package info.vircletrx.agents.core.trx;

import com.google.common.primitives.Bytes;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.bitcoinj.store.BlockStoreException;
import info.vircletrx.agents.constants.AgentRole;
import info.vircletrx.agents.constants.TrxAddresses;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.contract.*;
import info.vircletrx.agents.core.bridge.BattleContractApi;
import info.vircletrx.agents.core.bridge.ClaimContractApi;
import info.vircletrx.agents.core.bridge.Superblock;
import info.vircletrx.agents.core.bridge.SuperblockContractApi;
import info.vircletrx.agents.core.vircle.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import info.vircletrx.agents.util.JsonGasRanges;
import org.tron.tronj.abi.datatypes.DynamicBytes;
import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.client.TronClient;
import org.tron.tronj.client.exceptions.IllegalException;

import javax.annotation.Nullable;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import info.vircletrx.agents.addition.DefaultBlockParameter;
import org.tron.tronj.proto.Response.TransactionReturn;

/**
 *
 * Helps the agent communication with the Eth blockchain.
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Component
@Slf4j(topic = "TrxWrapper")
public class TrxWrapper {

    private static final Logger logger = LoggerFactory.getLogger("TrxWrapper");

    private TronClient client;
    private final TrxAddresses trxAddress;

    // Extensions of contracts generated automatically by web3j
    private final VircleBattleManagerExtended battleManager;
    private final VircleBattleManagerExtended battleManagerForChallenges;

    private BigInteger gasPriceMinimum;
    private final BigInteger gasPriceMaximum;

    private final SuperblockContractApi superblockContractApi;
    private final BattleContractApi battleContractApi;
    private final ClaimContractApi claimContractApi;
    private final BigInteger superblockDuration;

    private final BigInteger minProposalDeposit;
    private final SuperblockChain localSuperblockChain;
    private final VircleWrapper vircleWrapper;
    private final JsonGasRanges jsonGasRanges;
    private final Context vircleContext;
    private volatile boolean bAggressiveMode;
  
    @Autowired
    public TrxWrapper(
            Context vircleContext,
            SystemProperties config,
            SuperblockChain superblockChain,
            VircleWrapper vircleWrapper,
            TronClient client,
            TrxAddresses trxAddress,
            VircleBattleManagerExtended battleManager,
            VircleBattleManagerExtended battleManagerForChallenges,
            SuperblockContractApi superblockContractApi,
            BattleContractApi battleContractApi,
            ClaimContractApi claimContractApi,
            BigInteger superblockDuration,
            BigInteger minProposalDeposit,
            JsonGasRanges jsonGasRanges
    ) throws Exception {
        this.vircleContext = vircleContext;
        this.localSuperblockChain = superblockChain;
        this.vircleWrapper = vircleWrapper;
        this.client = client;
        this.trxAddress = trxAddress;
        this.battleManager = battleManager;
        this.battleManagerForChallenges = battleManagerForChallenges;
        this.superblockContractApi = superblockContractApi;
        this.battleContractApi = battleContractApi;
        this.claimContractApi = claimContractApi;
        this.superblockDuration = superblockDuration;
        this.minProposalDeposit = minProposalDeposit;

        this.gasPriceMinimum = BigInteger.valueOf(config.gasPriceMinimum());
        this.gasPriceMaximum = config.gasPriceMaximum() == 0? BigInteger.ZERO: BigInteger.valueOf(config.gasPriceMaximum());

        this.jsonGasRanges = jsonGasRanges;

        updateContractFacadesGasPrice();
    }
    public void setAggressiveMode(boolean bMode){
        bAggressiveMode = bMode;
    }
    public boolean getAggressiveMode(){
        return bAggressiveMode;
    }

    /**
     * Returns height of the Tronx blockchain.
     * @return Tronx block count.
     * @throws IOException
     */
    public long getTrxBlockCount() throws IllegalException {
        return this.client.getNowBlock().getBlockHeader().getRawData().getNumber();
    }

    public boolean isTrxNodeSyncing() {
        return false;
    }

    public boolean arePendingTransactionsForSendSuperblocksAddress() throws InterruptedException,IOException {
        return arePendingTransactionsFor(trxAddress.generalPurposeAddress);
    }

    public boolean arePendingTransactionsForChallengerAddress() throws InterruptedException, IOException {
        return arePendingTransactionsFor(trxAddress.challengerAddress);
    }

    /**
     * Checks if there are pending transactions for a given contract.
     * @param address
     * @return
     * @throws IOException
     */
    private boolean arePendingTransactionsFor(String address) throws InterruptedException, IOException {
        /*
        BigInteger latest = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.LATEST).send().getTransactionCount();
        BigInteger pending;
        try{
            pending = web3.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        }
        catch(Exception e){
            Thread.sleep(500);
            pending = web3Secondary.ethGetTransactionCount(address, DefaultBlockParameterName.PENDING).send().getTransactionCount();
        }
        return pending.compareTo(latest) > 0;

         */
        return false;
    }
    public void updateGasForAggressiveMode(){
        BigInteger newGasPrice = gasPriceMinimum.multiply(BigInteger.TWO);
        if (!gasPriceMaximum.equals(BigInteger.ZERO) && newGasPrice.compareTo(gasPriceMaximum) > 0) {
            newGasPrice = gasPriceMaximum;
        }
        logger.info("Updating fee for aggressive mode to " + newGasPrice);
        claimContractApi.updateGasPrice(newGasPrice);
    }
    public void updateGasForNormalMode(){
        logger.info("Updating fee back to normal from aggressive mode to " + gasPriceMinimum);
        claimContractApi.updateGasPrice(gasPriceMinimum);
    }
    /**
     * Sets gas prices for all contract instances.
     * @throws IOException
     */
    public void updateContractFacadesGasPrice() throws IOException {
        BigInteger jsonGasPrice = jsonGasRanges.gasPrice();
        /*
        BigInteger suggestedGasPrice = jsonGasPrice.equals(BigInteger.ZERO)? web3.ethGasPrice().send().getGasPrice(): jsonGasPrice;
        if (suggestedGasPrice.compareTo(gasPriceMinimum) > 0) {
            if (!gasPriceMaximum.equals(BigInteger.ZERO) && suggestedGasPrice.compareTo(gasPriceMaximum) > 0) {
                suggestedGasPrice = gasPriceMaximum;
            }
            if(!gasPriceMinimum.equals(suggestedGasPrice)) {
                gasPriceMinimum = suggestedGasPrice;
                logger.info("setting new min gas price to " + gasPriceMinimum);

                claimContractApi.updateGasPrice(gasPriceMinimum);
                battleContractApi.updateGasPrice(gasPriceMinimum);
                superblockContractApi.updateGasPrice(gasPriceMinimum);
            }
        }

         */
    }

    /* ---------------------------------- */
    /* - Relay Vircle superblocks section - */
    /* ---------------------------------- */
    /**
     * Helper method for confirming a semi-approved superblock.
     * Finds the highest semi-approved or new superblock in the main chain that comes after a given semi-approved superblock.
     *
     * @param toConfirm Superblock to be confirmed.
     * @param bestContractSuperblockId Hash of the best superblock from contract.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    @Nullable
    public Superblock getHighestApprovableOrNewDescendant(Superblock toConfirm, Keccak256Hash bestContractSuperblockId)
            throws BlockStoreException, IOException, Exception {
        if (localSuperblockChain.getByHash(bestContractSuperblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestApprovableOrNewDescendant.", bestContractSuperblockId);
            return null;
        }

        //noinspection ConstantConditions
        if (localSuperblockChain.getByHash(bestContractSuperblockId).getHeight() == localSuperblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is above the tip of the chain. Returning from getHighestApprovableOrNewDescendant.", bestContractSuperblockId);
            return null;
        }

        // Superblock sb = localSuperblockChain.getByHeight(150);          // john
        Superblock sb = localSuperblockChain.getChainHead();
        while (sb != null &&
                !sb.getHash().equals(bestContractSuperblockId) &&
                !newAndTimeoutPassed(sb.getHash()) &&
                !claimContractApi.getInBattleAndSemiApprovable(sb.getHash()) &&
                !semiApprovedAndApprovable(toConfirm, sb)) {
            sb = localSuperblockChain.getByHash(sb.getParentId());
        }
        return sb;
    }
    /**
     * Helper method for confirming a semi-approved/approved superblock.
     * Finds the highest semi-approved or approved in the main chain that comes after a given superblock.
     * @param superblockId Superblock to be confirmed.
     * @return Highest superblock in main chain that's newer than the given superblock
     *         if such a superblock exists, null otherwise (i.e. given superblock isn't in main chain
     *         or has no semi-approved/approved descendants).
     * @throws BlockStoreException
     * @throws IOException
     * @throws Exception
     */
    public Superblock getHighestSemiApprovedOrApprovedDescendant(Keccak256Hash superblockId)
            throws BlockStoreException, IOException, Exception {
        if (localSuperblockChain.getByHash(superblockId) == null) {
            // The superblock isn't in the main chain.
            logger.info("Superblock {} is not in the main chain. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }

        //noinspection ConstantConditions
        if (localSuperblockChain.getByHash(superblockId).getHeight() == localSuperblockChain.getChainHeight()) {
            // There's nothing above the tip of the chain.
            logger.info("Superblock {} is the tip of the superblock chain, no descendant exists. Returning from getHighestSemiApprovedOrApprovedDescendant.", superblockId);
            return null;
        }

        // Superblock head = localSuperblockChain.getByHeight(150);  // john
        Superblock head = localSuperblockChain.getChainHead();
        BigInteger status = superblockContractApi.getStatus(head.getHash());                // john
        while (head != null
                && !head.getHash().equals(superblockId)
                && !status.equals(Superblock.STATUS_SEMI_APPROVED)                          // john
                && !status.equals(Superblock.STATUS_APPROVED)) {
                /*&& !superblockContractApi.isSemiApproved(head.getHash())
                && !superblockContractApi.isApproved(head.getHash())) {
                 */
            head = localSuperblockChain.getByHash(head.getParentId());
            status = superblockContractApi.getStatus(head.getHash());                       // john
        }

        return head;
    }
    /**
     * Proposes a superblock to VircleClaimManager in order to keep the Sysethereum contracts updated.
     * @param superblock Oldest superblock that is already stored in the local database,
     *                   but still hasn't been submitted to Sysethereum Contracts.
     * @throws Exception If superblock hash cannot be calculated.
     */
    @SuppressWarnings("UnusedReturnValue")
    public boolean sendStoreSuperblock(Superblock superblock, String account) throws Exception {
        BigInteger processCounter = claimContractApi.getProcessCounter();
        if(processCounter.compareTo(BigInteger.TEN) == 0){
            logger.info("Superblock {} not sent because process counter is at maximum allowable in progress superblocks (10)", superblock.getHash());
            return false;
        }
        // Check if the parent has been approved before sending this superblock.
        Keccak256Hash parentId = superblock.getParentId();
        if (!(superblockContractApi.isApproved(parentId) || superblockContractApi.isSemiApproved(parentId))) {
            logger.info("Superblock {} not sent because its parent was neither approved nor semi approved.", superblock.getHash());
            return false;
        }
        // if claim exists we check to ensure the superblock chain isn't "stuck" and can be re-approved to be built even if it exists
        if (claimContractApi.getClaimExists(superblock.getHash())) {
            boolean allowed1 = claimContractApi.getClaimInvalid(superblock.getHash());
            boolean allowed2 = claimContractApi.getClaimDecided(superblock.getHash());
            String hhh = claimContractApi.getClaimSubmitter(superblock.getHash());

            boolean allowed = claimContractApi.getClaimInvalid(superblock.getHash())
                    && claimContractApi.getClaimDecided(superblock.getHash())
                    && !claimContractApi.getClaimSubmitter(superblock.getHash()).equalsIgnoreCase(account);

            if (!allowed) {
                logger.info("Superblock {} has already been sent. Returning.", superblock.getHash());
                return false;
            }

            if (superblockContractApi.isApproved(parentId)) {
                if (!superblockContractApi.getBestSuperblockId().equals(parentId)) {
                    logger.info("Superblock {} parent is approved but not best. Returning.", superblock.getHash());
                    return false;
                }
            } else {
                if (!superblockContractApi.isSemiApproved(parentId)) {
                    logger.info("Superblock {} parent is neither approved nor semi-approved. Returning.", superblock.getHash());
                    return false;
                }
            }
        }


        logger.info("About to send superblock {} to the bridge.", superblock.getHash());
        Thread.sleep(500);
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return false;
        }

        // Make any necessary deposits for sending the superblock
        claimContractApi.makeDepositIfNeeded(AgentRole.SUBMITTER, account, getSuperblockDeposit());
        // if random counter is 0 it means we are in aggressive mode, increase the fees for superblock submission
        boolean bAggressiveMode = getAggressiveMode();
        if(bAggressiveMode){updateGasForAggressiveMode();}

        // The parent is either approved or semi approved. We can send the superblock.
        TransactionReturn futureReceipt = claimContractApi.proposeSuperblock(superblock);
        // reset fees back to normal mode
        if(bAggressiveMode){updateGasForNormalMode();}

        logger.info("Sent superblock {}, process counter {}", superblock.getHash(), processCounter.intValue());
        // john
        /*futureReceipt.handle((receipt, throwable) -> {
            if (receipt != null) {
                logger.info("proposeSuperblock receipt {}", receipt.toString());
            } else {
                logger.info("proposeSuperblock EXCEPTION:", throwable);
            }
            return true;
        });*/

        Thread.sleep(200);
        return true;
    }


    /**
     * Returns the initial deposit for proposing a superblock, i.e. enough to cover the challenge,
     * all battle steps and a reward for the opponent in case the battle is lost.
     * This deposit only covers one battle and it's meant to optimise the number of transactions performed
     * by the submitter - it's still necessary to make a deposit for each step if another battle is carried out
     * over the same superblock.
     * @return Initial deposit for covering a reward and a single battle.
     */
    private BigInteger getSuperblockDeposit() {
        return minProposalDeposit;
    }

    /**
     * Listens to RespondBlockHeaders events from VircleBattleManager contract within a given block window
     * and parses web3j-generated instances into easier to manage RespondBlockHeaders objects.
     * @param startBlock First Tronx block to poll.
     * @param endBlock Last Tronx block to poll.
     * @return All NewBattle events from VircleBattleManager as RespondBlockHeaders objects.
     * @throws IOException
     */
    public List<RespondHeadersEvent> getNewRespondHeadersEvents(long startBlock, long endBlock) throws IOException {
        List<RespondHeadersEvent> result = new ArrayList<>();
        List<VircleBattleManager.RespondBlockHeadersEventResponse> newBattleEvents =
                battleManagerForChallenges.getNewBlockHeadersEventResponses(
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(startBlock)),
                        DefaultBlockParameter.valueOf(BigInteger.valueOf(endBlock)));

        for (VircleBattleManager.RespondBlockHeadersEventResponse response : newBattleEvents) {
            RespondHeadersEvent newRespondHeadersEvent = new RespondHeadersEvent();
            newRespondHeadersEvent.superblockHash = Keccak256Hash.wrap(response.superblockHash.getValue());
            newRespondHeadersEvent.merkleHashCount = response.merkleHashCount.getValue().intValue();
            newRespondHeadersEvent.submitter = response.submitter.getValue();
            result.add(newRespondHeadersEvent);
        }

        return result;
    }

    public static class RespondHeadersEvent {
        public Keccak256Hash superblockHash;
        public int merkleHashCount;
        public String submitter;
    }


    /* ---------------------------------- */
    /* --------- Battle section --------- */
    /* ---------------------------------- */
    /**
     * Responds to a challenge with all block headers
     * @param superblockId Battle session superblock.
     */
    public void respondBlockHeaders(Keccak256Hash superblockId, int merkleHashCount) throws Exception {
        Context.propagate(vircleContext);
        Thread.sleep(500); // in case the transaction takes some time to complete
        if (arePendingTransactionsForSendSuperblocksAddress()) {
            throw new Exception("Skipping respondBlockHeader, there are pending transaction for the sender address.");
        }
        int numHashesRequired = merkleHashCount < 3? 16: 12;
        int startIndex = merkleHashCount*16;
        int endIndex = startIndex + numHashesRequired;
        if(startIndex > 48)
            throw new Exception("Skipping respondBlockHeader, startIndex cannot be >48.");
        Superblock superblock = localSuperblockChain.getByHash(superblockId);
        assert superblock != null;
        List<Sha256Hash> listHashes = superblock.getVircleBlockHashes();
        if(!superblockDuration.equals(BigInteger.valueOf(listHashes.size())))
            throw new Exception("Skipping respondBlockHeader, superblock hash array list is incorrect length.");

        byte[] blockHeaderBytes = null;
        for(int i = startIndex;i<endIndex;i++){
            Block altBlock = vircleWrapper.getBlock(listHashes.get(i)).getHeader().cloneAsHeader();
            byte[] serializedBytes = altBlock.bitcoinSerialize();
            if(blockHeaderBytes == null)
                blockHeaderBytes = serializedBytes;
            else
                blockHeaderBytes = Bytes.concat(blockHeaderBytes, serializedBytes);
        }

        TransactionReturn futureReceipt = battleManager.respondBlockHeaders(
                new Bytes32(superblockId.getBytes()), new DynamicBytes(blockHeaderBytes), new Uint256(numHashesRequired));
        logger.info("Responded to last block header query for Vircle superblock {}, Receipt: {}",
                superblockId, futureReceipt);
    }
    // TODO: see if the challenger should know which superblock this is


    /**
     * Calls timeout for a session where a participant hasn't responded in time, thus closing the battle.
     * @param superblockId Battle session ID.
     * @param myBattleManager VircleBattleManager contract that the caller is using to handle its battles.
     */
    public void timeout(Keccak256Hash superblockId, VircleBattleManagerExtended myBattleManager) {
        TransactionReturn futureReceipt = myBattleManager.timeout(new Bytes32(superblockId.getBytes()));
        logger.info("Called timeout for superblock {}", superblockId);
    }

    /**
     * Checks if a superblock is semi-approved and has enough confirmations, i.e. semi-approved descendants.
     * To be used after finding a descendant with getHighestApprovableOrNewDescendant.
     * @param superblock Superblock to be confirmed.
     * @param descendant Highest semi-approved descendant of superblock to be confirmed.
     * @return True if the superblock can be safely approved, false otherwise.
     * @throws Exception
     */
    public boolean semiApprovedAndApprovable(Superblock superblock, Superblock descendant) throws Exception {
        return descendant.getHeight() - superblock.getHeight() >= claimContractApi.getSuperblockConfirmations()
                && superblockContractApi.isSemiApproved(descendant.getHash())
                && superblockContractApi.isSemiApproved(superblock.getHash());
    }


    public boolean newAndTimeoutPassed(Keccak256Hash superblockId) throws Exception {
        return superblockContractApi.isNew(superblockId) && claimContractApi.submittedTimeoutPassed(superblockId);
    }
}