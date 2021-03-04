package info.vircletrx.agents.constants;

import org.bitcoinj.core.Sha256Hash;
import org.libdohj.params.VircleMainNetParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import info.vircletrx.agents.core.bridge.SuperblockData;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import info.vircletrx.agents.service.rest.MerkleRootComputer;

import java.util.List;

/**
 * AgentConstants for mainnet
 * Uses Vircle Mainnet and Trx Mainnet.
 */
public class MainnetAgentConstantsFactory {

    private static final Logger logger = LoggerFactory.getLogger("MainnetAgentConstantsFactory");

    public MainnetAgentConstantsFactory() {
    }

    public AgentConstants create() {
        var vircleParams = VircleMainNetParams.get();

        var vircleToTrxTimerTaskPeriod = 45 * 1000;
        var vircleToTrxTimerTaskPeriodAggressive = 5 * 1000;

        List<Sha256Hash> sysHashes = List.of(Sha256Hash.wrap("00000eab5895cca2b698efe3c9cdb09259fdb7464500500f78d70df3f640f469"));  //blockhash

        var genesisSuperblock = new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(vircleParams, sysHashes),
                sysHashes,
                1611979096, 1611979094, 504365055,
                Keccak256Hash.wrap(new byte[32]), // initialised with 0s
                1
        );

        var defenderTimerTaskPeriod = 60 * 1000;
        var challengerTimerTaskPeriod = 60 * 1000;
        var defenderConfirmations = 2;
        var challengerConfirmations = 2;

        var networkId = "1"; // eth rinkeby 4; eth mainnet 1

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
