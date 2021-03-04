package info.vircletrx.agents.core.bridge;

import org.bitcoinj.core.Sha256Hash;
import info.vircletrx.agents.core.vircle.Keccak256Hash;

import java.math.BigInteger;
import java.util.List;


/**
 * Constructs a superblock from a sequence of block hashes.
 *
 * @author Catalina Juarros
 */
public class Superblock {

    @SuppressWarnings("unused")
    public static final BigInteger STATUS_UNINITIALIZED = BigInteger.valueOf(0);
    public static final BigInteger STATUS_NEW = BigInteger.valueOf(1);
    @SuppressWarnings("unused")
    public static final BigInteger STATUS_IN_BATTLE = BigInteger.valueOf(2);
    public static final BigInteger STATUS_SEMI_APPROVED = BigInteger.valueOf(3);
    public static final BigInteger STATUS_APPROVED = BigInteger.valueOf(4);
    @SuppressWarnings("unused")
    public static final BigInteger STATUS_INVALID = BigInteger.valueOf(5);

    private final Keccak256Hash hash;
    public final SuperblockData data;

    /**
     * Constructs a Superblock object from a list of Vircle block hashes.
     */
    public Superblock(SuperblockData data, Keccak256Hash superblockId) {
        this.data = data;
        this.hash = superblockId;
    }

    /**
     * Accesses superblock hash attribute if already calculated, calculates it otherwise.
     * @return Superblock hash.
     */
    public Keccak256Hash getHash() {
        return hash;
    }

    /**
     * Accesses Merkle root attribute.
     * @return Superblock Merkle root.
     */
    public Sha256Hash getMerkleRoot() {
        return data.merkleRoot;
    }

    /**
     * Accesses last Vircle block time attribute.
     * @return Superblock last Vircle block time.
     */
    public long getLastVircleBlockTime() {
        return data.lastVircleBlockTime;
    }

    /**
     * Accesses last Vircle block median time attribute.
     * @return Superblock last Vircle block median time.
     */
    public long getLastVircleBlockMedianTime() {
        return data.lastVircleBlockTimeMTP;
    }

    /**
     * Accesses last Vircle block hash attribute.
     * @return Superblock last Vircle block hash.
     */
    public Sha256Hash getLastVircleBlockHash() {
        return data.lastVircleBlockHash;
    }

    /**
     * Accesses last block difficulty bits
     * @return Superblock last Vircle block bits.
     */
    public long getlastVircleBlockBits() {
        return data.lastVircleBlockBits;
    }

    /**
     * Accesses parent hash attribute.
     * @return Superblock parent hash.
     */
    public Keccak256Hash getParentId() {
        return data.parentId;
    }

    /**
     * Accesses height attribute.
     * @return Superblock height within superblock chain.
     */
    public long getHeight() {
        return data.superblockHeight;
    }

    /**
     * Accesses Vircle block hashes attribute.
     * @return Superblock Vircle block hashes.
     */
    public List<Sha256Hash> getVircleBlockHashes() {
        return data.vircleBlockHashes;
    }

    /* ---- OTHER METHODS ---- */

    /**
     * Checks whether a given Vircle block hash is part of the superblock.
     * @param hash Vircle block hash to check.
     * @return True if the block is in the superblock, false otherwise.
     */
    public boolean hasVircleBlock(Sha256Hash hash) {
        return data.hasVircleBlock(hash);
    }

    /**
     * Returns index of a given Vircle block hash in the superblock's list of hashes.
     * @param hash Vircle block hash to find.
     * @return Position of hash within the list if it's part of the superblock, -1 otherwise.
     */
    public int getVircleBlockLeafIndex(Sha256Hash hash) {
        return data.vircleBlockHashes.indexOf(hash);
    }

    @Override
    public String toString() {
        return "Superblock{" +
                "hash=" + hash +
                ", data=" + data +
                '}';
    }
}