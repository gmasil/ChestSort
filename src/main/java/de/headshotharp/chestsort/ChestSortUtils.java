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
package de.headshotharp.chestsort;

import static de.headshotharp.chestsort.StaticConfig.MATERIAL_MARKER;
import static de.headshotharp.chestsort.StaticConfig.MATERIAL_SIGN_CENTRAL;
import static de.headshotharp.chestsort.StaticConfig.MATERIAL_SIGN_USER;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class ChestSortUtils {
    private ChestSortUtils() {
    }

    public static Block getBlockAt(Location location) {
        return Registry.getSpigotPlugin().getServer().getWorld(location.getWorld()).getBlockAt(location.getX(),
                location.getY(), location.getZ());
    }

    public static boolean hasMarkPermission(Player player) {
        return player.hasPermission(PERMISSION_MANAGE) || player.hasPermission(PERMISSION_MANAGE_CENTRAL);
    }

    public static boolean isMarkEvent(PlayerInteractEvent event) {
        return isRightClickBlock(event) && isMarkerInHand(event);
    }

    public static boolean isRightClickBlock(PlayerInteractEvent event) {
        return event.getAction() == Action.RIGHT_CLICK_BLOCK;
    }

    public static boolean isMarkerInHand(PlayerInteractEvent event) {
        return event.getPlayer().getInventory().getItemInMainHand().getType() == MATERIAL_MARKER;
    }

    public static boolean isChestClicked(PlayerInteractEvent event) {
        return isChest(event.getClickedBlock());
    }

    public static boolean isChestBreaked(BlockBreakEvent event) {
        return isChest(event.getBlock());
    }

    public static boolean isChest(Block block) {
        return block.getType() == Material.CHEST;
    }

    public static boolean isSignClicked(PlayerInteractEvent event) {
        return isSign(event.getClickedBlock());
    }

    public static boolean isSignBreaked(BlockBreakEvent event) {
        return isSign(event.getBlock());
    }

    public static boolean isSign(Block block) {
        return block.getType() == MATERIAL_SIGN_CENTRAL || block.getType() == MATERIAL_SIGN_USER;
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

    public static void sendPlayerBlockBelowSignBreakErrorMessage(BlockBreakEvent event) {
        event.getPlayer().sendMessage(ChatColor.RED + "This sign above this block is protected by ChestSort");
    }
}
