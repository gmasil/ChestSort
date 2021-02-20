package de.headshotharp.chestsort.config;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class ConfigService {
    private File configFile = new File("plugins/ChestSort", "config.yaml");
    private ObjectMapper mapper = new YAMLMapper();

    private Config config;

    public Config getDefaultConfig() {
        Config defaultConfig = new Config();
        defaultConfig.getDatabase().setDriver("com.mysql.cj.jdbc.Driver");
        defaultConfig.getDatabase().setDialect("org.hibernate.dialect.MySQL57Dialect");
        defaultConfig.getDatabase().setUrl("jdbc:mysql://localhost:3306/dbname?useSSL=false");
        defaultConfig.getDatabase().setUsername("user");
        defaultConfig.getDatabase().setPassword("pass");
        return defaultConfig;
    }

    public Config getH2Config() {
        Config h2Config = new Config();
        h2Config.getDatabase().setDriver("org.h2.Driver");
        h2Config.getDatabase().setDialect("org.hibernate.dialect.H2Dialect");
        h2Config.getDatabase().setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2Config.getDatabase().setUsername("sa");
        h2Config.getDatabase().setPassword("");
        return h2Config;
    }

    public synchronized Config getConfig() {
        return config;
    }

    public synchronized Config readConfig() throws IOException {
        if (config == null) {
            config = mapper.readValue(configFile, Config.class);
        }
        return config;
    }

    public void saveConfig(Config config) throws IOException {
        configFile.getParentFile().mkdirs();
        mapper.writeValue(configFile, config);
    }

    public void saveDefaultConfig() throws IOException {
        if (!configFile.exists()) {
            saveConfig(getDefaultConfig());
        }
    }

    protected File getConfigFile() {
        return configFile;
    }

    protected void setConfigFile(File configFile) {
        this.configFile = configFile;
    }
}
