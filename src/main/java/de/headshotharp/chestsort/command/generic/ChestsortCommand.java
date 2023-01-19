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

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.plugin.base.command.generic.ExecutableCommand;

public abstract class ChestsortCommand extends ExecutableCommand<ChestSortPlugin> {

    public static final String WH_USER = "user";
    public static final String WH_CENTRAL = "central";
    public static final String WH_CHESTS = "chests";
    public static final String WH_SIGNS = "signs";
    public static final String WH_ALL = "all";

    protected ChestsortCommand(ChestSortPlugin plugin) {
        super(plugin);
    }

    @Override
    public Server getServer() {
        return getPlugin().getServer();
    }

    @Override
    public boolean isForPlayerOnly() {
        return true;
    }

    @Override
    public boolean isApplicable(CommandSender sender, String command, String... args) {
        return command.equalsIgnoreCase(getName());
    }
}
