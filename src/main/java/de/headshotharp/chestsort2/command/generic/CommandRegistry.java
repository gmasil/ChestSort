package de.headshotharp.chestsort2.command.generic;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class CommandRegistry implements CommandExecutor, TabCompleter {
	private List<ChestsortCommand> commands = new LinkedList<>();

	public CommandRegistry() {

	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String alias,
			String[] args) {
		return new LinkedList<>();
	}
}
