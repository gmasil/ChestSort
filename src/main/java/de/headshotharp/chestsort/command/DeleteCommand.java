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
package de.headshotharp.chestsort.command;

import static de.headshotharp.chestsort.ChestSortUtils.getBlockAt;
import static de.headshotharp.chestsort.ChestSortUtils.isSign;
import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.StaticConfig.COLOR_NORMAL;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.Registry;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class DeleteCommand implements ChestsortCommand {
    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (!player.hasPermission(PERMISSION_MANAGE) && !player.hasPermission(PERMISSION_MANAGE_CENTRAL)) {
            player.sendMessage(COLOR_ERROR + "You dont have permissions to manage chests");
            return;
        }
        Location markedBlock = Registry.getPlayerEventListener().getMarkedLocation(player.getName());
        if (markedBlock == null) {
            player.sendMessage(COLOR_ERROR
                    + "You have to mark a chest or sign first. Right click a chest or sign with a stick in your main hand");
            return;
        }
        DataProvider dp = Registry.getDataProvider();
        List<SignDAO> signs = dp.findAllSignsAt(markedBlock);
        List<ChestDAO> chests = dp.findAllChestsAt(markedBlock);
        if (isDeletionConfirmed(args)) {
            if (signs.isEmpty() && chests.isEmpty()) {
                player.sendMessage(COLOR_NORMAL + "There are no more chests or signs at your marked location");
            } else {
                player.sendMessage(COLOR_NORMAL + "Deleting " + signs.size() + " signs");
                signs.forEach(dp::deleteSign);
                player.sendMessage(COLOR_NORMAL + "Deleting " + chests.size() + " chests");
                chests.forEach(chest -> {
                    player.sendMessage(COLOR_NORMAL + "Deleting " + chest.getTextBlockString());
                    dp.deleteChest(chest);
                });
                if (verifyDeletionSuccess(dp, markedBlock)) {
                    player.sendMessage(COLOR_GOOD + "All chests and signs at this location have been deleted");
                    Block block = getBlockAt(markedBlock);
                    if (isSign(block)) {
                        block.breakNaturally();
                    }
                } else {
                    player.sendMessage(COLOR_ERROR + "Error while deleting from database, this should never occur");
                }
            }
        } else {
            if (signs.isEmpty() && chests.isEmpty()) {
                player.sendMessage(COLOR_NORMAL + "There are no more chests or signs at your marked location");
            } else {
                player.sendMessage(COLOR_NORMAL + "Would delete " + signs.size() + " signs");
                player.sendMessage(COLOR_NORMAL + "Would delete " + chests.size() + " chests");
                chests.forEach(
                        chest -> player.sendMessage(COLOR_NORMAL + "Would delete " + chest.getTextBlockString()));
                player.sendMessage(COLOR_NORMAL + "This was a dryrun only. Confirm command with " + COLOR_GOOD
                        + "/chestsort delete confirm");
            }
        }
    }

    @Override
    public boolean isApplicable(CommandSender sender, String command, String... args) {
        return command.equalsIgnoreCase(getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList("confirm");
        } else if (args.length == 1) {
            return Arrays.asList("confirm").stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new LinkedList<>();
    }

    @Override
    public boolean isForPlayerOnly() {
        return true;
    }

    @Override
    public String usage() {
        return "Usage: /chestsort delete [confirm]";
    }

    @Override
    public String getName() {
        return "delete";
    }

    private boolean verifyDeletionSuccess(DataProvider dp, Location markedChest) {
        List<SignDAO> signs = dp.findAllSignsAt(markedChest);
        List<ChestDAO> chests = dp.findAllChestsAt(markedChest);
        return signs.isEmpty() && chests.isEmpty();
    }

    private boolean isDeletionConfirmed(String... args) {
        return args.length > 0 && args[0].equalsIgnoreCase("confirm");
    }
}
