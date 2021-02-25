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

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin extends JavaPlugin {
    public void error(String msg) {
        getLogger().log(Level.SEVERE, msg);
    }

    public void warn(String msg) {
        getLogger().log(Level.WARNING, msg);
    }

    public void info(String msg) {
        getLogger().log(Level.INFO, msg);
    }

    public void error(String msg, Throwable t) {
        getLogger().log(Level.SEVERE, msg, t);
    }

    public void warn(String msg, Throwable t) {
        getLogger().log(Level.WARNING, msg, t);
    }

    public void info(String msg, Throwable t) {
        getLogger().log(Level.INFO, msg, t);
    }
}
