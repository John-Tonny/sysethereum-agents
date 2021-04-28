package info.vircletrx.agents.constants;

import com.google.common.base.Charsets;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;

import static info.vircletrx.agents.constants.AgentRole.CHALLENGER;

/**
 * Utility class to retrieve property values from the configuration file
 * <p>
 * The properties are taken from different sources and merged in the following order
 * (the config option from the next source overrides option from previous):
 * - system property : each config entry might be altered via -D VM option
 * - config specified with the -Dvircletrx.agents.conf.file=[file.conf] VM option
 *
 * @author Roman Mandeleil
 * @since 22.05.2014
 */
@Slf4j(topic = "SystemProperties")
public class SystemProperties {

    private static final Logger logger = LoggerFactory.getLogger("SystemProperties");

    public static final String LOCAL = "local";
    public static final String INTEGRATION = "integration";
    public static final String TRX_VIRCLEMAIN = "mainnet";

    public static final String YES = "yes";
    public static final String NO = "no";

    private final Config config;

    private final String projectVersion;
    private final String projectVersionModifier;

    public static SystemProperties forTest(InputStream config) {
        return new SystemProperties(ConfigFactory.parseReader(new InputStreamReader(config, Charsets.UTF_8)), "1.0.0", "DEV");
    }

    public SystemProperties(Config config, String projectVersion, String projectVersionModifier) {
        this.config = config;
        this.projectVersion = projectVersion;
        this.projectVersionModifier = projectVersionModifier;

        logger.debug("Config trace: " + config.root().render(ConfigRenderOptions.defaults().setComments(false).setJson(false)));
    }

    public String getLastEthBlockProcessedFilename(AgentRole agentRole) {
        return agentRole == CHALLENGER
                ? "SuperblockChallengerLatestEthBlockProcessedFile.dat"
                : "SuperblockDefenderLatestEthBlockProcessedFile.dat";
    }

    public String getSessionToSuperblockMapFilename(AgentRole agentRole) {
        return agentRole == CHALLENGER
                ? "SuperblockChallengerSessionToSuperblockMap.dat"
                : "SuperblockDefenderSessionToSuperblockMap.dat";
    }

    public boolean isAgentRoleEnabled(AgentRole agentRole) {
        return agentRole == CHALLENGER
                ? getBooleanProperty("vircle.superblock.challenger.enabled")
                : getBooleanProperty("vircle.superblock.submitter.enabled");
    }


    public String constants() {
        return config.getString("constants");
    }

    public String projectVersion() {
        return projectVersion;
    }

    public String projectVersionModifier() {
        return projectVersionModifier;
    }

    public String generalPurposeAndSendSuperblocksAddress() {
        return getStringProperty("general.purpose.and.send.superblocks.address", null);
    }

    public String generalPurposeAndSendSuperblocksUnlockPW() {
        return getStringProperty("general.purpose.and.send.superblocks.unlockpw", null);
    }

    public String vircleSuperblockChallengerAddress() {
        return getStringProperty("vircle.superblock.challenger.address", null);
    }

    public String vircleSuperblockChallengerUnlockPW() {
        return getStringProperty("vircle.superblock.challenger.unlockpw", null);
    }
    public boolean isServerEnabled() {
        return getBooleanProperty("server.enabled", false);
    }
    public String sslFile() {
        return getStringProperty("server.ssl.key-store");
    }

    public String sslFilePassword() {
        return getStringProperty("server.ssl.key-store-password");
    }

    public String dataDirectory() {
        return getStringProperty("data.directory");
    }

    public String secondaryURL() {
        return getStringProperty("secondary.url", "https://mainnet.infura.io/v3/d178aecf49154b12be98e68e998cfb8d");
    }

    public String vircleRPCUser() {
        return getStringProperty("virclerpc.user", "u");
    }

    public String vircleRPCPassword() {
        return getStringProperty("virclerpc.password", "p");
    }

    public String vircleRPCURL() {
        return getStringProperty("virclerpc.url_and_port", "http://localhost:9142/");
    }

    public long gasPriceMinimum() {
        return getLongProperty("gas.price.min", 0);
    }

    public long gasPriceMaximum() {
        return getLongProperty("gas.price.max", 0);
    }

    public long gasLimit() {
        return getLongProperty("gas.limit", 0);
    }

    public boolean isWithdrawFundsEnabled() {
        return getBooleanProperty("withdraw.funds.enabled", false);
    }

    // john
    public boolean isWeb3Parity() {
        return getBooleanProperty("web3j.parity.mode", false);
    }

    public long depositedFundsLimit() {
        return getLongProperty("deposited.funds.limit", 0);
    }

    public String getStringProperty(String propertyName) {
        return config.getString(propertyName);
    }

    public String getStringProperty(String propertyName, String defaultValue) {
        return config.hasPath(propertyName) ? config.getString(propertyName) : defaultValue;
    }

    public int getIntProperty(String propertyName) {
        return config.getInt(propertyName);
    }


    @SuppressWarnings("unused")
    public long getLongProperty(String propertyName) {
        return config.getLong(propertyName);
    }

    public long getLongProperty(String propertyName, long defaultValue) {
        return config.hasPath(propertyName) ? config.getLong(propertyName) : defaultValue;
    }

    @SuppressWarnings("unused")
    public boolean getBooleanProperty(String propertyName) {
        return config.getBoolean(propertyName);
    }

    public boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        return config.hasPath(propertyName) ? config.getBoolean(propertyName) : defaultValue;
    }

}