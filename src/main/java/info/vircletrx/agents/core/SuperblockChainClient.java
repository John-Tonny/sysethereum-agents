package info.vircletrx.agents.core;

import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Sha256Hash;
import org.bitcoinj.store.BlockStoreException;
import info.vircletrx.agents.constants.AgentConstants;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.core.vircle.VircleWrapper;
import info.vircletrx.agents.core.bridge.Superblock;
import info.vircletrx.agents.core.vircle.SuperblockChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;
import static info.vircletrx.agents.constants.AgentRole.SUBMITTER;

/**
 * Runs a SuperblockChain.
 * @author Catalina Juarros
 */
@Service
@Slf4j(topic = "SuperblockChainClient")
public class SuperblockChainClient {

    private static final Logger logger = LoggerFactory.getLogger("SuperblockChainClient");

    private final SystemProperties config;
    private final AgentConstants agentConstants;
    private final SuperblockChain localSuperblockChain;
    private final VircleWrapper vircleWrapper;
    private final Timer timer;

    public SuperblockChainClient(
            SystemProperties systemProperties,
            AgentConstants agentConstants,
            SuperblockChain superblockChain,
            VircleWrapper vircleWrapper
    ) {
        this.config = systemProperties;
        this.agentConstants = agentConstants;
        this.localSuperblockChain = superblockChain;
        this.vircleWrapper = vircleWrapper;
        this.timer = new Timer("SuperblockChainClient", true);
    }

    public boolean setup() {
        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            try {
                timer.scheduleAtFixedRate(
                        new UpdateSuperblocksTimerTask(),
                        1_000,
                        agentConstants.getVircleToTrxTimerTaskPeriod()
                );
            } catch (Exception e) {
                return false;
            }
        }

        return true;
    }

    public void cleanUp() {
        timer.cancel();
        timer.purge();
        logger.info("cleanUp: Timer was canceled.");
    }

    /**
     * Builds and maintains a chain of superblocks from the whole Vircle blockchain.
     * Writes it to disk as specified by SuperblockLevelDBBlockStore.
     * @throws BlockStoreException
     * @throws IOException
     */
    public void updateChain() throws Exception, BlockStoreException, IOException {
        Superblock bestSuperblock = localSuperblockChain.getChainHead();
        Sha256Hash bestSuperblockLastBlockHash = bestSuperblock.getLastVircleBlockHash();

        // Get all the Vircle blocks that haven't yet been hashed into a superblock
        Stack<Sha256Hash> allVircleHashesToHash = vircleWrapper.getNewerHashesThan(bestSuperblockLastBlockHash);
        localSuperblockChain.storeSuperblocks(allVircleHashesToHash, bestSuperblock.getHash()); // group them in superblocks
    }


    /**
     * Task to keep superblock chain updated whenever the agent is running.
     */
    private class UpdateSuperblocksTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                logger.debug("UpdateSuperblocksTimerTask");
                updateChain();
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
            }
        }
    }
}