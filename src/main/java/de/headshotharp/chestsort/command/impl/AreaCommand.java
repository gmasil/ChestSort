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

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.PlayerEventListener;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Area;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class AreaCommand extends CreateCommand {

    public AreaCommand(ChestSortPlugin plugin, DataProvider dp, PlayerEventListener listener) {
        super(plugin, dp, listener);
    }

    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        Material material = verifyParameters(player, args);
        if (material == null) {
            return;
        }
        Area markedArea = getMarkedArea(player);
        if (markedArea == null) {
            return;
        }
        boolean isCentral = args[0].equalsIgnoreCase(WH_CENTRAL);
        List<ChestDAO> chests = chestsByParameter(player, material, markedArea, isCentral);
        if (chests.isEmpty()) {
            player.sendMessage(COLOR_NORMAL + "There are no chests in the marked area.");
            return;
        }
        int chestsCreated = 0;
        int chestsNotCreated = 0;
        for (ChestDAO chest : chests) {
            if (dp.chests().findChest(chest).isEmpty()) {
                dp.chests().persist(chest);
                if (dp.chests().findChest(chest).isEmpty()) {
                    chestsNotCreated++;
                } else {
                    chestsCreated++;
                }
            }
        }
        if (chestsNotCreated > 0) {
            if (chestsCreated > 0) {
                player.sendMessage(COLOR_ERROR + "Some chests could not be created, this should never happen!");
            } else {
                player.sendMessage(COLOR_ERROR + "The chests could not be persisted in the database");
            }
        } else {
            player.sendMessage(COLOR_GOOD + "In total " + chestsCreated + " chests of type " + material
                    + " have been successfully created");
        }
    }

    public Area getMarkedArea(Player player) {
        Location markedLast = listener.getMarkedLocation(player.getName());
        Location markedFirst = listener.getPreviouslyMarkedLocation(player.getName());
        if (markedLast == null || markedFirst == null) {
            player.sendMessage(COLOR_ERROR
                    + "You have to mark two chest for an area first. Right click a chest with a stick in your main hand");
            return null;
        }
        if (!markedLast.getWorld().equals(markedFirst.getWorld())) {
            player.sendMessage(COLOR_ERROR
                    + "You have marked chests in different worlds. You can only mark an area in a single world.");
            return null;
        }
        return new Area(markedLast, markedFirst);
    }

    public List<ChestDAO> chestsByParameter(Player player, Material material, Area area, boolean isCentral) {
        List<ChestDAO> chests = new LinkedList<>();
        for (int x = area.getLower().getX(); x <= area.getHigher().getX(); x++) {
            for (int y = area.getLower().getY(); y <= area.getHigher().getY(); y++) {
                for (int z = area.getLower().getZ(); z <= area.getHigher().getZ(); z++) {
                    Location loc = new Location(area.getWorld(), x, y, z);
                    if (isChestAt(loc)) {
                        chests.add(new ChestDAO(loc, material.toString(), isCentral ? null : player.getName()));
                    }
                }
            }
        }
        return chests;
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
        return "Usage: /chestsort area <central/user> <material>";
    }

    @Override
    public String getName() {
        return "area";
    }
}
