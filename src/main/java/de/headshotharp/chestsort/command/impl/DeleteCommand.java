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

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.PlayerEventListener;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class DeleteCommand extends ChestsortCommand {

    private DataProvider dp;
    private PlayerEventListener listener;

    public DeleteCommand(ChestSortPlugin plugin, DataProvider dp, PlayerEventListener listener) {
        super(plugin);
        this.dp = dp;
        this.listener = listener;
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (args.length < 1 || args.length > 2) {
            sendusage(player);
            return;
        }
        if (!Arrays.asList(WH_CENTRAL, WH_USER).contains(args[0].toLowerCase())) {
            sendusage(player);
            return;
        }
        if (args[0].equalsIgnoreCase(WH_USER)) {
            // check if user has normal manage permissions
            if (!player.hasPermission(PERMISSION_MANAGE)) {
                player.sendMessage(COLOR_ERROR + "You dont have permissions to manage chests");
                return;
            }
        } else {
            // check if user has central manage permissions
            if (!player.hasPermission(PERMISSION_MANAGE_CENTRAL)) {
                player.sendMessage(COLOR_ERROR + "You dont have permissions to manage central chests");
                return;
            }
        }
        // here the user is allowed to perform the command
        Location markedBlock = listener.getMarkedLocation(player.getName());
        if (markedBlock == null) {
            player.sendMessage(COLOR_ERROR
                    + "You have to mark a chest or sign first. Right click a chest or sign with a stick in your main hand");
            return;
        }
        // get all chests/signs
        List<SignDAO> signs = dp.findAllSignsAt(markedBlock);
        List<ChestDAO> chests = dp.findAllChestsAt(markedBlock);
        // filter for user/central depending on command
        if (args[0].equalsIgnoreCase(WH_USER)) {
            signs = signs.stream().filter(sign -> !sign.isCentral())
                    .filter(sign -> sign.getUsername().equals(player.getName())).collect(Collectors.toList());
            chests = chests.stream().filter(chest -> !chest.isCentral())
                    .filter(chest -> chest.getUsername().equals(player.getName())).collect(Collectors.toList());
        } else {
            signs = signs.stream().filter(SignDAO::isCentral).collect(Collectors.toList());
            chests = chests.stream().filter(ChestDAO::isCentral).collect(Collectors.toList());
        }
        if (isDeletionConfirmed(args)) {
            if (signs.isEmpty() && chests.isEmpty()) {
                player.sendMessage(COLOR_NORMAL + "There are no more chests or signs at your marked location");
            } else {

                player.sendMessage(COLOR_NORMAL + String.format("Deleting %d signs", signs.size()));
                signs.forEach(dp::deleteSign);
                player.sendMessage(COLOR_NORMAL + String.format("Deleting %d chests", chests.size()));
                chests.forEach(chest -> {
                    player.sendMessage(COLOR_NORMAL + "Deleting " + chest.getTextBlockString());
                    dp.deleteChest(chest);
                });
                if (verifyDeletionSuccess(dp, markedBlock)) {
                    player.sendMessage(COLOR_GOOD + "All chests and signs at this location have been deleted");
                    Block block = getBlockAt(getServer(), markedBlock);
                    if (isSign(block)) {
                        block.breakNaturally();
                    }
                } else {
                    player.sendMessage(
                            COLOR_ERROR + "There are still registered signs/chests at your marked location.");
                    player.sendMessage(COLOR_ERROR
                            + "This might happen when you delete a user chest, but the same chest is also registered as a central chest.");
                }
            }
        } else {
            if (signs.isEmpty() && chests.isEmpty()) {
                player.sendMessage(COLOR_NORMAL + "There are no more chests or signs at your marked location");
            } else {
                player.sendMessage(COLOR_NORMAL + String.format("Would delete %d signs", signs.size()));
                player.sendMessage(COLOR_NORMAL + String.format("Would delete %d chests", chests.size()));
                chests.forEach(
                        chest -> player.sendMessage(COLOR_NORMAL + "Would delete " + chest.getTextBlockString()));
                player.sendMessage(COLOR_NORMAL + "This was a dryrun only. Confirm command with " + COLOR_GOOD
                        + "/chestsort delete " + args[0].toLowerCase() + " confirm");
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList(WH_CENTRAL, WH_USER);
        } else if (args.length == 1) {
            return Arrays.asList(WH_CENTRAL, WH_USER).stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }
        // no autocomplete for confirm
        return new LinkedList<>();
    }

    @Override
    public String usage() {
        return "Usage: /chestsort delete <central/user> [confirm]";
    }

    @Override
    public String getName() {
        return "delete";
    }

    private void sendusage(Player player) {
        player.sendMessage(COLOR_ERROR + usage());
    }

    private boolean verifyDeletionSuccess(DataProvider dp, Location markedChest) {
        List<SignDAO> signs = dp.findAllSignsAt(markedChest);
        List<ChestDAO> chests = dp.findAllChestsAt(markedChest);
        return signs.isEmpty() && chests.isEmpty();
    }

    private boolean isDeletionConfirmed(String... args) {
        return args.length > 1 && args[1].equalsIgnoreCase("confirm");
    }
}
