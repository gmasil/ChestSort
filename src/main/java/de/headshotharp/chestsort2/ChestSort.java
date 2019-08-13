package de.headshotharp.chestsort2;

import java.io.IOException;

import org.bukkit.event.Listener;

import de.headshotharp.chestsort2.config.ConfigService;

public class ChestSort extends SpigotPlugin implements Listener {
	@Override
	public void saveDefaultConfig() {
		try {
			ConfigService.saveDefaultConfig();
		} catch (IOException e) {
			error("Error while saving default config", e);
		}
	}
}
