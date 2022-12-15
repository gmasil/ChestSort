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

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class StaticConfig {

    private StaticConfig() {
    }

    public static final ChatColor COLOR_ERROR = ChatColor.DARK_RED;
    public static final ChatColor COLOR_ERROR_HIGHLIGHT = ChatColor.RED;
    public static final ChatColor COLOR_NORMAL = ChatColor.BLUE;
    public static final ChatColor COLOR_GOOD = ChatColor.GREEN;

    public static final String PERMISSION_MANAGE = "chestsort.manage";
    public static final String PERMISSION_MANAGE_CENTRAL = "chestsort.manage.central";
    public static final String PERMISSION_RESET = "chestsort.reset";

    public static final Material MATERIAL_SIGN_CENTRAL = Material.OAK_SIGN;
    public static final Material MATERIAL_SIGN_USER = Material.BIRCH_SIGN;
    public static final Material MATERIAL_MARKER = Material.STICK;

}
