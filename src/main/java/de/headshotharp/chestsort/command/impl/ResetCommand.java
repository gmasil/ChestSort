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

import static de.headshotharp.chestsort.config.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.config.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.config.StaticConfig.COLOR_NORMAL;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.config.StaticConfig;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.util.ChestSortUtils;

public class ResetCommand extends ChestsortCommand {

    private DataProvider dp;

    public ResetCommand(ChestSortPlugin plugin, DataProvider dp) {
        super(plugin);
        this.dp = dp;
    }

    @Override
    public boolean execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (args.length < 2) {
            return false;
        }
        if (!args[1].equalsIgnoreCase(WH_ALL) && !args[1].equalsIgnoreCase(WH_CHESTS)
                && !args[1].equalsIgnoreCase(WH_SIGNS)) {
            return false;
        }
        boolean central;
        if (args[0].equalsIgnoreCase(WH_CENTRAL)) {
            central = true;
            if (!player.hasPermission(StaticConfig.PERMISSION_RESET)) {
                player.sendMessage(COLOR_ERROR + "You don't have permissions to reset ChestSort");
                return true;
            }
        } else if (args[0].equalsIgnoreCase(WH_USER)) {
            central = false;
            if (!player.hasPermission(StaticConfig.PERMISSION_MANAGE)) {
                player.sendMessage(COLOR_ERROR + "You don't have permissions to manage ChestSort");
                return true;
            }
        } else {
            return false;
        }
        List<ChestDAO> chests = new LinkedList<>();
        if (args[1].equalsIgnoreCase(WH_ALL) || args[1].equalsIgnoreCase(WH_CHESTS)) {
            chests = dp.chests().findAllChestsByMaterialAndUser(null, central ? null : player.getName());
        }
        List<SignDAO> signs = new LinkedList<>();
        if (args[1].equalsIgnoreCase(WH_ALL) || args[1].equalsIgnoreCase(WH_SIGNS)) {
            signs = dp.signs().findAllSignsByUser(central ? null : player.getName());
        }
        if (isDeletionConfirmed(args)) {
            for (ChestDAO chest : chests) {
                dp.chests().delete(chest);
            }
            for (SignDAO sign : signs) {
                dp.signs().delete(sign);
                Block block = ChestSortUtils.getBlockAt(getServer(), sign.getLocation());
                if (ChestSortUtils.isSign(block)) {
                    block.breakNaturally();
                }
            }
            player.sendMessage(COLOR_GOOD + "Deleted " + signs.size() + " signs and " + chests.size() + " chests.");
        } else {
            player.sendMessage(
                    COLOR_NORMAL + "Would delete " + signs.size() + " signs and " + chests.size() + " chests.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList(WH_CENTRAL, WH_USER);
        } else if (args.length == 1) {
            return Arrays.asList(WH_CENTRAL, WH_USER).stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .toList();
        } else if (args.length == 2) {
            return Arrays.asList(WH_ALL, WH_CHESTS, WH_SIGNS).stream()
                    .filter(cmd -> cmd.startsWith(args[1].toLowerCase())).toList();
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
