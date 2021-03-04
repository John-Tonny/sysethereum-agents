package info.vircletrx.agents.core.bridge;

import com.google.common.io.BaseEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import info.vircletrx.agents.contract.VircleSuperblocksExtended;
import info.vircletrx.agents.core.vircle.BlockSPVProof;
import info.vircletrx.agents.core.trx.SuperblockSPVProof;
import info.vircletrx.agents.core.vircle.Keccak256Hash;

import org.tron.tronj.abi.datatypes.generated.Bytes32;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.abi.datatypes.DynamicBytes;
import org.tron.tronj.abi.datatypes.DynamicArray;

import org.tron.tronj.proto.Response.TransactionReturn;


import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class SuperblockContractApi {
    private static final Logger logger = LoggerFactory.getLogger("SuperblockContractApi");
    private final VircleSuperblocksExtended superblocks;
    private final VircleSuperblocksExtended superblocksForChallenges;

    public SuperblockContractApi(
            VircleSuperblocksExtended superblocks,
            VircleSuperblocksExtended superblocksForChallenges
    ) {
        this.superblocks = superblocks;
        this.superblocksForChallenges = superblocksForChallenges;
    }

    // private BigInteger getStatus(Keccak256Hash superblockId) throws Exception {
    public BigInteger getStatus(Keccak256Hash superblockId) throws Exception {  // john
        return superblocks.getSuperblockStatus(new Bytes32(superblockId.getBytes())).getValue();
    }

    public boolean isApproved(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_APPROVED);
    }

    public boolean isSemiApproved(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_SEMI_APPROVED);
    }

    public boolean isNew(Keccak256Hash superblockId) throws Exception {
        return getStatus(superblockId).equals(Superblock.STATUS_NEW);
    }

    public Keccak256Hash getBestSuperblockId() throws Exception {
        return Keccak256Hash.wrap(superblocks.getBestSuperblock().getValue());
    }

    public BigInteger getHeight(Keccak256Hash superblockId) throws Exception {
        return superblocks.getSuperblockHeight(new Bytes32(superblockId.getBytes())).getValue();
    }

    public BigInteger getChainHeight() throws Exception {
        return superblocks.getChainHeight().getValue();
    }

    public void updateGasPrice(BigInteger gasPriceMinimum) {
        //noinspection deprecation
    }

    public static class SuperblockEvent {
        public final Keccak256Hash superblockId;
        public final String who;

        public SuperblockEvent(Keccak256Hash superblockId, String who) {
            this.superblockId = superblockId;
            this.who = who;
        }
    }

    /**
     * Listens to NewSuperblock events from VircleSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Tronx block to poll.
     * @param endBlock Last Tronx block to poll.
     * @return All NewSuperblock events from VircleSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getNewSuperblocks(long startBlock, long endBlock) throws IOException {

        return superblocks.getNewSuperblockEvents(startBlock, endBlock)
                .stream().map(response -> new SuperblockEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.who.getValue()
                ))
                .collect(toList());
    }

    /**
     * Listens to SemiApprovedSuperblock events from VircleSuperblocks contract within a given block window
     * and parses web3j-generated instances into easier to manage SuperblockEvent objects.
     * @param startBlock First Tronx block to poll.
     * @param endBlock Last Tronx block to poll.
     * @return All SemiApprovedSuperblock events from VircleSuperblocks as SuperblockEvent objects.
     * @throws IOException
     */
    public List<SuperblockEvent> getSemiApprovedSuperblocks(long startBlock, long endBlock) throws IOException {

        return superblocks.getSemiApprovedSuperblockEvents(startBlock, endBlock)
                .stream().map(response -> new SuperblockEvent(
                        Keccak256Hash.wrap(response.superblockHash.getValue()),
                        response.who.getValue()
                ))
                .collect(toList());
    }
  
    public void challengeCancelTransfer(BlockSPVProof blockSPVProof, SuperblockSPVProof superblockSPVProof){
        List<Uint256> txSiblings = new ArrayList<>();
        for(int i =0;i<blockSPVProof.siblings.size();i++){
            txSiblings.add(i, new Uint256(new BigInteger(blockSPVProof.siblings.get(i), 16)));
        }
        List<Uint256> blockSiblings = new ArrayList<>();
        for(int i =0;i<superblockSPVProof.merklePath.size();i++){
            blockSiblings.add(i, new Uint256(new BigInteger(superblockSPVProof.merklePath.get(i), 16)));
        }
        TransactionReturn futureReceipt = superblocksForChallenges.challengeCancelTransfer(new DynamicBytes(BaseEncoding.base16().lowerCase().decode(blockSPVProof.transaction)), new Uint256(blockSPVProof.index),new DynamicArray<Uint256>(txSiblings),
                new DynamicBytes(BaseEncoding.base16().lowerCase().decode(blockSPVProof.header)), new Uint256(superblockSPVProof.index), new DynamicArray<Uint256>(blockSiblings), new Bytes32(BaseEncoding.base16().lowerCase().decode(superblockSPVProof.superBlock)));

        logger.info("challengeCancelTransfer receipt {}", futureReceipt.toString());

    }

}
