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
package de.headshotharp.chestsort.utils;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.mockito.Mockito;

import de.headshotharp.chestsort.ChestSortPlugin;

public class PluginMock {

    private World world = mock(World.class);
    private ChestSortPlugin plugin = mock(ChestSortPlugin.class);
    private Server server = mock(Server.class);

    public PluginMock() {
        this("world");
    }

    public PluginMock(String worldName) {
        Mockito.when(world.getName()).thenReturn(worldName);
        Mockito.when(server.getWorld(worldName)).thenReturn(world);
        Mockito.when(plugin.getServer()).thenReturn(server);
        Mockito.when(plugin.getLogger()).thenReturn(Logger.getLogger(ChestSortPlugin.class.getName()));
    }

    public PluginMock withBlockAt(Material material, Location location) {
        return withBlockAt(material, location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public PluginMock withBlockAt(Material material, int x, int y, int z) {
        Location loc = new Location(world, x, y, z);
        Block block = mock(Block.class);
        when(block.getType()).thenReturn(material);
        when(block.getLocation()).thenReturn(loc);
        when(world.getBlockAt(eq(loc))).thenReturn(block);
        when(world.getBlockAt(eq(x), eq(y), eq(z))).thenReturn(block);
        return this;
    }

    public PluginMock withChestAt(Location location) {
        return withChestAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public PluginMock withChestAt(int x, int y, int z) {
        withBlockAt(Material.CHEST, x, y, z);
        Block block = getBlockAt(x, y, z);
        Chest chest = mock(Chest.class);
        when(block.getState()).thenReturn(chest);
        Inventory inventory = mock(Inventory.class);
        when(chest.getInventory()).thenReturn(inventory);
        return this;
    }

    public Block getBlockAt(Location location) {
        return getBlockAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Block getBlockAt(int x, int y, int z) {
        return world.getBlockAt(x, y, z);
    }

    public Chest getChestAt(Location location) {
        return getChestAt(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public Chest getChestAt(int x, int y, int z) {
        return (Chest) world.getBlockAt(x, y, z).getState();
    }

    public World getWorld() {
        return world;
    }

    public ChestSortPlugin getPlugin() {
        return plugin;
    }

    public Server getServer() {
        return server;
    }

    public ChestSortPlugin build() {
        return getPlugin();
    }
}
