package de.headshotharp.chestsort2;

import de.headshotharp.chestsort2.command.CommandRegistry;
import de.headshotharp.chestsort2.config.ConfigService;
import de.headshotharp.chestsort2.hibernate.DataProvider;
import de.headshotharp.chestsort2.hibernate.HibernateUtils;

public class Registry {
	private static CommandRegistry commandRegistry = new CommandRegistry();
	private static HibernateUtils hibernateUtils = new HibernateUtils();
	private static DataProvider dataprovider = new DataProvider();
	private static ConfigService configService = new ConfigService();
	private static PlayerEventListener playerEventListener = new PlayerEventListener();

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

	public static DataProvider getDataprovider() {
		return dataprovider;
	}

	protected static void setDataprovider(DataProvider dataprovider) {
		Registry.dataprovider = dataprovider;
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
}
