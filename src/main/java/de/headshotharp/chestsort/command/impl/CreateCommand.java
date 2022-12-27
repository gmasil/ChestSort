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
import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR_HIGHLIGHT;
import static de.headshotharp.chestsort.StaticConfig.COLOR_GOOD;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.ChestSortUtils;
import de.headshotharp.chestsort.PlayerEventListener;
import de.headshotharp.chestsort.StaticConfig;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class CreateCommand extends ChestsortCommand {

    protected DataProvider dp;
    protected PlayerEventListener listener;

    public CreateCommand(ChestSortPlugin plugin, DataProvider dp, PlayerEventListener listener) {
        super(plugin);
        this.dp = dp;
        this.listener = listener;
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        Material material = verifyParameters(player, args);
        if (material == null) {
            return;
        }
        boolean isCentral = args[0].equalsIgnoreCase(WH_CENTRAL);
        ChestDAO chest = chestByParameter(player, material, isCentral);
        if (chest != null) {
            if (isChestAt(chest.getLocation())) {
                if (dp.chests().findChest(chest).isEmpty()) {
                    dp.chests().persist(chest);
                    if (dp.chests().findChest(chest).isEmpty()) {
                        player.sendMessage(COLOR_ERROR
                                + "The chest could not be persisted in the database, this should never occur");
                    } else {
                        player.sendMessage(
                                COLOR_GOOD + "Chest of type " + chest.getMaterial() + " was created successfully");
                    }
                } else {
                    player.sendMessage(COLOR_ERROR + "Chest is already registered with type " + COLOR_ERROR_HIGHLIGHT
                            + chest.getMaterial());
                }
            } else {
                player.sendMessage(COLOR_ERROR + "There is no chest at the marked location.");
            }
        }
    }

    protected boolean isChestAt(Location loc) {
        Block block = getPlugin().getServer().getWorld(loc.getWorld()).getBlockAt(loc.getX(), loc.getY(), loc.getZ());
        return ChestSortUtils.isChest(block);
    }

    public Material verifyParameters(Player player, String... args) {
        if (args.length != 2) {
            sendusage(player);
            return null;
        }
        if (!Arrays.asList(WH_CENTRAL, WH_USER).contains(args[0].toLowerCase())) {
            sendusage(player);
            return null;
        }
        if (args[0].equalsIgnoreCase(WH_CENTRAL)) {
            if (!player.hasPermission(StaticConfig.PERMISSION_MANAGE_CENTRAL)) {
                player.sendMessage(COLOR_ERROR + "You dont have permissions to manage the central chests");
                if (player.hasPermission(PERMISSION_MANAGE)) {
                    player.sendMessage(COLOR_ERROR
                            + "If you want to create a chest for your personal warehouse please use the command /chestsort create user");
                }
                return null;
            }
        } else {
            if (!player.hasPermission(StaticConfig.PERMISSION_MANAGE)) {
                player.sendMessage(COLOR_ERROR + "You dont have permissions to manage chests");
                return null;
            }
        }
        Optional<Material> optionalMaterial = Arrays.asList(Material.values()).stream()
                .filter(mat -> mat.toString().equalsIgnoreCase(args[1])).findFirst();
        if (!optionalMaterial.isPresent()) {
            player.sendMessage(COLOR_ERROR + "The material " + args[1] + " does not exist");
            sendusage(player);
            return null;
        }
        return optionalMaterial.get();
    }

    public ChestDAO chestByParameter(Player player, Material material, boolean isCentral) {
        Location markedChest = listener.getMarkedLocation(player.getName());
        if (markedChest == null) {
            player.sendMessage(
                    COLOR_ERROR + "You have to mark a chest first. Right click a chest with a stick in your main hand");
            return null;
        }
        return new ChestDAO(markedChest, material.toString(), isCentral ? null : player.getName());
    }

    private void sendusage(Player player) {
        player.sendMessage(COLOR_ERROR + usage());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        if (args.length == 0) {
            return Arrays.asList(WH_CENTRAL, WH_USER);
        } else if (args.length == 1) {
            return Arrays.asList(WH_CENTRAL, WH_USER).stream().filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        } else if (args.length == 2) {
            return Arrays.asList(Material.values()).stream().map(Material::toString).sorted()
                    .filter(mat -> mat.startsWith(args[1].toUpperCase())).collect(Collectors.toList());
        }
        return new LinkedList<>();
    }

    @Override
    public String usage() {
        return "Usage: /chestsort create <central/user> <material>";
    }

    @Override
    public String getName() {
        return "create";
    }
}
