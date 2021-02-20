package de.headshotharp.chestsort.command;

import static de.headshotharp.chestsort.ChestSortUtils.getBlockAt;
import static de.headshotharp.chestsort.ChestSortUtils.isChest;
import static de.headshotharp.chestsort.ChestSortUtils.isSign;
import static de.headshotharp.chestsort.StaticConfig.COLOR_ERROR;
import static de.headshotharp.chestsort.StaticConfig.COLOR_NORMAL;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.headshotharp.chestsort.Registry;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class InfoCommand implements ChestsortCommand {
    @Override
    public void execute(CommandSender sender, String command, String... args) {
        Player player = (Player) sender;
        if (!player.hasPermission(PERMISSION_MANAGE) && !player.hasPermission(PERMISSION_MANAGE_CENTRAL)) {
            player.sendMessage(COLOR_ERROR + "You dont have permissions to manage chests");
            return;
        }
        Location markedBlock = Registry.getPlayerEventListener().getMarkedLocation(player.getName());
        if (markedBlock == null) {
            player.sendMessage(
                    COLOR_ERROR + "You have to mark a chest first. Right click a chest with a stick in your main hand");
        } else {
            Block block = getBlockAt(markedBlock);
            if (isChest(block)) {
                List<ChestDAO> chests = Registry.getDataProvider().findAllChestsAt(markedBlock);
                if (chests.isEmpty()) {
                    player.sendMessage(COLOR_NORMAL + "The marked chest is not registered in ChestSort");
                } else if (chests.size() == 1) {
                    player.sendMessage(COLOR_NORMAL + "The marked chest is a " + chests.get(0).getTextBlockString());
                } else {
                    player.sendMessage(COLOR_NORMAL + "The marked chest is registered multiple times:");
                    chests.forEach(chest -> player.sendMessage(COLOR_NORMAL + "- " + chest.getTextBlockString()));
                }
            } else if (isSign(block)) {
                List<SignDAO> signs = Registry.getDataProvider().findAllSignsAt(markedBlock);
                if (signs.isEmpty()) {
                    player.sendMessage(COLOR_NORMAL + "The marked sign is not registered in ChestSort");
                } else if (signs.size() == 1) {
                    player.sendMessage(COLOR_NORMAL + "The marked sign is a " + signs.get(0).getTextBlockString());
                } else {
                    player.sendMessage(COLOR_NORMAL + "The marked chest is registered multiple times:");
                    signs.forEach(sign -> player.sendMessage(COLOR_NORMAL + "- " + sign.getTextBlockString()));
                }
            } else {
                player.sendMessage(COLOR_ERROR + "There is no sign or chest at the marked location anymore");
            }
        }
    }

    @Override
    public boolean isApplicable(CommandSender sender, String command, String... args) {
        return command.equalsIgnoreCase(getName());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String command, String... args) {
        return new LinkedList<>();
    }

    @Override
    public boolean isForPlayerOnly() {
        return true;
    }

    @Override
    public String usage() {
        return "Usage: /chestsort info";
    }

    @Override
    public String getName() {
        return "info";
    }
}
