package org.sysethereum.agents.constants;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.typesafe.config.ConfigRenderOptions;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

/**
 * Utility class to retrieve property values from the configuration file
 *
 * The properties are taken from different sources and merged in the following order
 * (the config option from the next source overrides option from previous):
 * - system property : each config entry might be altered via -D VM option
 * - config specified with the -Dsysethereum.agents.conf.file=[file.conf] VM option
 *
 * @author Roman Mandeleil
 * @since 22.05.2014
 */
@Slf4j(topic = "SystemProperties")
public class SystemProperties {
    private static final Logger logger = LoggerFactory.getLogger("SystemProperties");

    public static final String LOCAL = "local";
    public static final String INTEGRATION = "integration";
    public static final String ETHGANACHE_SYSCOINMAIN = "ethganachesyscoinmain";

    private static final String YES = "yes";
    private static final String NO = "no";

    private final Config config;

    private final String projectVersion;
    private final String projectVersionModifier;

    public SystemProperties() {
        try {
            Config javaSystemProperties = ConfigFactory.load("no-such-resource-only-system-props");
            String file = System.getProperty("sysethereum.agents.conf.file");
            Config cmdLineConfigFile = file != null ? ConfigFactory.parseFile(new File(file)) : ConfigFactory.empty();
            logger.info("Config ( {} ): user properties from -Dsysethereum.agents.conf.file file '{}'",
                    cmdLineConfigFile.entrySet().isEmpty() ? NO : YES, file);
            this.config = javaSystemProperties
                    .withFallback(cmdLineConfigFile);

            logger.debug("Config trace: " + config.root().render(ConfigRenderOptions.defaults().
                    setComments(false).setJson(false)));

            Properties props = new Properties();
            InputStream is = getClass().getResourceAsStream("/version.properties");
            props.load(is);

            this.projectVersion = props.getProperty("versionNumber").replaceAll("'", "");
            this.projectVersionModifier = props.getProperty("modifier").replaceAll("\"", "");
        } catch (Exception e) {
            logger.error("Can't read config.", e);
            throw new RuntimeException(e);
        }
    }

    public boolean isSyscoinSuperblockSubmitterEnabled() {
        return getBooleanProperty("syscoin.superblock.submitter.enabled", false);
    }

    public boolean isSyscoinBlockChallengerEnabled() {
        return getBooleanProperty("syscoin.superblock.challenger.enabled", false);
    }

    public boolean isGanache() {
        return LOCAL.equals(constants()) || ETHGANACHE_SYSCOINMAIN.equals(constants());
    }

    public String constants() {
        return config.hasPath("constants") ? config.getString("constants") : null;
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

    public String generalPurposeAndSendSuperblocksUnlockPW(){
        return getStringProperty("general.purpose.and.send.superblocks.unlockpw", null);
    }

    public String syscoinSuperblockChallengerAddress() {
        return getStringProperty("syscoin.superblock.challenger.address", null);
    }

    public String syscoinSuperblockChallengerUnlockPW(){
        return getStringProperty("syscoin.superblock.challenger.unlockpw", null);
    }

    public String dataDirectory() {
        return getStringProperty("data.directory", null);
    }

    public String secondaryURL() {
        return getStringProperty("secondary.url", "https://mainnet.infura.io/v3/d178aecf49154b12be98e68e998cfb8d");
    }

    public String syscoinRPCUser() {
        return getStringProperty("syscoinrpc.user", "u");
    }

    public String syscoinRPCPassword() {
        return getStringProperty("syscoinrpc.password", "p");
    }

    public String syscoinRPCURL() {
        return getStringProperty("syscoinrpc.url_and_port", "http://localhost:8370/");
    }

    public long gasPriceMinimum() {
        return getLongProperty("gas.price.min", 0);
    }

    public long gasPriceMaximum() {
        return getLongProperty("gas.price.max",  0);
    }

    public long gasLimit() {
        return getLongProperty("gas.limit", 0);
    }

    public boolean isWithdrawFundsEnabled() {
        return getBooleanProperty("withdraw.funds.enabled", false);
    }

    public long depositedFundsLimit() {
        return getLongProperty("deposited.funds.limit", 0);
    }

    protected String getStringProperty(String propertyName, String defaultValue) {
        return config.hasPath(propertyName) ? config.getString(propertyName) : defaultValue;
    }

    protected long getLongProperty(String propertyName, long defaultValue) {
        return config.hasPath(propertyName) ? config.getLong(propertyName) : defaultValue;
    }

    protected boolean getBooleanProperty(String propertyName, boolean defaultValue) {
        return config.hasPath(propertyName) ? config.getBoolean(propertyName) : defaultValue;
    }

}