package info.vircletrx.agents;

import okhttp3.OkHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.test.util.ReflectionTestUtils;
import info.vircletrx.agents.checker.OperatorPeersChecker;
import info.vircletrx.agents.constants.SystemProperties;
import info.vircletrx.agents.core.*;
import info.vircletrx.agents.core.vircle.SuperblockLevelDBBlockStore;
import info.vircletrx.agents.core.vircle.VircleWrapper;
import info.vircletrx.agents.util.JsonGasRanges;

import javax.annotation.PreDestroy;
import java.io.IOException;
import java.math.BigInteger;

import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;
import static info.vircletrx.agents.constants.AgentRole.SUBMITTER;

@Service
public class MainLifecycle {

    private static final Logger logger = LoggerFactory.getLogger("MainLifecycle");

    private final SystemProperties config;
    private final OperatorPeersChecker operatorPeersChecker;
    private final VircleWrapper vircleWrapper;
    private final SuperblockChainClient sysSuperblockChainClient;
    private final VircleToTrxClient vircleToTrxClient;
    private final RestServer restServer;
    private final SuperblockChallengerClient superblockChallengerClient;
    private final SuperblockDefenderClient superblockDefenderClient;
    private final SuperblockLevelDBBlockStore superblockLevelDBBlockStore;
    private final JsonGasRanges jsonGasRanges;
    public MainLifecycle(
            SystemProperties config,
            OperatorPeersChecker operatorPeersChecker,
            VircleWrapper vircleWrapper,
            SuperblockChainClient sysSuperblockChainClient,
            VircleToTrxClient vircleToTrxClient,
            RestServer restServer,
            SuperblockChallengerClient superblockChallengerClient,
            SuperblockDefenderClient superblockDefenderClient,
            SuperblockLevelDBBlockStore superblockLevelDBBlockStore,
            JsonGasRanges jsonGasRanges
    ) {
        this.config = config;
        this.operatorPeersChecker = operatorPeersChecker;
        this.vircleWrapper = vircleWrapper;
        this.sysSuperblockChainClient = sysSuperblockChainClient;
        this.vircleToTrxClient = vircleToTrxClient;
        this.restServer = restServer;
        this.superblockChallengerClient = superblockChallengerClient;
        this.superblockDefenderClient = superblockDefenderClient;
        this.superblockLevelDBBlockStore = superblockLevelDBBlockStore;
        this.jsonGasRanges = jsonGasRanges;
    }

    public void initialize() throws Exception {
        logger.debug("initialize: [Step #1]");

        operatorPeersChecker.setup();

        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            logger.debug("initialize: [Optional step] Start Vircle wrapper");
            vircleWrapper.setupAndStart();
        }

        logger.debug("initialize: [Step #2]");
        adminResult();

        logger.debug("initialize: [Step #3]");
        if (!sysSuperblockChainClient.setup()) return;

        logger.debug("initialize: [Step #4]");
        if (config.isAgentRoleEnabled(SUBMITTER)) {
            if (!vircleToTrxClient.setup()) return;
        }

        logger.debug("initialize: [Step #5]");
        if (config.isAgentRoleEnabled(CHALLENGER)) {
            if (!superblockChallengerClient.setup()) return;
        }

        logger.debug("initialize: [Step #6]");
        if (config.isAgentRoleEnabled(SUBMITTER)) {
            if (!superblockDefenderClient.setup()) return;
        }

        restServer.start();
        jsonGasRanges.setup();
        logger.debug("initialize: Done");
    }

    public void adminResult() throws IOException {
    }

    @PreDestroy
    public void cleanUp() {
        Thread.currentThread().setName("spring-pre-destroy-thread");
        logger.debug("cleanUp: Free resources");

        if (config.isAgentRoleEnabled(CHALLENGER)) {
            try {
                superblockChallengerClient.cleanUp();
            } catch (IOException e) {
                logger.debug("cleanUp: superblockChallengerClient.cleanUp() failed", e);
            }
        }

        if (config.isAgentRoleEnabled(SUBMITTER)) {
            try {
                superblockDefenderClient.cleanUp();
            } catch (IOException e) {
                logger.debug("cleanUp: superblockDefenderClient.cleanUp() failed", e);
            }
        }

        if (config.isAgentRoleEnabled(SUBMITTER)) {
            vircleToTrxClient.cleanUp();
        }

        sysSuperblockChainClient.cleanUp();

        restServer.stop();

        if (config.isAgentRoleEnabled(CHALLENGER) || config.isAgentRoleEnabled(SUBMITTER)) {
            vircleWrapper.stop();
        }

        superblockLevelDBBlockStore.close();

        jsonGasRanges.cleanUp();
        logger.debug("cleanUp: Done");
    }

}
