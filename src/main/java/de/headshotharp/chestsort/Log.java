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

public class Log {
    private Log() {
    }

    public static void error(String msg) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().error(msg);
        }
    }

    public static void warn(String msg) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().warn(msg);
        }
    }

    public static void info(String msg) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().info(msg);
        }
    }

    public static void error(String msg, Throwable t) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().error(msg, t);
        }
    }

    public static void warn(String msg, Throwable t) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().warn(msg, t);
        }
    }

    public static void info(String msg, Throwable t) {
        if (Registry.getSpigotPlugin() != null) {
            Registry.getSpigotPlugin().info(msg, t);
        }
    }
}
