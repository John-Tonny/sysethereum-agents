package info.vircletrx.agents.core.bridge.battle;

import info.vircletrx.agents.core.vircle.Keccak256Hash;
import org.tron.tronj.abi.datatypes.generated.Uint256;

public class SuperblockFailedEvent {
    public Keccak256Hash superblockHash;
    public String challenger;
    public Uint256 processCounter;

    public SuperblockFailedEvent(Keccak256Hash superblockHash, String challenger, Uint256 processCounter) {
        this.superblockHash = superblockHash;
        this.challenger = challenger;
        this.processCounter = processCounter;

    }
}