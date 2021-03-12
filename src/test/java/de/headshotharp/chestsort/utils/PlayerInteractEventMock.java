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

import static org.mockito.Mockito.mock;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.mockito.Mockito;

public class PlayerInteractEventMock {
    private PlayerInteractEvent event = mock(PlayerInteractEvent.class);

    public PlayerInteractEventMock withPlayer(Player player) {
        Mockito.when(event.getPlayer()).thenReturn(player);
        return this;
    }

    public PlayerInteractEventMock withAction(Action action) {
        Mockito.when(event.getAction()).thenReturn(action);
        return this;
    }

    public PlayerInteractEventMock withClickedBlock(Block block) {
        Mockito.when(event.getClickedBlock()).thenReturn(block);
        return this;
    }

    public PlayerInteractEventMock withClickedBlock(PluginMock pluginMock, Location location) {
        return withClickedBlock(pluginMock.getBlockAt(location));
    }

    public PlayerInteractEventMock withClickedBlock(PluginMock pluginMock, int x, int y, int z) {
        return withClickedBlock(pluginMock.getBlockAt(x, y, z));
    }

    public PlayerInteractEvent build() {
        return event;
    }
}
