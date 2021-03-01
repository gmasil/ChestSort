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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

public class PlayerMock {
    private Player player = mock(Player.class);

    public PlayerMock withName(String name) {
        when(player.getName()).thenReturn(name);
        return this;
    }

    public PlayerMock withPermission(String permission) {
        return withPermission(permission, true);
    }

    public PlayerMock withPermission(String permission, boolean granted) {
        when(player.hasPermission(eq(permission))).thenReturn(granted);
        return this;
    }

    public PlayerMock withInventory(PlayerInventory inventory) {
        Mockito.when(player.getInventory()).thenReturn(inventory);
        return this;
    }

    public PlayerMock withItemInMainHand(Material material) {
        return withItemInMainHand(new ItemStack(material));
    }

    public PlayerMock withItemInMainHand(ItemStack itemStack) {
        if (player.getInventory() == null) {
            withInventory(mock(PlayerInventory.class));
        }
        if (!MockUtil.isMock(player.getInventory())) {
            throw new IllegalStateException("Inventory of mocked player is not a mock");
        }
        Mockito.when(player.getInventory().getItemInMainHand()).thenReturn(itemStack);
        return this;
    }

    public Player build() {
        return player;
    }
}
