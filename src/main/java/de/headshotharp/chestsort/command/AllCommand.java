package de.headshotharp.chestsort.command;

import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.InventoryUtils;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;

public class AllCommand implements ChestsortCommand {
	@Override
	public void execute(CommandSender sender, String command, String... args) {
		Player player = (Player) sender;
		if (args.length != 1) {
			player.sendMessage(COLOR_ERROR + usage());
			return;
		}
		boolean central;
		if (args[0].equalsIgnoreCase("central")) {
			central = true;
		} else if (args[0].equalsIgnoreCase("user")) {
			central = false;
		} else {
			player.sendMessage(COLOR_ERROR + usage());
			return;
		}
		InventoryUtils.insertAllInventory(player, central);
	}

	@Override
	public boolean isApplicable(CommandSender sender, String command, String... args) {
		return command.equalsIgnoreCase(getName());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String... args) {
		if (args.length == 0) {
			return Arrays.asList("central", "user");
		} else if (args.length == 1) {
			return Arrays.asList("central", "user").stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
					.collect(Collectors.toList());
		}
		return new LinkedList<>();
	}

	@Override
	public boolean isForPlayerOnly() {
		return true;
	}

	@Override
	public String usage() {
		return "Usage: /chestsort all <central/user>";
	}

	@Override
	public String getName() {
		return "all";
	}
}
