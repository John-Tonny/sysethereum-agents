package info.vircletrx.agents.core.bridge.battle;

import info.vircletrx.agents.core.vircle.Keccak256Hash;
import org.tron.tronj.abi.datatypes.generated.Uint256;

public class SuperblockSuccessfulEvent {
    public Keccak256Hash superblockHash;
    public String submitter;
    public Uint256 processCounter;

    public SuperblockSuccessfulEvent(Keccak256Hash superblockHash, String submitter, Uint256 processCounter) {
        this.superblockHash = superblockHash;
        this.submitter = submitter;
        this.processCounter = processCounter;
    }
}
