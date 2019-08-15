package de.headshotharp.chestsort2.command;

import static de.headshotharp.chestsort2.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort2.StaticConfig.COLOR_NORMAL;
import static de.headshotharp.chestsort2.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort2.StaticConfig.PERMISSION_MANAGE_CENTRAL;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort2.Registry;
import de.headshotharp.chestsort2.command.generic.ChestsortCommand;
import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

public class InfoCommand implements ChestsortCommand {
	@Override
	public void execute(CommandSender sender, String command, String... args) {
		Player player = (Player) sender;
		if (!player.hasPermission(PERMISSION_MANAGE) && !player.hasPermission(PERMISSION_MANAGE_CENTRAL)) {
			player.sendMessage(COLOR_ERROR + "You dont have permissions to manage chests");
			return;
		}
		Location markedChest = Registry.getPlayerEventListener().getMarkedLocation(player.getName());
		if (markedChest == null) {
			player.sendMessage(COLOR_ERROR
					+ "You have to mark a chest first. Right click a chest with a stick in your main hand.");
		} else {
			List<ChestDAO> chests = Registry.getDataProvider().findAllChestsAt(markedChest);
			if (chests.isEmpty()) {
				player.sendMessage(COLOR_NORMAL + "The marked chest is not registered in ChestSort");
			} else if (chests.size() == 1) {
				player.sendMessage(COLOR_NORMAL + "The marked chest is a " + chests.get(0).getTextBlockString());
			} else {
				player.sendMessage(COLOR_NORMAL + "The marked chest is registered multiple times:");
				chests.forEach(chest -> {
					player.sendMessage(COLOR_NORMAL + "- " + chest.getTextBlockString());
				});
			}
		}
	}

	@Override
	public boolean isApplicable(CommandSender sender, String command, String... args) {
		return command.equalsIgnoreCase(getName());
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String... args) {
		return new LinkedList<>();
	}

	@Override
	public boolean isForPlayerOnly() {
		return true;
	}

	@Override
	public String usage() {
		return "Usage: /chestsort info";
	}

	@Override
	public String getName() {
		return "info";
	}
}
