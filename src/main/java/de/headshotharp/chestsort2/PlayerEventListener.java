package de.headshotharp.chestsort2;

import static de.headshotharp.chestsort2.ChestSortUtils.convertLocation;
import static de.headshotharp.chestsort2.ChestSortUtils.isChestBreaked;
import static de.headshotharp.chestsort2.ChestSortUtils.isChestMarkEvent;
import static de.headshotharp.chestsort2.ChestSortUtils.isSignBreaked;
import static de.headshotharp.chestsort2.ChestSortUtils.locationFromEvent;
import static de.headshotharp.chestsort2.ChestSortUtils.sendPlayerChestBreakErrorMessage;
import static de.headshotharp.chestsort2.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort2.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort2.StaticConfig.MATERIAL_SIGN_CENTRAL;
import static de.headshotharp.chestsort2.StaticConfig.MATERIAL_SIGN_USER;
import static de.headshotharp.chestsort2.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort2.StaticConfig.PERMISSION_MANAGE_CENTRAL;

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

import de.headshotharp.chestsort2.hibernate.DataProvider;
import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.SignDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

public class PlayerEventListener implements Listener {
	private Map<String, Location> markedLocations = new HashMap<>();

	public Location getMarkedLocation(String player) {
		return markedLocations.get(player);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerInteract(PlayerInteractEvent event) {
		if ((event.getPlayer().hasPermission(PERMISSION_MANAGE)
				|| event.getPlayer().hasPermission(PERMISSION_MANAGE_CENTRAL)) && isChestMarkEvent(event)) {
			Location location = locationFromEvent(event);
			markedLocations.put(event.getPlayer().getName(), location);
			event.getPlayer().sendMessage(COLOR_GOOD + "Marked chest at " + location.toHumanString());
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
				if (event.getPlayer().hasPermission(PERMISSION_MANAGE)) {
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
