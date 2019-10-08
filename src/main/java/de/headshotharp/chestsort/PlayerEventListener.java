package de.headshotharp.chestsort;

import static de.headshotharp.chestsort.ChestSortUtils.convertLocation;
import static de.headshotharp.chestsort.ChestSortUtils.hasMarkPermission;
import static de.headshotharp.chestsort.ChestSortUtils.isChestBreaked;
import static de.headshotharp.chestsort.ChestSortUtils.isChestClicked;
import static de.headshotharp.chestsort.ChestSortUtils.isMarkEvent;
import static de.headshotharp.chestsort.ChestSortUtils.isRightClickBlock;
import static de.headshotharp.chestsort.ChestSortUtils.isSignBreaked;
import static de.headshotharp.chestsort.ChestSortUtils.isSignClicked;
import static de.headshotharp.chestsort.ChestSortUtils.locationFromEvent;
import static de.headshotharp.chestsort.ChestSortUtils.sendPlayerChestBreakErrorMessage;
import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.StaticConfig.MATERIAL_SIGN_CENTRAL;
import static de.headshotharp.chestsort.StaticConfig.MATERIAL_SIGN_USER;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class PlayerEventListener implements Listener {
	private Map<String, Location> markedLocations = new HashMap<>();

	public Location getMarkedLocation(String player) {
		return markedLocations.get(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (hasMarkPermission(event.getPlayer()) && isMarkEvent(event)) {
			String mark = null;
			if (isChestClicked(event)) {
				mark = "chest";
			} else if (isSignClicked(event)) {
				mark = "sign";
			}
			if (mark != null) {
				Location location = locationFromEvent(event);
				markedLocations.put(event.getPlayer().getName(), location);
				event.setCancelled(true);
				event.getPlayer().sendMessage(COLOR_GOOD + "Marked " + mark + " at " + location.toHumanString());
			}
		}
		if (isRightClickBlock(event) && isSignClicked(event)) {
			List<SignDAO> signs = Registry.getDataProvider().findAllSignsAt(locationFromEvent(event));
			if (signs.size() == 1) {
				InventoryUtils.insertItemInHand(event.getPlayer(), signs.get(0).isCentral());
			} else if (signs.size() > 1) {
				event.getPlayer().sendMessage(COLOR_ERROR
						+ "Multiple registered signs found at this location. Please delete and recreate the sign");
			}
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		SignDAO sign = createSign(event);
		if (sign != null) {
			DataProvider dp = Registry.getDataProvider();
			// delete all possible previous signs at the location
			dp.findAllSignsAt(sign.getLocation()).forEach(dp::deleteSign);
			// save sign
			dp.persistSign(sign);
			// check
			List<SignDAO> signs = dp.findSign(sign);
			if (signs.isEmpty()) {
				event.getPlayer()
						.sendMessage(COLOR_ERROR + "Error while saving sign to database, this should never occur");
			} else {
				event.getPlayer().sendMessage(COLOR_GOOD + "ChestSort sign successfully created");
			}
		}
	}

	public SignDAO createSign(SignChangeEvent event) {
		if (event.getLine(0).equals("[ChestSort]")) {
			if (event.getBlock().getType().equals(MATERIAL_SIGN_CENTRAL)) {
				if (!event.getPlayer().hasPermission(PERMISSION_MANAGE_CENTRAL)) {
					event.getPlayer()
							.sendMessage(COLOR_ERROR + "You dont have permissions to manage the central warehouse");
					if (event.getPlayer().hasPermission(PERMISSION_MANAGE)) {
						event.getPlayer().sendMessage(COLOR_ERROR
								+ "If you want to create a ChestSort sign for your personal warehouse please use a sign of type "
								+ MATERIAL_SIGN_USER);
					}
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return null;
				}
				event.setLine(0, ChatColor.BLUE + "[ChestSort]");
				event.setLine(1, ChatColor.RED + "Central");
				event.setLine(2, "rightclick to");
				event.setLine(3, "insert a block");
				return new SignDAO(convertLocation(event.getBlock().getLocation()));
			} else if (event.getBlock().getType().equals(MATERIAL_SIGN_USER)) {
				if (!event.getPlayer().hasPermission(PERMISSION_MANAGE)) {
					event.getPlayer().sendMessage(COLOR_ERROR + "You dont have permissions to manage the warehouse");
					event.setCancelled(true);
					event.getBlock().breakNaturally();
					return null;
				}
				event.setLine(0, ChatColor.BLUE + "[ChestSort]");
				event.setLine(1, ChatColor.GREEN + event.getPlayer().getName());
				event.setLine(2, "rightclick to");
				event.setLine(3, "insert a block");
				return new SignDAO(convertLocation(event.getBlock().getLocation()), event.getPlayer().getName());
			}
		}
		return null;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onBlockBreak(BlockBreakEvent event) {
		if (isChestBreaked(event)) {
			List<ChestDAO> chests = Registry.getDataProvider().findAllChestsAt(locationFromEvent(event));
			if (!chests.isEmpty()) {
				event.setCancelled(true);
				sendPlayerChestBreakErrorMessage(event, chests);
			}
		}
		if (isSignBreaked(event)) {
			Location loc = locationFromEvent(event);
			List<SignDAO> signs = Registry.getDataProvider().findAllSignsAt(loc);
			if (!signs.isEmpty()) {
				event.setCancelled(true);
				ChestSortUtils.sendPlayerSignBreakErrorMessage(event);
			}
		}
	}
}