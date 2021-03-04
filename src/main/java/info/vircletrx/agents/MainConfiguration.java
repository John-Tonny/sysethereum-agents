package info.vircletrx.agents;

import com.google.gson.Gson;

import javax.net.ssl.*;

import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsParameters;
import com.sun.net.httpserver.HttpsServer;
import info.vircletrx.agents.addition.TrxContract;
import lombok.extern.slf4j.Slf4j;
import org.bitcoinj.core.Context;
import org.bitcoinj.script.Script;
import org.simplejavamail.mailer.Mailer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import info.vircletrx.agents.constants.*;
import info.vircletrx.agents.contract.*;
import info.vircletrx.agents.core.vircle.VircleWalletAppKit;
import info.vircletrx.agents.service.MailerFactory;
import info.vircletrx.agents.service.rest.*;
import org.tron.tronj.abi.datatypes.generated.Uint256;
import org.tron.tronj.client.TronClient;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.List;
import java.util.concurrent.Executors;

import static info.vircletrx.agents.constants.SystemProperties.*;

import org.tron.tronj.client.contract.*;
import org.tron.tronj.utils.*;
import org.tron.tronj.client.exceptions.IllegalException;

@Configuration
@Slf4j(topic = "MainConfiguration")
public class MainConfiguration {

    @SuppressWarnings("unused")
    private static final Logger logger = LoggerFactory.getLogger("MainConfiguration");

    @Bean
    public Gson gson() {
        return new Gson();
    }

    @Bean
    public SystemProperties config(SystemPropertiesFactory systemPropertiesFactory) {
        return systemPropertiesFactory.create();
    }

    @Nullable
    @Bean
    public Mailer mailer(MailerFactory mailerFactory) {
        return mailerFactory.create();
    }

    @Bean
    public AgentConstants getAgentConstants(SystemProperties systemProperties) {
        String constants = systemProperties.constants();

        AgentConstants result;

        switch (constants) {
            case INTEGRATION:
                result = new IntegrationAgentConstantsFactory().create();
                break;
            case LOCAL:
                result = new LocalAgentConstantsFactory().create();
                break;
            case TRX_VIRCLEMAIN:
                result = new MainnetAgentConstantsFactory().create();
                break;
            default:
                throw new RuntimeException("Unknown value for 'constants': '" + constants + "'");
        }

        return result;
    }

    @Bean
    public TronClient mainTrxService(SystemProperties config) {
        String networkName = config.getStringProperty("tron.network");
        String privateKey = config.getStringProperty("txon.private.key");
        if(networkName.compareTo("mainnet")==0){
            return TronClient.ofMainnet(privateKey);
        }else if(networkName.compareTo("shasta")==0){
            return TronClient.ofShasta(privateKey);
        }else if(networkName.compareTo("nile")== 0){
            return TronClient.ofNile(privateKey);
        }else{
            throw new RuntimeException("Unknown network for 'network': '" + networkName + "'");
        }
    }

    @Bean
    public TrxAddresses trxAddresses(SystemProperties config) {
        return new TrxAddresses(config.generalPurposeAndSendSuperblocksAddress(), config.vircleSuperblockChallengerAddress());
    }

    @Bean
    public BigInteger superblockDuration(VircleBattleManagerExtended battleManager) {
        return battleManager.superblockDuration().getValue();
    }

    @Bean
    public VircleClaimManagerExtended claimManager(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleClaimManager.getPreviouslyDeployedAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleClaimManagerExtended(trxCntr);

        return result;
    }

