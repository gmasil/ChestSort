package de.headshotharp.chestsort2.command;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort2.command.generic.ChestsortCommand;

public class CommandRegistry implements CommandExecutor, TabCompleter {
	private List<ChestsortCommand> commands = new LinkedList<>();

	public void registerDefaultCommands() {
		commands.add(new CreateCommand());
		commands.add(new InfoCommand());
	}

	public List<ChestsortCommand> getCommands() {
		return commands;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] originalArgs) {
		if (originalArgs.length > 0) {
			String cmd = originalArgs[0];
			String[] args = moveArgs(originalArgs);
			for (ChestsortCommand command : commands) {
				if (command.isApplicable(sender, cmd, args)) {
					if (command.isForPlayerOnly() && !(sender instanceof Player)) {
						sender.sendMessage("The command is for players only");
					} else {
						command.execute(sender, cmd, args);
					}
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command bukkitCommand, String alias,
			String[] originalArgs) {
		if (originalArgs.length > 0) {
			String cmd = originalArgs[0];
			String[] args = moveArgs(originalArgs);
			for (ChestsortCommand command : commands) {
				if (command.isApplicable(sender, cmd, args)) {
					return command.onTabComplete(sender, cmd, args);
				}
			}
		}
		return new LinkedList<>();
	}

	private String[] moveArgs(String[] args) {
		if (args.length <= 1) {
			return new String[0];
		}
		String[] newArgs = new String[args.length - 1];
		for (int i = 0; i < newArgs.length; i++) {
			newArgs[i] = args[i + 1];
		}
		return newArgs;
	}
}
