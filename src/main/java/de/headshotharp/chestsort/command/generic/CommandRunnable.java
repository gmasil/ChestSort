package de.headshotharp.chestsort.command.generic;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandRunnable {
    public void execute(CommandSender sender, String command, String... args);
}
