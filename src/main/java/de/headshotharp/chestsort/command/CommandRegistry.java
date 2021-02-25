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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.reflections.Reflections;

import de.headshotharp.chestsort.Log;
import de.headshotharp.chestsort.command.generic.ChestsortCommand;

public class CommandRegistry implements CommandExecutor, TabCompleter {
    private List<ChestsortCommand> commands = new LinkedList<>();

    public void scanCommands() throws InstantiationException, IllegalAccessException {
        Reflections reflections = new Reflections("de.headshotharp.chestsort.command.impl");
        Set<Class<? extends ChestsortCommand>> commandClasses = reflections.getSubTypesOf(ChestsortCommand.class);
        for (Class<? extends ChestsortCommand> clazz : commandClasses) {
            ChestsortCommand command = clazz.newInstance();
            commands.add(command);
        }
        String allCommands = String.join(", ", commands.stream().map(c -> c.getName()).collect(Collectors.toList()));
        Log.info(String.format("Registered %d commands: %s", commands.size(), allCommands));
    }

    public List<ChestsortCommand> getCommands() {
        return commands;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command bukkitCommand, String label, String[] originalArgs) {
        if (originalArgs.length > 0) {
            String cmd = originalArgs[0];
            String[] args = moveArgs(originalArgs);
            for (ChestsortCommand command : commands) {
                if (command.isApplicable(sender, cmd, args)) {
                    if (command.isForPlayerOnly() && !(sender instanceof Player)) {
                        sender.sendMessage("The command is for players only");
                    } else {
                        command.execute(sender, cmd, args);
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command bukkitCommand, String alias,
            String[] originalArgs) {
        if (originalArgs.length == 1) {
            return commands.stream().map(cmd -> cmd.getName().toLowerCase())
                    .filter(cmd -> cmd.startsWith(originalArgs[0].toLowerCase())).collect(Collectors.toList());
        } else {
            String cmd = originalArgs[0];
            String[] args = moveArgs(originalArgs);
            for (ChestsortCommand command : commands) {
                if (command.isApplicable(sender, cmd, args)) {
                    return command.onTabComplete(sender, cmd, args);
                }
            }
        }
        return new LinkedList<>();
    }

    private String[] moveArgs(String[] args) {
        if (args.length <= 1) {
            return new String[0];
        }
        String[] newArgs = new String[args.length - 1];
        for (int i = 0; i < newArgs.length; i++) {
            newArgs[i] = args[i + 1];
        }
        return newArgs;
    }
}
