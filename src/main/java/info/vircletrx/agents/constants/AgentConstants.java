package info.vircletrx.agents.constants;

import org.libdohj.params.AbstractVircleParams;
import info.vircletrx.agents.core.bridge.SuperblockData;

import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;

/**
 * Agent and Bridge constants.
 * Subclasses are customizations for each network
 */
public class AgentConstants {

    protected final AbstractVircleParams vircleParams;

    protected final long vircleToTrxTimerTaskPeriod;
    protected final long vircleToTrxTimerTaskPeriodAggressive;
    // Minimum number of confirmations a tx has to have in order to EVALUATE relaying it to trx
    protected final SuperblockData genesisSuperblock;
    protected final long defenderTimerTaskPeriod;
    protected final long challengerTimerTaskPeriod;
    protected final long defenderConfirmations;
    protected final long challengerConfirmations;

    protected final String networkId;

    public AgentConstants(
            AbstractVircleParams vircleParams,
            long vircleToTrxTimerTaskPeriod,
            long vircleToTrxTimerTaskPeriodAggressive,
            SuperblockData genesisSuperblock,
            long defenderTimerTaskPeriod,
            long challengerTimerTaskPeriod,
            long defenderConfirmations,
            long challengerConfirmations,
            String networkId
    ) {
        this.vircleParams = vircleParams;
        this.vircleToTrxTimerTaskPeriod = vircleToTrxTimerTaskPeriod;
        this.vircleToTrxTimerTaskPeriodAggressive = vircleToTrxTimerTaskPeriodAggressive;
        this.genesisSuperblock = genesisSuperblock;
        this.defenderTimerTaskPeriod = defenderTimerTaskPeriod;
        this.challengerTimerTaskPeriod = challengerTimerTaskPeriod;
        this.defenderConfirmations = defenderConfirmations;
        this.challengerConfirmations = challengerConfirmations;
        this.networkId = networkId;
    }

    public AbstractVircleParams getVircleParams() {
        return vircleParams;
    }

    public long getVircleToTrxTimerTaskPeriod() {
        return vircleToTrxTimerTaskPeriod;
    }
    public long getVircleToTrxTimerTaskPeriodAggressive() {
        return vircleToTrxTimerTaskPeriodAggressive;
    }
    public SuperblockData getGenesisSuperblock() {
        return genesisSuperblock;
    }

    /**
     * @param agentRole
     * @return time in seconds
     */
    public long getTimerTaskPeriod(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challengerTimerTaskPeriod : defenderTimerTaskPeriod;
    }
    public long getConfirmations(AgentRole agentRole) {
        return agentRole == CHALLENGER ? challengerConfirmations : defenderConfirmations;
    }

    public String getNetworkId() {
        return networkId;
    }


}
