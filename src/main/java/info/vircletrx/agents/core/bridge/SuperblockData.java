package info.vircletrx.agents.core.bridge;

import org.bitcoinj.core.Sha256Hash;
import info.vircletrx.agents.core.vircle.Keccak256Hash;

import java.util.ArrayList;
import java.util.List;

public class SuperblockData {

    // Root of a Merkle tree comprised of Vircle block hashes. 32 bytes.
    public final Sha256Hash merkleRoot;

    // Timestamp of last mined Vircle block in the superblock. 32 bytes to comply with Solidity version.
    public final long lastVircleBlockTime;

    // Median timestamp of last mined Vircle block in the superblock. 32 bytes to comply with Solidity version.
    public final long lastVircleBlockTimeMTP;

    // SHA-256 hash of last mined Vircle block in the superblock. 32 bytes.
    public final Sha256Hash lastVircleBlockHash;

    // Bits (difficulty) of last difficulty adjustment. 32 bytes.
    public final long lastVircleBlockBits;

    // SHA3-256 hash of previous superblock. 32 bytes.
    public final Keccak256Hash parentId;

    /* ---- EXTRA FIELDS ---- */
    public final long superblockHeight;
    public final List<Sha256Hash> vircleBlockHashes;

    /**
     * Constructs a Superblock object from a list of Vircle block hashes.
     * @param merkleRoot MerkleRoot computed from vircleBlockHashes parameter
     * @param vircleBlockHashes List of hashes belonging to all Vircle blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param lastVircleBlockTime Last Vircle block's timestamp.
     * @param lastVircleBlockTimeMTP Last Vircle block's median timestamp.
     * @param lastVircleBlockBits Difficulty bits of the last block in the superblock bits.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> vircleBlockHashes,
            long lastVircleBlockTime,
            long lastVircleBlockTimeMTP,
            long lastVircleBlockBits,
            Sha256Hash lastVircleBlockHash,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this.merkleRoot = merkleRoot;
        this.lastVircleBlockTime = lastVircleBlockTime;
        this.lastVircleBlockTimeMTP = lastVircleBlockTimeMTP;
        this.lastVircleBlockBits = lastVircleBlockBits;
        this.lastVircleBlockHash = lastVircleBlockHash;
        this.parentId = parentId;
        this.superblockHeight = superblockHeight;
        this.vircleBlockHashes = new ArrayList<>(vircleBlockHashes);
    }

    /**
     * Constructs a Superblock object from a list of Vircle block hashes.
     * @param merkleRoot MerkleRoot computed from vircleBlockHashes parameter
     * @param vircleBlockHashes List of hashes belonging to all Vircle blocks
     *                        mined within the one hour lapse corresponding to this superblock.
     * @param lastVircleBlockTime Last Vircle block's timestamp.
     * @param lastVircleBlockTimeMTP Last Vircle block's median timestamp.
     * @param lastVircleBlockBits Difficulty bits of the last block in the superblock bits.
     * @param parentId Previous superblock's SHA-256 hash.
     * @param superblockHeight Height of this superblock within superblock chain.
     */
    public SuperblockData(
            Sha256Hash merkleRoot,
            List<Sha256Hash> vircleBlockHashes,
            long lastVircleBlockTime,
            long lastVircleBlockTimeMTP,
            long lastVircleBlockBits,
            Keccak256Hash parentId,
            long superblockHeight
    ) {
        this(merkleRoot,
                vircleBlockHashes,
                lastVircleBlockTime,
                lastVircleBlockTimeMTP,
                lastVircleBlockBits,
                vircleBlockHashes.get(vircleBlockHashes.size() -1),
                parentId,
                superblockHeight
        );
    }

    /**
     * Checks whether a given Vircle block hash is part of the superblock.
     * @param hash Vircle block hash to check.
     * @return True if the block is in the superblock, false otherwise.
     */
    public boolean hasVircleBlock(Sha256Hash hash) {
        for (Sha256Hash h : vircleBlockHashes) {
            if (h.equals(hash))
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "SuperblockData{" +
                "merkleRoot=" + merkleRoot +
                ", lastVircleBlockTime=" + lastVircleBlockTime +
                ", lastVircleBlockTimeMTP=" + lastVircleBlockTimeMTP +
                ", lastVircleBlockHash=" + lastVircleBlockHash +
                ", lastVircleBlockBits=" + lastVircleBlockBits +
                ", parentId=" + parentId +
                ", superblockHeight=" + superblockHeight +
                '}';
    }
}
