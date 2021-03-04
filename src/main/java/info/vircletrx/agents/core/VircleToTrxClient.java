/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package info.vircletrx.agents.core;


import org.springframework.beans.factory.annotation.Autowired;
import info.vircletrx.agents.constants.TrxAddresses;
import info.vircletrx.agents.core.bridge.Superblock;
import info.vircletrx.agents.core.bridge.SuperblockContractApi;
import info.vircletrx.agents.core.trx.SuperblockSPVProof;
import info.vircletrx.agents.core.vircle.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import info.vircletrx.agents.constants.AgentConstants;
import info.vircletrx.agents.core.trx.TrxWrapper;
import info.vircletrx.agents.util.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.web3j.utils.Numeric;

import javax.annotation.Nullable;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

/**
 * Manages the process of informing VircleTrx Contracts news about the vircle blockchain
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "VircleToTrxClient")
public class VircleToTrxClient {
    private static final Logger logger = LoggerFactory.getLogger("VircleToTrxClient");

    private final TrxWrapper trxWrapper;
    private final VircleWrapper vircleWrapper;
    private final SuperblockChain localSuperblockChain;
    private final SuperblockContractApi superblockContractApi;
    private final TrxAddresses trxAddresses;

    private final AgentConstants agentConstants;
    private Timer timer;
    private final Context vircleContext;
    @Autowired
    public VircleToTrxClient(
            Context vircleContext,
            AgentConstants agentConstants,
            SuperblockChain superblockChain,
            VircleWrapper vircleWrapper,
            TrxWrapper trxWrapper,
            SuperblockContractApi superblockContractApi,
            TrxAddresses trxAddresses
    ) {
        this.vircleContext = vircleContext;
        this.agentConstants = agentConstants;
        this.localSuperblockChain = superblockChain;
        this.vircleWrapper = vircleWrapper;
        this.trxWrapper = trxWrapper;
        this.superblockContractApi = superblockContractApi;
        this.trxAddresses = trxAddresses;
        this.timer = new Timer("Vircle to Trx client", true);
    }
    public boolean setupTimer(){
        try {
            timer.cancel();
            timer.purge();
            timer = new Timer("Vircle to Trx client", true);
            timer.scheduleAtFixedRate(new VircleToTrxClientTimerTask(), trxWrapper.getAggressiveMode()? 0: 20_000, trxWrapper.getAggressiveMode()? agentConstants.getVircleToTrxTimerTaskPeriodAggressive(): agentConstants.getVircleToTrxTimerTaskPeriod());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public boolean setup() {
        return setupTimer();
    }

    public void cleanUp() {
        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");
    }

    private class VircleToTrxClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!trxWrapper.isTrxNodeSyncing()) {
                    logger.debug("VircleToTrxClientTimerTask");
                    trxWrapper.updateContractFacadesGasPrice();
                    updateBridgeSuperblockChain();
                } else {
                    logger.warn("VircleToTrxClientTimerTask skipped because the trx node is syncing blocks");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Updates bridge with all the superblocks that the agent has but the bridge doesn't.
     * @throws Exception
     */
    public void updateBridgeSuperblockChain() throws Exception {
        if (trxWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return;
        }
        Keccak256Hash bestSuperblockId = superblockContractApi.getBestSuperblockId();
        checkNotNull(bestSuperblockId, "No best chain superblock found");
        logger.debug("Best superblock {}.", bestSuperblockId);

        Keccak256Hash highestDescendantId ;
        Superblock highestDescendant = trxWrapper.getHighestSemiApprovedOrApprovedDescendant(bestSuperblockId);
        if (highestDescendant == null) {
            highestDescendantId = bestSuperblockId;
        }
        else
            highestDescendantId = highestDescendant.getHash();

        //highestDescendantId = bestSuperblockId;   // john
        Superblock toConfirm = localSuperblockChain.getFirstDescendant(highestDescendantId);
        if (toConfirm == null) {
            logger.info("No new superblock to submit found in local database. Last processed superblockId {}. Stopping.", highestDescendantId);
            return;
        }

        if (!localSuperblockChain.sendingTimePassed(toConfirm)) {
            logger.debug("Too early to send superblock {}, will try again in a few seconds.", toConfirm.getHash());
            return;
        }

        trxWrapper.sendStoreSuperblock(toConfirm, trxAddresses.generalPurposeAddress);
    }

    /**
     * Relays all unprocessed transactions to Tronx contracts by calling sendRelayTx.
     * @throws Exception
     */
    public Object getSuperblockSPVProof(Sha256Hash blockHash, int height, boolean isApprovedCheck) throws Exception {
        synchronized (this) {
            Context.propagate(vircleContext);
            StoredBlock txStoredBlock;
            if(blockHash != null)
                txStoredBlock  = vircleWrapper.getBlock(blockHash);
            else
                txStoredBlock  = vircleWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                return new RestError("Block has not been stored in local database. Block hash: " + blockHash);
            }

            Superblock txSuperblock = localSuperblockChain.findBySysBlockHash(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                return new RestError("Superblock has not been stored in local database yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash());
            }

            if (isApprovedCheck && !superblockContractApi.isApproved(txSuperblock.getHash())) {
                return new RestError("Superblock has not been approved yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash() + ", superblock ID: " + txSuperblock.getHash());
            }

            int vircleBlockIndex = txSuperblock.getVircleBlockLeafIndex(txStoredBlock.getHeader().getHash());
            byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getVircleBlockHashes().size() / 8.0)];
            Utils.setBitLE(includeBits, vircleBlockIndex);
            SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getVircleParams(),
                    includeBits, txSuperblock.getVircleBlockHashes());

            return getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);
        }
    }

    /**
     * Returns an SPV Proof to the superblock for a Vircle transaction to VircleTrx contracts.
     * @param vircleBlock Vircle block that the transaction is in.
     * @param pmt Partial Merkle tree for constructing an SPV proof
     *                      of the Vircle block's existence in the superblock.
     * @throws Exception
     */
    private Object getSuperblockSPVProof(AltcoinBlock vircleBlock, Superblock superblock, SuperblockPartialMerkleTree pmt) {

        // Construct SPV proof for block
        int vircleBlockIndex = pmt.getTransactionIndex(vircleBlock.getHash());
        List<String> siblings = pmt.getTransactionPath(vircleBlock.getHash())
                .stream().map(Sha256Hash::toString).collect(toList());

        return new SuperblockSPVProof(vircleBlockIndex, siblings, superblock.getHash().toString());
    }
    /**
     * Tx SPV Proof for challengeCancelBridgeTransfer
     * @throws Exception
     */
    public void fillBlockSPVProof(BlockSPVProof blockSPVProof, Sha256Hash txHash) {
        List<Sha256Hash> sha256Siblings = blockSPVProof.siblings.stream().map(Sha256Hash::wrap).collect(toList());
        byte[] includeBits = new byte[(int) Math.ceil(blockSPVProof.siblings.size() / 8.0)];
        Utils.setBitLE(includeBits, blockSPVProof.index);
        SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getVircleParams(),
                includeBits, sha256Siblings);

        blockSPVProof.siblings = superblockPMT.getTransactionPath(txHash)
                .stream().map(Sha256Hash::toString).collect(toList());

    }
    private static class SuperBlockResponse {
        public final String merkleRoot;
        public final long lastVircleBlockTime;
        public final long lastVircleBlockMedianTime;
        public final String lastVircleBlockHash;
        public final long lastVircleBlockBits;
        public final String parentId;
        public final String superblockId;
        public final long superblockHeight;
        public final boolean approved;

        public SuperBlockResponse(Superblock sbIn, boolean approvedIn) {
            this.merkleRoot = sbIn.getMerkleRoot().toString();
            this.lastVircleBlockTime = sbIn.getLastVircleBlockTime();
            this.lastVircleBlockMedianTime = sbIn.getLastVircleBlockMedianTime();
            this.lastVircleBlockHash = sbIn.getLastVircleBlockHash().toString();
            this.lastVircleBlockBits = sbIn.getlastVircleBlockBits();
            this.parentId = sbIn.getParentId().toString();
            this.superblockId = sbIn.getHash().toString();
            this.superblockHeight = sbIn.getHeight();
            this.approved = approvedIn;
        }
    }

    public Object getSuperblock(@Nullable Keccak256Hash superblockId, int height) throws Exception {
        synchronized (this) {
            Superblock sb;

            if (superblockId != null) {
                sb = localSuperblockChain.getByHash(superblockId);
            } else {
                sb = localSuperblockChain.getByHeight(height);
            }

            return handleSuperblock(sb);
        }
    }

    public Object getSuperblockByVircleBlock(@Nullable Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {
            Context.propagate(vircleContext);
            StoredBlock sb;

            if (blockHash != null) {
                sb = vircleWrapper.getBlock(blockHash);
            } else {
                sb = vircleWrapper.getStoredBlockAtHeight(height);
            }

            if (sb == null) {
                return new RestError("Block has not been stored in local database.");
            }

            Superblock txSuperblock = localSuperblockChain.findBySysBlockHash(sb.getHeader().getHash());

            return handleSuperblock(txSuperblock);
        }
    }

    private Object handleSuperblock(@Nullable Superblock sb) throws Exception {
        if (sb == null) {
            return new RestError("Superblock has not been stored in local database yet.");
        }
        return new SuperBlockResponse(sb, superblockContractApi.isApproved(sb.getHash()));
    }

}