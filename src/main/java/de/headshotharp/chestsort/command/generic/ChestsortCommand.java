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
package de.headshotharp.chestsort.command.generic;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;

import de.headshotharp.chestsort.SpigotPlugin;

public abstract class ChestsortCommand implements CommandRunnable, CommandApplicable, CommandTabCompletable {
    public static final String WH_USER = "user";
    public static final String WH_CENTRAL = "central";
    public static final String WH_CHESTS = "chests";
    public static final String WH_SIGNS = "signs";
    public static final String WH_ALL = "all";

    private SpigotPlugin plugin;

    public ChestsortCommand(SpigotPlugin plugin) {
        this.plugin = plugin;
    }

    public SpigotPlugin getPlugin() {
        return plugin;
    }

    public Server getServer() {
        return plugin.getServer();
    }

    public boolean isForPlayerOnly() {
        return true;
    }

    public abstract String usage();

    public abstract String getName();

    @Override
    public boolean isApplicable(CommandSender sender, String command, String... args) {
        return command.equalsIgnoreCase(getName());
    }
}
