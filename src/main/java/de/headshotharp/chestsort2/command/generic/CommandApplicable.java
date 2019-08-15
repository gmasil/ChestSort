package de.headshotharp.chestsort2.command.generic;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandApplicable {
	public boolean isApplicable(CommandSender sender, String command, String... args);
}
