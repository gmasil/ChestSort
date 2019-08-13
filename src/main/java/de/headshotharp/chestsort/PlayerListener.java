package de.headshotharp.chestsort;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {
	private ChestSort plugin;
	private Location markedloc;

	public PlayerListener(ChestSort plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			Player player = event.getPlayer();
			if (player.hasPermission(ChestSort.PERMISSION_NAME_MANAGE)) {
				if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
					if (event.getClickedBlock().getType().equals(Material.CHEST)) {
						markedloc = new Location(event.getPlayer().getWorld(),
								event.getClickedBlock().getLocation().getBlockX(),
								event.getClickedBlock().getLocation().getBlockY(),
								event.getClickedBlock().getLocation().getBlockZ());
						player.sendMessage(ChatColor.GREEN + "You have marked a chest at " + markedloc.getBlockX()
								+ ", " + markedloc.getBlockY() + ", " + markedloc.getBlockZ() + ".");
						event.setCancelled(true);
					}
				}
			}
			if (event.getClickedBlock().getType().equals(Material.OAK_SIGN)) {
				if (player.getInventory().getItemInMainHand().getType().equals(Material.STICK)) {
					if (player.hasPermission(ChestSort.PERMISSION_NAME_MANAGE)) {
						for (Signs sign : getSigns()) {
							if (sign.getLocation().equals(event.getClickedBlock().getLocation())) {
								markedloc = new Location(event.getPlayer().getWorld(),
										event.getClickedBlock().getLocation().getBlockX(),
										event.getClickedBlock().getLocation().getBlockY(),
										event.getClickedBlock().getLocation().getBlockZ());
								player.sendMessage(
										ChatColor.GREEN + "You have marked a ChestSort-Sign at " + markedloc.getBlockX()
												+ ", " + markedloc.getBlockY() + ", " + markedloc.getBlockZ() + ".");
								event.setCancelled(true);
								return;
							}
						}
					}
				} else {
					if (isChestSortSign(event.getClickedBlock().getLocation())) {
						event.setCancelled(true);
						plugin.fillChest(player);
					}
				}
			}
		}
	}

	public boolean isChestSortSign(Location loc) {
		for (Signs sign : getSigns()) {
			if (sign.getLocation().equals(loc)) {
				return true;
			}
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		if (event.getPlayer().hasPermission(ChestSort.PERMISSION_NAME_MANAGE)) {
			if (event.getLine(0).equals("[ChestSort]")) {
				if (event.getBlock().getType().equals(Material.OAK_SIGN)) {
					event.setLine(0, ChatColor.BLUE + "[ChestSort]");
					event.setLine(1, "rightclick to");
					event.setLine(2, "insert a block");
					event.setLine(3, "in your hand");
					getSigns().add(new Signs(event.getBlock().getLocation()));
					event.getPlayer().sendMessage(ChatColor.GREEN + "A chestsort sign has been created successfully!");
				} else {
					event.setCancelled(true);
					event.getBlock().breakNaturally();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		// PERFORMANCE ISSUE!!!
		if (event.getBlock().getType().equals(Material.CHEST)) {
			for (Chests chest : getChests()) {
				if (chest.getLocation().equals(event.getBlock().getLocation())) {
					event.setCancelled(true);
					event.getPlayer().sendMessage(ChatColor.RED + "This chest of type " + ChatColor.BLUE
							+ chest.getMaterial().toString() + ChatColor.RED + " is protected by ChestSort");
					return;
				}
			}
		}
		if (event.getBlock().getType().equals(Material.OAK_SIGN)) {
			for (Signs sign : getSigns()) {
				if ((sign.getLocation().getWorld().getName()
						.equals(event.getBlock().getLocation().getWorld().getName()))
						&& (event.getBlock().getLocation().getBlockX() == sign.getLocation().getBlockX())
						&& (event.getBlock().getLocation().getBlockZ() == sign.getLocation().getBlockZ())) {
					if ((event.getBlock().getLocation().getBlockY() == sign.getLocation().getBlockY())) {
						event.setCancelled(true);
						event.getPlayer().sendMessage(ChatColor.RED + "This sign is protected by ChestSort");
						return;
					} else if ((event.getBlock().getLocation().getBlockY() == (sign.getLocation().getBlockY() - 1))) {
						event.setCancelled(true);
						event.getPlayer()
								.sendMessage(ChatColor.RED + "The sign obove this block is protected by ChestSort");
						return;
					}
				}
			}
		}
	}

	public Location getMarkedLocation() {
		return this.markedloc;
	}

	public List<Chests> getChests() {
		return plugin.getChests();
	}

	public List<Signs> getSigns() {
		return plugin.getSigns();
	}
}
