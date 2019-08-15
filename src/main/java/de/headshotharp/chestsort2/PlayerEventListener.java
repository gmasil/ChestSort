package de.headshotharp.chestsort2;

import static de.headshotharp.chestsort2.ChestSortUtils.isChestBreaked;
import static de.headshotharp.chestsort2.ChestSortUtils.isChestMarkEvent;
import static de.headshotharp.chestsort2.ChestSortUtils.isSignBreaked;
import static de.headshotharp.chestsort2.ChestSortUtils.locationFromEvent;
import static de.headshotharp.chestsort2.ChestSortUtils.sendPlayerChestBreakErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

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
		if (isChestMarkEvent(event)) {
			markedLocations.put(event.getPlayer().getName(), locationFromEvent(event));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onSignChange(SignChangeEvent event) {
		// TODO Auto-generated method stub
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
			List<SignDAO> signs = Registry.getDataProvider().findAllSignsAt(locationFromEvent(event));
			if (!signs.isEmpty()) {
				event.setCancelled(true);
				ChestSortUtils.sendPlayerSignBreakErrorMessage(event);
			}
		}
	}
}
