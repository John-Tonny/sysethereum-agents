package info.vircletrx.agents.core.bridge.battle;

import info.vircletrx.agents.constants.AgentRole;
import info.vircletrx.agents.core.vircle.Keccak256Hash;

import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;

public class NewBattleEvent {
    public Keccak256Hash superblockHash;
    public String submitter;
    public String challenger;

    public String getAddressByRole(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challenger : submitter;
    }
}
