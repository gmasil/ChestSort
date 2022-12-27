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

import static de.headshotharp.chestsort.ChestSortUtils.getBlockAt;
import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.StaticConfig.COLOR_NORMAL;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;

public class InventoryUtils {

    private InventoryUtils() {
    }

    public static ItemStack insertIntoChests(DataProvider dp, Server server, ItemStack itemStack, boolean central,
            String username) {
        String material = itemStack.getType().toString();
        List<ChestDAO> chests;
        if (central) {
            chests = dp.chests().findAllCentralChestsByMaterial(material);
        } else {
            chests = dp.chests().findAllChestsByMaterialAndUser(material, username);
        }
        for (ChestDAO chest : chests) {
            Block chestBlock = getBlockAt(server, chest.getLocation());
            if (chestBlock.getType() == Material.CHEST) {
                Chest bukkitChest = (Chest) chestBlock.getState();
                itemStack = bukkitChest.getInventory().addItem(itemStack).get(0);
                if (itemStack == null) {
                    // everything was stored
                    break;
                }
            }
        }
        return itemStack;
    }

    public static void insertAllInventory(DataProvider dp, Server server, Player player, boolean central) {
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length; i++) {
            if (contents[i] != null) {
                contents[i] = insertIntoChests(dp, server, contents[i], central, player.getName());
                if (contents[i] != null && contents[i].getAmount() == 0) {
                    contents[i] = null;
                }
            }
        }
        player.getInventory().setContents(contents);
    }

    public static void insertItemInHand(DataProvider dp, Server server, Player player, boolean central) {
        if (player.getInventory().getItemInMainHand().getType() == Material.AIR) {
            return;
        }
        String material = player.getInventory().getItemInMainHand().getType().toString();
        List<ChestDAO> chests;
        if (central) {
            chests = dp.chests().findAllCentralChestsByMaterial(material);
        } else {
            chests = dp.chests().findAllChestsByMaterialAndUser(material, player.getName());
        }
        if (chests.isEmpty()) {
            player.sendMessage(COLOR_NORMAL + "There are no chests of type " + material);
            return;
        }
        ItemStack itemStack = player.getInventory().getItemInMainHand();
        for (ChestDAO chest : chests) {
            Block chestBlock = getBlockAt(server, chest.getLocation());
            if (chestBlock.getType() != Material.CHEST) {
                player.sendMessage(COLOR_ERROR + "The registered chest at " + chest.getLocation().toHumanString()
                        + " is no chest anymore");
            } else {
                Chest bukkitChest = (Chest) chestBlock.getState();
                itemStack = bukkitChest.getInventory().addItem(itemStack).get(0);
                if (itemStack == null) {
                    // everything was stored
                    break;
                }
            }
        }
        if (itemStack == null) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        } else {
            player.getInventory().setItemInMainHand(itemStack);
            player.sendMessage(COLOR_ERROR + "There is not enough space in the chests");
        }
    }
}
