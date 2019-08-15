package de.headshotharp.chestsort2.command.generic;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandRunnable {
	public void execute(CommandSender sender, String command, String... args);
}
