package info.vircletrx.agents.constants;

import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.VircleTestNet3Params;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import info.vircletrx.agents.core.bridge.SuperblockData;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import info.vircletrx.agents.service.rest.MerkleRootComputer;

import java.math.BigInteger;
import java.util.List;

/**
 * AgentConstants for integration tests.
 * Uses Vircle Mainnet and Trx Shasta.
 * Vircle Mainnet is used for testing because Vircle testnet is hard to use and mainnet vircles are not that expensive.
 */
public class IntegrationAgentConstantsFactory {

    private static final Logger logger = LoggerFactory.getLogger("IntegrationAgentConstants");

    public IntegrationAgentConstantsFactory() {
    }

    public AgentConstants create() {
        var vircleParams = VircleTestNet3Params.get();

        var vircleToTrxTimerTaskPeriod = 45 * 1000;
        var vircleToTrxTimerTaskPeriodAggressive = 5 * 1000;

        List<Sha256Hash> sysHashes = List.of(Sha256Hash.wrap("00000eab5895cca2b698efe3c9cdb09259fdb7464500500f78d70df3f640f469"));

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(vircleParams, sysHashes),
                sysHashes,
                1611979096, 1611979094, 380734,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );

        var defenderTimerTaskPeriod = 60 * 1000;
        var challengerTimerTaskPeriod = 60 * 1000;
        var defenderConfirmations = 2;
        var challengerConfirmations = 2;

        var networkId = "4"; // eth rinkeby 4; eth mainnet 1

        logger.info("genesisSuperblock " + genesisSuperblock.toString());

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
