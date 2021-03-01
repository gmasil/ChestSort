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
package de.headshotharp.chestsort.command.impl;

import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.InventoryUtils;
import de.headshotharp.chestsort.SpigotPlugin;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.DataProvider;

public class AllCommand extends ChestsortCommand {
    private DataProvider dp;

    public AllCommand(SpigotPlugin plugin, DataProvider dp) {
        super(plugin);
        this.dp = dp;
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (args.length != 1) {
            player.sendMessage(COLOR_ERROR + usage());
            return;
        }
        boolean central;
        if (args[0].equalsIgnoreCase("central")) {
            central = true;
        } else if (args[0].equalsIgnoreCase("user")) {
            central = false;
        } else {
            player.sendMessage(COLOR_ERROR + usage());
            return;
        }
        InventoryUtils.insertAllInventory(dp, getServer(), player, central);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList("central", "user");
        } else if (args.length == 1) {
            return Arrays.asList("central", "user").stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new LinkedList<>();
    }

    @Override
    public String usage() {
        return "Usage: /chestsort all <central/user>";
    }

    @Override
    public String getName() {
        return "all";
    }
}
