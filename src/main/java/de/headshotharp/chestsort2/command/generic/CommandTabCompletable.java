package de.headshotharp.chestsort2.command.generic;

import java.util.List;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandTabCompletable {
	public List<String> onTabComplete(CommandSender sender, String command, String[] args);
}
