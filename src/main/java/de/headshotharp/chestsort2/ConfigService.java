package de.headshotharp.chestsort2;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

public class ConfigService {
	private static File configFile = new File("plugins/ChestSort", "config.yaml");
	private static ObjectMapper mapper = new YAMLMapper();

	private static Config config;

	private ConfigService() {
	}

	public static Config getDefaultConfig() {
		Config config = new Config();
		config.getDatabase().setDriver("com.mysql.cj.jdbc.Driver");
		config.getDatabase().setDialect("org.hibernate.dialect.MySQL57Dialect");
		config.getDatabase().setUrl("jdbc:mysql://localhost:3306/dbname?useSSL=false");
		config.getDatabase().setUsername("user");
		config.getDatabase().setPassword("pass");
		return config;
	}

	public static Config getH2Config() {
		Config config = new Config();
		config.getDatabase().setDriver("org.h2.Driver");
		config.getDatabase().setDialect("org.hibernate.dialect.H2Dialect");
		config.getDatabase().setUrl("jdbc:h2:mem:");
		config.getDatabase().setUsername("sa");
		config.getDatabase().setPassword("");
		return config;
	}

	public static synchronized Config getConfig() {
		return config;
	}

	public static synchronized Config readConfig() throws IOException {
		if (config == null) {
			config = mapper.readValue(configFile, Config.class);
		}
		return config;
	}

	public static void saveConfig(Config config) throws IOException {
		configFile.getParentFile().mkdirs();
		mapper.writeValue(configFile, config);
	}

	public static void saveDefaultConfig() throws IOException {
		saveConfig(getDefaultConfig());
	}

	protected static File getConfigFile() {
		return configFile;
	}

	protected static void setConfigFile(File configFile) {
		ConfigService.configFile = configFile;
	}
}
