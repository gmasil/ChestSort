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
import static de.headshotharp.chestsort.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.StaticConfig.COLOR_NORMAL;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.Registry;
import de.headshotharp.chestsort.StaticConfig;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;

public class ResetCommand implements ChestsortCommand {
    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (args.length < 2) {
            player.sendMessage(COLOR_ERROR + usage());
            return;
        }
        if (!args[1].equalsIgnoreCase("all") && !args[1].equalsIgnoreCase("chests")
                && !args[1].equalsIgnoreCase("signs")) {
            player.sendMessage(COLOR_ERROR + usage());
            return;
        }
        boolean central;
        if (args[0].equalsIgnoreCase("central")) {
            central = true;
            if (!player.hasPermission(StaticConfig.PERMISSION_RESET)) {
                player.sendMessage(COLOR_ERROR + "You don't have permissions to reset ChestSort");
                return;
            }
        } else if (args[0].equalsIgnoreCase("user")) {
            central = false;
            if (!player.hasPermission(StaticConfig.PERMISSION_MANAGE)) {
                player.sendMessage(COLOR_ERROR + "You don't have permissions to manage ChestSort");
                return;
            }
        } else {
            player.sendMessage(COLOR_ERROR + usage());
            return;
        }
        DataProvider dp = Registry.getDataProvider();
        List<ChestDAO> chests = new LinkedList<>();
        if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("chests")) {
            chests = dp.findAllChestsByMaterialAndUser(null, central ? null : player.getName());
        }
        List<SignDAO> signs = new LinkedList<>();
        if (args[1].equalsIgnoreCase("all") || args[1].equalsIgnoreCase("signs")) {
            signs = dp.findAllSignsByUser(central ? null : player.getName());
        }
        if (isDeletionConfirmed(args)) {
            for (ChestDAO chest : chests) {
                dp.deleteChest(chest);
            }
            for (SignDAO sign : signs) {
                dp.deleteSign(sign);
            }
            player.sendMessage(COLOR_GOOD + "Deleted " + signs.size() + " signs and " + chests.size() + " chests.");
        } else {
            player.sendMessage(
                    COLOR_NORMAL + "Would delete " + signs.size() + " signs and " + chests.size() + " chests.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList("central", "user");
        } else if (args.length == 1) {
            return Arrays.asList("central", "user").stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList("all", "chests", "signs").stream().filter(cmd -> cmd.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }
        // no autocomplete for confirm
        return new LinkedList<>();
    }

    @Override
    public String usage() {
        return "Usage: /chestsort reset <central/user> <all/chests/signs> [confirm]";
    }

    @Override
    public String getName() {
        return "reset";
    }

    private boolean isDeletionConfirmed(String... args) {
        return args.length > 2 && args[2].equalsIgnoreCase("confirm");
    }
}
