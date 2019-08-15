package de.headshotharp.chestsort2.command;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort2.Registry;
import de.headshotharp.chestsort2.command.generic.ChestsortCommand;
import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

public class CreateCommand implements ChestsortCommand {
	public static final String WH_CENTRAL = "central";
	public static final String WH_USER = "user";

	@Override
	public void execute(CommandSender sender, String command, String... args) {
		Player player = (Player) sender;
		ChestDAO chest = chestByParameter(player, args);
		if (chest != null) {
			if (Registry.getDataprovider().findChest(chest).isEmpty()) {
				Registry.getDataprovider().persistChest(chest);
				if (Registry.getDataprovider().findChest(chest).isEmpty()) {
					player.sendMessage(ChatColor.DARK_RED
							+ "The chest could not be persisted in the database, this should never occur");
				} else {
					player.sendMessage(
							ChatColor.GREEN + "Chest of type " + chest.getMaterial() + " was created successfully");
				}
			} else {
				player.sendMessage(ChatColor.DARK_RED + "Chest is already registered with type " + chest.getMaterial());
			}
		}
	}

	public ChestDAO chestByParameter(Player player, String... args) {
		if (args.length != 2) {
			sendusage(player);
			return null;
		}
		if (!Arrays.asList(WH_CENTRAL, WH_USER).contains(args[0].toLowerCase())) {
			sendusage(player);
			return null;
		}
		String username = null;
		if (args[0].equalsIgnoreCase(WH_USER)) {
			username = player.getName();
		}
		Optional<Material> optionalMaterial = Arrays.asList(Material.values()).stream()
				.filter(mat -> mat.toString().equalsIgnoreCase(args[1])).findFirst();
		if (!optionalMaterial.isPresent()) {
			player.sendMessage(ChatColor.DARK_RED + "The material " + args[1] + " does not exist");
			sendusage(player);
			return null;
		}
		Location markedChest = Registry.getPlayerEventListener().getMarkedLocation(player.getName());
		if (markedChest == null) {
			player.sendMessage(
					ChatColor.DARK_RED + "Right click a chest with a stick in your main hand first to mark a chest.");
			return null;
		}
		return new ChestDAO(markedChest, optionalMaterial.get().toString(), username);
	}

	private void sendusage(Player player) {
		player.sendMessage(ChatColor.DARK_RED + usage());
	}

	@Override
	public boolean isApplicable(CommandSender sender, String command, String... args) {
		return command.equalsIgnoreCase("create");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, String command, String... args) {
		if (args.length == 0) {
			return Arrays.asList(WH_CENTRAL, WH_USER);
		} else if (args.length == 1) {
			return Arrays.asList(WH_CENTRAL, WH_USER).stream().filter(cmd -> cmd.startsWith(args[0]))
					.collect(Collectors.toList());
		} else if (args.length == 2) {
			return Arrays.asList(Material.values()).stream().map(Material::toString)
					.filter(mat -> mat.startsWith(args[1])).collect(Collectors.toList());
		}
		return new LinkedList<>();
	}

	@Override
	public boolean isForPlayerOnly() {
		return true;
	}

	@Override
	public String usage() {
		return "Usage: /chestsort create <central/user> <material>";
	}
}