    @Bean
    public VircleClaimManagerExtended claimManagerForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleClaimManager.getPreviouslyDeployedAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleClaimManagerExtended(trxCntr);
        return result;
    }


    @Bean
    public VircleBattleManagerExtended battleManager(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleBattleManagerExtended(trxCntr);

        return result;
    }

    @Bean
    public VircleBattleManagerExtended battleManagerForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleBattleManagerExtended.getAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleBattleManagerExtended(trxCntr);

        return result;
    }

    @Bean
    public VircleERC20ManagerExtended erc20ManagerForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleERC20ManagerExtended.getAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleERC20ManagerExtended(trxCntr);

        return result;
    }

    @Bean
    public VircleSuperblocksExtended superblocks(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleSuperblocksExtended.getAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleSuperblocksExtended(trxCntr);

        return result;
    }

    @Bean
    public VircleSuperblocksExtended superblocksForChallenges(
            SystemProperties config, AgentConstants agentConstants,
            TrxAddresses trxAddresses
    ) throws IOException {
        String contractAddress = VircleSuperblocksExtended.getAddress(agentConstants.getNetworkId());

        TronClient client = mainTrxService(config);
        TrxContract trxCntr = new TrxContract(client, contractAddress, trxAddresses.generalPurposeAddress);

        var result = new VircleSuperblocksExtended(trxCntr);

        return result;
    }

    @Bean
    public BigInteger superblockDelay(VircleClaimManagerExtended claimManager) throws Exception {
        return claimManager.superblockDelay().getValue();
    }

    @Bean
    public BigInteger superblockTimeout(VircleClaimManagerExtended claimManager) throws Exception {
        return claimManager.superblockTimeout().getValue();
    }

    @Bean
    public BigInteger minProposalDeposit(VircleClaimManagerExtended claimManager) throws Exception {
        return claimManager.minProposalDeposit().getValue();
    }

    @Bean
    public HttpsServer httpsServer(
            SystemProperties config,
            GetSPVHandler getSPVHandler,
            GetSuperblockByVircleHandler getSuperblockByVircleHandler,
            GetSuperblockHandler getSuperblockHandler,
            GetVircleRPCHandler getVircleRPCHandler,
            InfoHandler infoHandler
    ) throws IOException, KeyManagementException, NoSuchAlgorithmException, KeyStoreException, CertificateException, UnrecoverableKeyException {
        HttpsServer httpsServer  = HttpsServer.create(new InetSocketAddress(8443), 0);
        if(!config.isServerEnabled())
            return httpsServer;


        SSLContext sslContext = SSLContext.getInstance("TLS");

        // initialise the keystore
        char[] password = config.sslFilePassword().toCharArray();
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream(config.sslFile());
        ks.load(fis, password);

        // setup the key manager factory
        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, password);

        // setup the trust manager factory
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
        tmf.init(ks);

        // setup the HTTPS context and parameters
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
        httpsServer.setHttpsConfigurator(new HttpsConfigurator(sslContext) {
            public void configure(HttpsParameters params) {
                try {
                    // initialise the SSL context
                    SSLContext context = getSSLContext();
                    SSLEngine engine = context.createSSLEngine();
                    params.setNeedClientAuth(false);
                    params.setCipherSuites(engine.getEnabledCipherSuites());
                    params.setProtocols(engine.getEnabledProtocols());

                    // Set the SSL parameters
                    SSLParameters sslParameters = context.getSupportedSSLParameters();
                    params.setSSLParameters(sslParameters);

                } catch (Exception ex) {
                    System.out.println("Failed to create HTTPS port");
                }
            }
        });


        httpsServer.createContext("/", infoHandler);
        httpsServer.createContext("/spvproof", getSPVHandler);
        httpsServer.createContext("/superblockbyvircleblock", getSuperblockByVircleHandler);
        httpsServer.createContext("/superblock", getSuperblockHandler);
        httpsServer.createContext("/virclerpc", getVircleRPCHandler);
        httpsServer.setExecutor(Executors.newFixedThreadPool(256));
        return httpsServer;
    }

    @Bean
    public Context vircleContext(AgentConstants agentConstants) {
        return new Context(agentConstants.getVircleParams());
    }

    @Bean
    public VircleWalletAppKit vircleWalletAppKit(SystemProperties config, Context vircleContext) {
        File dataDirectory = new File(config.dataDirectory() + "/VircleWrapper");
        return new VircleWalletAppKit(vircleContext, Script.ScriptType.P2WPKH, null, dataDirectory, "vircletrxAgentLibdohj");
    }
}
