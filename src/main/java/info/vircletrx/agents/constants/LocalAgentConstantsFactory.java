package info.vircletrx.agents.constants;

import org.bitcoinj.core.Sha256Hash;
import info.vircletrx.agents.core.bridge.SuperblockData;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import org.libdohj.params.VircleRegTestParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import info.vircletrx.agents.service.rest.MerkleRootComputer;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for local tests.
 * Uses Vircle RegTest and Trx Ganache.
 */
public class LocalAgentConstantsFactory {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("LocalAgentConstants");

    public LocalAgentConstantsFactory() {
    }

    public AgentConstants create() {
        var vircleParams = VircleRegTestParams.get();

        var vircleToTrxTimerTaskPeriod = 45 * 1000;
        var vircleToTrxTimerTaskPeriodAggressive = 5 * 1000;
        List<Sha256Hash> sysHashes = List.of(vircleParams.getGenesisBlock().getHash());

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(vircleParams, sysHashes),
                sysHashes,
                vircleParams.getGenesisBlock().getTimeSeconds(),0,0,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );
        var defenderTimerTaskPeriod = 60 * 1000;
        var challengerTimerTaskPeriod = 60 * 1000;
        var defenderConfirmations = 1;
        var challengerConfirmations = 1;

        var networkId = "32001"; // local eth network

        return new AgentConstants(
                vircleParams,
                vircleToTrxTimerTaskPeriod,
                vircleToTrxTimerTaskPeriodAggressive,
                genesisSuperblock,
                defenderTimerTaskPeriod,
                challengerTimerTaskPeriod,
                defenderConfirmations,
                challengerConfirmations,
                networkId
        );
    }
}
