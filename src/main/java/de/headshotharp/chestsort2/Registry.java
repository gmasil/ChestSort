package de.headshotharp.chestsort2;

import de.headshotharp.chestsort2.command.CommandRegistry;
import de.headshotharp.chestsort2.config.ConfigService;
import de.headshotharp.chestsort2.hibernate.HibernateUtils;

public class Registry {
	private static CommandRegistry commandRegistry = new CommandRegistry();
	private static HibernateUtils hibernateUtils = new HibernateUtils();
	private static ConfigService configService = new ConfigService();
	private static PlayerEventListener playerEventListener = new PlayerEventListener();

	private Registry() {
	}

	public static CommandRegistry getCommandRegistry() {
		return commandRegistry;
	}

	public static HibernateUtils getHibernateUtils() {
		return hibernateUtils;
	}

	public static ConfigService getConfigService() {
		return configService;
	}

	public static PlayerEventListener getPlayerEventListener() {
		return playerEventListener;
	}
}
