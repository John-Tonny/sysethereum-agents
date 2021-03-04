package info.vircletrx.agents.tool;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.core.StoredBlock;
import org.bitcoinj.store.BlockStoreException;
import org.libdohj.params.AbstractVircleParams;
import info.vircletrx.agents.MainLifecycle;
import info.vircletrx.agents.constants.AgentConstants;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.core.bridge.SuperblockData;
import info.vircletrx.agents.core.bridge.SuperblockFactory;
import info.vircletrx.agents.core.vircle.VircleWrapper;
import info.vircletrx.agents.core.vircle.Keccak256Hash;
import info.vircletrx.agents.core.bridge.Superblock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.bitcoinj.core.Utils.HEX;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import info.vircletrx.agents.service.rest.MerkleRootComputer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Tool to create a genesis superblock
 * @author Catalina Juarros
 * @author Oscar Guindzberg
 */
@Configuration
@ComponentScan
@Slf4j(topic = "GenesisSuperblockGeneratorMain")
public class GenesisSuperblockGeneratorMain {
    private static final Logger logger = LoggerFactory.getLogger("GenesisSuperblockGeneratorMain");
    private static final String BASE_DIR = "/yourPath/vircletrx-agents";
    private static final String SUB_DIR = "/src/main/java/org/vircletrx/agents/tool";

    public static void main(String[] args) throws Exception {
        // Instantiate the spring context
        AnnotationConfigApplicationContext c = new AnnotationConfigApplicationContext();
        c.registerShutdownHook();

        SystemProperties config = c.getBean(SystemProperties.class);
        logger.info("Running GenesisSuperblockGeneratorMain version: {}-{}", config.projectVersion(), config.projectVersionModifier());

        c.register(VircleWrapper.class);
        c.refresh();

        var lifecycle = c.getBean(MainLifecycle.class);
        lifecycle.initialize();

        VircleWrapper vircleWrapper = c.getBean(VircleWrapper.class);
        AgentConstants agentConstants = c.getBean(AgentConstants.class);
        SuperblockFactory superblockFactory = c.getBean(SuperblockFactory.class);

        SuperblockData data = getGenesisSuperblock(agentConstants, vircleWrapper);
        Superblock s = superblockFactory.fromData(data);

        System.out.println(s);
    }

    private static SuperblockData getGenesisSuperblock(AgentConstants agentConstants, VircleWrapper vircleWrapper) throws IOException, BlockStoreException {
        AbstractVircleParams params = agentConstants.getVircleParams();

        BufferedReader reader = new BufferedReader(
                new FileReader(BASE_DIR + SUB_DIR + "/virclemain-2309215-to-2309216"));
        List<Sha256Hash> vircleBlockHashes = parseBlockHashes(reader);
        Keccak256Hash genesisParentHash = Keccak256Hash.wrap(new byte[32]); // initialised with 0s
        StoredBlock lastVircleBlock = vircleWrapper.getBlock(vircleBlockHashes.get(vircleBlockHashes.size() - 1));

        return new SuperblockData(
                MerkleRootComputer.computeMerkleRoot(params, vircleBlockHashes),
                vircleBlockHashes,
                lastVircleBlock.getHeader().getTimeSeconds(),
                vircleWrapper.getMedianTimestamp(lastVircleBlock),
                0,
                genesisParentHash,
                0);
    }

    private static List<Sha256Hash> parseBlockHashes(BufferedReader reader) throws IOException {
        List<Sha256Hash> result = new ArrayList<>();
        String line = reader.readLine();
        while (line != null) {
            byte[] rawBytes = HEX.decode(line);
            result.add(Sha256Hash.wrap(rawBytes));
            line = reader.readLine();
        }
        return result;
    }
}