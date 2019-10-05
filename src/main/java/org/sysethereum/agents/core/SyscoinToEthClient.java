/*
 * Copyright (C) 2017 RSK Labs Ltd.
 * Copyright (C) 2018 Coinfabrik and Oscar Guindzberg.
 * Copyright (C) 2019 Jagdeep Sidhu
 */
package org.sysethereum.agents.core;


import org.sysethereum.agents.constants.EthAddresses;
import org.sysethereum.agents.core.bridge.ClaimContractApi;
import org.sysethereum.agents.core.bridge.Superblock;
import org.sysethereum.agents.core.bridge.SuperblockContractApi;
import org.sysethereum.agents.core.eth.SPVProof;
import org.sysethereum.agents.core.syscoin.*;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.*;
import org.sysethereum.agents.constants.AgentConstants;
import org.sysethereum.agents.constants.SystemProperties;
import org.sysethereum.agents.core.syscoin.SyscoinWrapper;
import org.sysethereum.agents.core.eth.EthWrapper;
import org.sysethereum.agents.util.RestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.PreDestroy;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.sysethereum.agents.constants.AgentRole.SUBMITTER;

/**
 * Manages the process of informing Sysethereum Contracts news about the syscoin blockchain
 * @author Oscar Guindzberg
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SyscoinToEthClient")
public class SyscoinToEthClient {
    private static final Logger logger = LoggerFactory.getLogger("SyscoinToEthClient");

    private final EthWrapper ethWrapper;
    private final SyscoinWrapper syscoinWrapper;
    private final SuperblockChain superblockChain;
    private final SuperblockContractApi superblockContractApi;
    private final ClaimContractApi claimContractApi;
    private final EthAddresses ethAddresses;

    private final SystemProperties config;
    private final AgentConstants agentConstants;
    private final Timer timer;

    public SyscoinToEthClient(
            SystemProperties systemProperties,
            AgentConstants agentConstants,
            SuperblockChain superblockChain,
            SyscoinWrapper syscoinWrapper,
            EthWrapper ethWrapper,
            SuperblockContractApi superblockContractApi,
            ClaimContractApi claimContractApi,
            EthAddresses ethAddresses
    ) {
        this.config = systemProperties;
        this.agentConstants = agentConstants;
        this.superblockChain = superblockChain;
        this.syscoinWrapper = syscoinWrapper;
        this.ethWrapper = ethWrapper;
        this.superblockContractApi = superblockContractApi;
        this.claimContractApi = claimContractApi;
        this.ethAddresses = ethAddresses;
        this.timer = new Timer("Syscoin to Eth client", true);
    }

    public boolean setup() {
        if (config.isAgentRoleEnabled(SUBMITTER)) {
            try {
                timer.scheduleAtFixedRate(
                        new SyscoinToEthClientTimerTask(),
                        20_000, // 20 seconds
                        agentConstants.getSyscoinToEthTimerTaskPeriod()
                );
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    @PreDestroy
    public void cleanUp() {
        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");
    }

    private class SyscoinToEthClientTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                if (!ethWrapper.isEthNodeSyncing()) {
                    logger.debug("SyscoinToEthClientTimerTask");
                    ethWrapper.updateContractFacadesGasPrice();
                    if (config.isAgentRoleEnabled(SUBMITTER)) {
                        updateBridgeSuperblockChain();
                    }
                } else {
                    logger.warn("SyscoinToEthClientTimerTask skipped because the eth node is syncing blocks");
                }
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }

    /**
     * Updates bridge with all the superblocks that the agent has but the bridge doesn't.
     * @return Number of superblocks sent to the bridge.
     * @throws Exception
     */
    @SuppressWarnings("UnusedReturnValue")
    public long updateBridgeSuperblockChain() throws Exception {
        if (ethWrapper.arePendingTransactionsForSendSuperblocksAddress()) {
            logger.debug("Skipping sending superblocks, there are pending transaction for the sender address.");
            return 0;
        }
        Keccak256Hash bestSuperblockId = superblockContractApi.getBestSuperblockId();
        checkNotNull(bestSuperblockId, "No best chain superblock found");
        logger.debug("Best superblock {}.", bestSuperblockId);

        Keccak256Hash highestDescendantId ;
        Superblock highestDescendant = ethWrapper.getHighestSemiApprovedOrApprovedDescendant(bestSuperblockId);
        if (highestDescendant == null) {
            highestDescendantId = bestSuperblockId;
        }
        else
            highestDescendantId = highestDescendant.getSuperblockId();

        Superblock toConfirm = superblockChain.getFirstDescendant(highestDescendantId);
        if (toConfirm == null) {
            logger.info("Best superblock from contracts, {}, not found in local database. Stopping.", highestDescendantId);
            return 0;
        }

        if (!superblockChain.sendingTimePassed(toConfirm) || !claimContractApi.getAbilityToProposeNextSuperblock()) {
            logger.debug("Too early to send superblock {}, will try again in a few seconds.",
                    toConfirm.getSuperblockId());
            return 0;
        }

        if(!ethWrapper.sendStoreSuperblock(toConfirm, ethAddresses.generalPurposeAndSendSuperblocksAddress)){
            return 0;
        }

        return toConfirm.getSuperblockHeight();
    }

    /**
     * Relays all unprocessed transactions to Ethereum contracts by calling sendRelayTx.
     * @throws Exception
     */
    public Object getSuperblockSPVProof(Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {

            StoredBlock txStoredBlock;
            if(blockHash != null)
                txStoredBlock  = syscoinWrapper.getBlock(blockHash);
            else
                txStoredBlock  = syscoinWrapper.getStoredBlockAtHeight(height);
            if (txStoredBlock == null) {
                return new RestError("Block has not been stored in local database. Block hash: " + blockHash);
            }
            Superblock txSuperblock = superblockChain.findBySysBlockHash(txStoredBlock.getHeader().getHash());

            if (txSuperblock == null) {
                return new RestError("Superblock has not been stored in local database yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash());
            }

            if (!superblockContractApi.isApproved(txSuperblock.getSuperblockId())) {
                return new RestError("Superblock has not been approved yet. " +
                        "Block hash: " + txStoredBlock.getHeader().getHash() + ", superblock ID: " + txSuperblock.getSuperblockId());
            }

            int syscoinBlockIndex = txSuperblock.getSyscoinBlockLeafIndex(txStoredBlock.getHeader().getHash());
            byte[] includeBits = new byte[(int) Math.ceil(txSuperblock.getSyscoinBlockHashes().size() / 8.0)];
            Utils.setBitLE(includeBits, syscoinBlockIndex);
            SuperblockPartialMerkleTree superblockPMT = SuperblockPartialMerkleTree.buildFromLeaves(agentConstants.getSyscoinParams(),
                    includeBits, txSuperblock.getSyscoinBlockHashes());

            return getSuperblockSPVProof((AltcoinBlock) txStoredBlock.getHeader(), txSuperblock, superblockPMT);
        }
    }

    /**
     * Returns an SPV Proof to the superblock for a Syscoin transaction to Sysethereum contracts.
     * @param syscoinBlock Syscoin block that the transaction is in.
     * @param pmt Partial Merkle tree for constructing an SPV proof
     *                      of the Syscoin block's existence in the superblock.
     * @throws Exception
     */
    public Object getSuperblockSPVProof(AltcoinBlock syscoinBlock, Superblock superblock, SuperblockPartialMerkleTree pmt) {

        // Construct SPV proof for block
        int syscoinBlockIndex = pmt.getTransactionIndex(syscoinBlock.getHash());
        List<String> siblings = pmt.getTransactionPath(syscoinBlock.getHash())
                .stream().map(Sha256Hash::toString).collect(toList());

        return new SPVProof(syscoinBlockIndex, siblings, superblock.getSuperblockId().toString());
    }

    private static class SuperBlockResponse {
        public final String merkleRoot;
        public final long lastSyscoinBlockTime;
        public final long lastSyscoinBlockMedianTime;
        public final String lastSyscoinBlockHash;
        public final long lastSyscoinBlockBits;
        public final String parentId;
        public final String superblockId;
        public final long superblockHeight;
        public final boolean approved;

        public SuperBlockResponse(Superblock sbIn, boolean approvedIn) {
            this.merkleRoot = sbIn.getMerkleRoot().toString();
            this.lastSyscoinBlockTime = sbIn.getLastSyscoinBlockTime();
            this.lastSyscoinBlockMedianTime = sbIn.getLastSyscoinBlockMedianTime();
            this.lastSyscoinBlockHash = sbIn.getLastSyscoinBlockHash().toString();
            this.lastSyscoinBlockBits = sbIn.getlastSyscoinBlockBits();
            this.parentId = sbIn.getParentId().toString();
            this.superblockId = sbIn.getSuperblockId().toString();
            this.superblockHeight = sbIn.getSuperblockHeight();
            this.approved = approvedIn;
        }
    }

    public Object getSuperblock(@Nullable Keccak256Hash superblockId, int height) throws Exception {
        synchronized (this) {
            Superblock sb;

            if (superblockId != null) {
                sb = superblockChain.getSuperblock(superblockId);
            } else {
                sb = superblockChain.getByHeight(height);
            }

            return handleSuperblock(sb);
        }
    }

    public Object getSuperblockBySyscoinBlock(@Nullable Sha256Hash blockHash, int height) throws Exception {
        synchronized (this) {
            StoredBlock sb;

            if (blockHash != null) {
                sb = syscoinWrapper.getBlock(blockHash);
            } else {
                sb = syscoinWrapper.getStoredBlockAtHeight(height);
            }

            if (sb == null) {
                return new RestError("Block has not been stored in local database.");
            }

            Superblock txSuperblock = superblockChain.findBySysBlockHash(sb.getHeader().getHash());

            return handleSuperblock(txSuperblock);
        }
    }

    private Object handleSuperblock(@Nullable Superblock sb) throws Exception {
        if (sb == null) {
            return new RestError("Superblock has not been stored in local database yet.");
        }
        return new SuperBlockResponse(sb, superblockContractApi.isApproved(sb.getSuperblockId()));
    }

}