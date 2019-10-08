package de.headshotharp.chestsort;

import de.headshotharp.chestsort.command.CommandRegistry;
import de.headshotharp.chestsort.config.ConfigService;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.HibernateUtils;

public class Registry {
	private static CommandRegistry commandRegistry = new CommandRegistry();
	private static HibernateUtils hibernateUtils = new HibernateUtils();
	private static DataProvider dataProvider = new DataProvider();
	private static ConfigService configService = new ConfigService();
	private static PlayerEventListener playerEventListener = new PlayerEventListener();
	private static SpigotPlugin spigotPlugin = null;

	private Registry() {
	}

	public static CommandRegistry getCommandRegistry() {
		return commandRegistry;
	}

	protected static void setCommandRegistry(CommandRegistry commandRegistry) {
		Registry.commandRegistry = commandRegistry;
	}

	public static HibernateUtils getHibernateUtils() {
		return hibernateUtils;
	}

	protected static void setHibernateUtils(HibernateUtils hibernateUtils) {
		Registry.hibernateUtils = hibernateUtils;
	}

	public static DataProvider getDataProvider() {
		return dataProvider;
	}

	protected static void setDataProvider(DataProvider dataProvider) {
		Registry.dataProvider = dataProvider;
	}

	public static ConfigService getConfigService() {
		return configService;
	}

	protected static void setConfigService(ConfigService configService) {
		Registry.configService = configService;
	}

	public static PlayerEventListener getPlayerEventListener() {
		return playerEventListener;
	}

	protected static void setPlayerEventListener(PlayerEventListener playerEventListener) {
		Registry.playerEventListener = playerEventListener;
	}

	public static SpigotPlugin getSpigotPlugin() {
		return spigotPlugin;
	}

	protected static void setSpigotPlugin(SpigotPlugin spigotPlugin) {
		Registry.spigotPlugin = spigotPlugin;
	}
}
