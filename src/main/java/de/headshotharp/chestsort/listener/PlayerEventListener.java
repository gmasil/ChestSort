/**
 * ChestSort
 * Copyright © 2021 gmasil.de
 *
 * This file is part of ChestSort.
 *
 * ChestSort is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ChestSort is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChestSort. If not, see <https://www.gnu.org/licenses/>.
 */
package de.headshotharp.chestsort.listener;

import static de.headshotharp.chestsort.config.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.config.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.config.StaticConfig.MATERIAL_SIGN_CENTRAL;
import static de.headshotharp.chestsort.config.StaticConfig.MATERIAL_SIGN_USER;
import static de.headshotharp.chestsort.config.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.config.StaticConfig.PERMISSION_MANAGE_CENTRAL;
import static de.headshotharp.chestsort.util.ChestSortUtils.convertLocation;
import static de.headshotharp.chestsort.util.ChestSortUtils.hasMarkPermission;
import static de.headshotharp.chestsort.util.ChestSortUtils.isChestBreaked;
import static de.headshotharp.chestsort.util.ChestSortUtils.isChestClicked;
import static de.headshotharp.chestsort.util.ChestSortUtils.isMarkerInHand;
import static de.headshotharp.chestsort.util.ChestSortUtils.isRightClickBlock;
import static de.headshotharp.chestsort.util.ChestSortUtils.isSignBreaked;
import static de.headshotharp.chestsort.util.ChestSortUtils.isSignClicked;
import static de.headshotharp.chestsort.util.ChestSortUtils.locationFromEvent;
import static de.headshotharp.chestsort.util.ChestSortUtils.sendPlayerChestBreakErrorMessage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import de.headshotharp.chestsort.util.ChestSortUtils;
import de.headshotharp.chestsort.util.InventoryUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerEventListener implements Listener {

    private static final String SIGN_TITLE = "[ChestSort]";
    private Map<String, Location> markedLocations = new HashMap<>();
    private Map<String, Location> previouslyMarkedLocations = new HashMap<>();
    private DataProvider dp;
    private ChestSortPlugin plugin;

    public PlayerEventListener(DataProvider dp, ChestSortPlugin plugin) {
        this.dp = dp;
        this.plugin = plugin;
    }

    public Location getMarkedLocation(String player) {
        return markedLocations.get(player);
    }

    public Location getPreviouslyMarkedLocation(String player) {
        return previouslyMarkedLocations.get(player);
    }

    public void setMarkedLocation(String player, Location location) {
        Location previousLocation = markedLocations.get(player);
        if (previousLocation != null) {
            previouslyMarkedLocations.put(player, previousLocation);
        }
        markedLocations.put(player, location);
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (isMarkerInHand(event)) {
            processMarkEvent(event);
        } else if (isRightClickBlock(event) && isSignClicked(event)) {
            processInsert(event);
        }
    }

    private void processMarkEvent(PlayerInteractEvent event) {
        if (hasMarkPermission(event.getPlayer()) && isRightClickBlock(event)) {
            String mark = null;
            if (isChestClicked(event)) {
                mark = "chest";
            } else if (isSignClicked(event)) {
                mark = "sign";
            }
            if (mark != null) {
                Location location = locationFromEvent(event);
                setMarkedLocation(event.getPlayer().getName(), location);
                event.setCancelled(true);
                event.getPlayer().sendMessage(COLOR_GOOD + "Marked " + mark + " at " + location.toHumanString());
            }
        }
    }

    private void processInsert(PlayerInteractEvent event) {
        List<SignDAO> signs = dp.signs().findAllSignsAt(locationFromEvent(event));
        if (signs.size() == 1) {
            InventoryUtils.insertItemInHand(dp, plugin.getServer(), event.getPlayer(), signs.get(0).isCentral());
        } else if (signs.size() > 1) {
            event.getPlayer().sendMessage(COLOR_ERROR
                    + "Multiple registered signs found at this location. Please delete and recreate the sign");
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onSignChange(SignChangeEvent event) {
        SignDAO sign = createSign(event);
        if (sign != null) {
            // delete all possible previous signs at the location
            dp.signs().findAllSignsAt(sign.getLocation()).forEach(dp.signs()::delete);
            // save sign
            dp.signs().persist(sign);
            // check
            List<SignDAO> signs = dp.signs().findSign(sign);
            if (signs.isEmpty()) {
                event.getPlayer()
                        .sendMessage(COLOR_ERROR + "Error while saving sign to database, this should never occur");
            } else {
                event.getPlayer().sendMessage(COLOR_GOOD + "ChestSort sign successfully created");
            }
        }
    }

    public SignDAO createSign(SignChangeEvent event) {
        if (event.line(0) instanceof TextComponent textComponent && textComponent.content().equals(SIGN_TITLE)) {
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
                updateSignText(event);
                return new SignDAO(convertLocation(event.getBlock().getLocation()));
            } else if (event.getBlock().getType().equals(MATERIAL_SIGN_USER)) {
                if (!event.getPlayer().hasPermission(PERMISSION_MANAGE)) {
                    event.getPlayer().sendMessage(COLOR_ERROR + "You dont have permissions to manage the warehouse");
                    event.setCancelled(true);
                    event.getBlock().breakNaturally();
                    return null;
                }
                updateSignText(event, event.getPlayer().getName());
                return new SignDAO(convertLocation(event.getBlock().getLocation()), event.getPlayer().getName());
            }
        }
        return null;
    }

    /**
     * Update sign text for central sign
     *
     * @param event
     */
    private void updateSignText(SignChangeEvent event) {
        updateSignText(event, null);
    }

    /**
     * Update sign text for user if username is present, for central otherwise
     *
     * @param event
     * @param username
     */
    private void updateSignText(SignChangeEvent event, String username) {
        event.line(0, Component.empty().color(NamedTextColor.BLUE).content(SIGN_TITLE));
        if (username != null) {
            event.line(1, Component.empty().color(NamedTextColor.GREEN).content(username));
        } else {
            event.line(1, Component.empty().color(NamedTextColor.RED).content("Central"));
        }
        event.line(2, Component.empty().content("rightclick to"));
        event.line(3, Component.empty().content("insert a block"));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onBlockBreak(BlockBreakEvent event) {
        if (isChestBreaked(event)) {
            List<ChestDAO> chests = dp.chests().findAllChestsAt(locationFromEvent(event));
            if (!chests.isEmpty()) {
                event.setCancelled(true);
                sendPlayerChestBreakErrorMessage(event, chests);
            }
        }
        if (isSignBreaked(event)) {
            Location loc = locationFromEvent(event);
            List<SignDAO> signs = dp.signs().findAllSignsAt(loc);
            if (!signs.isEmpty()) {
                event.setCancelled(true);
                ChestSortUtils.sendPlayerSignBreakErrorMessage(event);
            }
        }
    }
}
