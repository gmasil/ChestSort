package de.headshotharp.chestsort2;

import java.io.IOException;

import org.bukkit.event.Listener;

public class ChestSort extends SpigotPlugin implements Listener {
	@Override
	public void onEnable() {
		saveDefaultConfig();
		try {
			Registry.getConfigService().readConfig();
		} catch (IOException e) {
			error("Error while loading config", e);
			return;
		}
		try {
			Registry.getHibernateUtils().setDatabaseConfig(Registry.getConfigService().getConfig().getDatabase());
		} catch (Exception e) {
			error("Error while connecting to database", e);
			return;
		}
		try {
			getCommand("chestsort").setExecutor(Registry.getCommandRegistry());
			getCommand("chestsort").setTabCompleter(Registry.getCommandRegistry());
			getServer().getPluginManager().registerEvents(Registry.getPlayerEventListener(), this);
			Registry.getCommandRegistry().registerDefaultCommands();
		} catch (Exception e) {
			error("Error while registering commands", e);
		}
	}

	@Override
	public void saveDefaultConfig() {
		try {
			Registry.getConfigService().saveDefaultConfig();
		} catch (IOException e) {
			error("Error while saving default config", e);
		}
	}
}
