package de.headshotharp.chestsort.command.generic;

import org.bukkit.command.CommandSender;

@FunctionalInterface
public interface CommandApplicable {
    public boolean isApplicable(CommandSender sender, String command, String... args);
}
