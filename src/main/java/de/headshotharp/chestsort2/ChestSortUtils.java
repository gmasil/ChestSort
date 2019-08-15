package de.headshotharp.chestsort2;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

public class ChestSortUtils {
	private ChestSortUtils() {
	}

	public static boolean isChestMarkEvent(PlayerInteractEvent event) {
		return isRightClickBlock(event) && isStickInHand(event) && isChestClicked(event);
	}

	public static boolean isRightClickBlock(PlayerInteractEvent event) {
		return event.getAction() == Action.RIGHT_CLICK_BLOCK;
	}

	public static boolean isStickInHand(PlayerInteractEvent event) {
		return event.getPlayer().getInventory().getItemInMainHand().getType() == Material.STICK;
	}

	public static boolean isChestClicked(PlayerInteractEvent event) {
		return event.getClickedBlock().getType() == Material.CHEST;
	}

	public static boolean isChestBreaked(BlockBreakEvent event) {
		return event.getBlock().getType() == Material.CHEST;
	}

	public static boolean isSignBreaked(BlockBreakEvent event) {
		return event.getBlock().getType() == ChestSort.MATERIAL_SIGN_CENTRAL
				|| event.getBlock().getType() == ChestSort.MATERIAL_SIGN_USER;
	}

	public static Location locationFromEvent(PlayerInteractEvent event) {
		return convertLocation(event.getClickedBlock().getLocation());
	}

	public static Location locationFromEvent(BlockBreakEvent event) {
		return convertLocation(event.getBlock().getLocation());
	}

	public static Location convertLocation(org.bukkit.Location location) {
		return new Location(location.getWorld().getName(), location.getBlockX(), location.getBlockY(),
				location.getBlockZ());
	}

	public static void sendPlayerChestBreakErrorMessage(BlockBreakEvent event, List<ChestDAO> chests) {
		if (chests.size() == 1) {
			event.getPlayer().sendMessage(ChatColor.RED + "This chest of type " + ChatColor.BLUE
					+ chests.get(0).getMaterial() + ChatColor.RED + " is protected by ChestSort");
		} else {
			event.getPlayer()
					.sendMessage(ChatColor.RED + "This chest of types " + ChatColor.BLUE
							+ String.join(", ", chests.stream().map(ChestDAO::getMaterial).collect(Collectors.toList()))
							+ ChatColor.RED + " is protected by ChestSort");
		}
	}

	public static void sendPlayerSignBreakErrorMessage(BlockBreakEvent event) {
		event.getPlayer().sendMessage(ChatColor.RED + "This sign is protected by ChestSort");
	}
}
