package net.getnova.backend.config;

import lombok.extern.slf4j.Slf4j;
import net.getnova.backend.service.Service;
import net.getnova.backend.service.event.PreInitService;
import net.getnova.backend.service.event.PreInitServiceEvent;

import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This is the config service which hales all configs, for other services, modules or the main config.
 * <b>The config service <i>MUST</i> be initialized before all other services.</b>
 */
@Slf4j
@Service("config")
@Singleton
public class ConfigService {

    private final Map<String, Set<ConfigValueData>> configs;
    private final File configFile;
    private boolean environment;

    /**
     * Creates a new config service.
     */
    public ConfigService() {
        this.environment = false;
        this.configs = new HashMap<>();
        this.configFile = new File("config.yaml").getAbsoluteFile();
    }

    /**
     * The values are now read from the environment variables.
     */
    public void useEnvironment() {
        this.environment = true;
    }

    @PreInitService
    private void preInit(final PreInitServiceEvent event) {
        if (this.environment) {
            log.info("Config values will be read from the environment variables.");
            ConfigUtils.loadFromEnvironment(this.configs);
        } else {
            try {
                if (!this.configFile.exists()) {
                    final File parentFile = this.configFile.getParentFile();
                    if (parentFile.exists()) parentFile.mkdirs();
                    if (this.configFile.createNewFile()) {
                        log.info("Created configuration file {}.", this.configFile.getName());
                    }
                }
                ConfigUtils.loadFromFile(this.configFile, this.configs);
                ConfigUtils.save(this.configFile, this.configs);
            } catch (IOException e) {
                log.error("Unable to create, read from or write to config file.", e);
            }
        }
    }

    /**
     * This method adds a new Config file to the config lifecycle.
     *
     * @param id     the id of the config file
     * @param config the instance of the config
     * @param <T>    the type of the config object
     * @return the instance of the config
     */
    public <T> T addConfig(final String id, final T config) {
        this.configs.put(id.strip(), ConfigUtils.parseConfig(config));
        return config;
    }
}
