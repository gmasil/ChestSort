package de.headshotharp.chestsort;

import java.util.logging.Level;

import org.bukkit.plugin.java.JavaPlugin;

public class SpigotPlugin extends JavaPlugin {
    public void error(String msg) {
        getLogger().log(Level.INFO, msg);
    }

    public void warn(String msg) {
        getLogger().log(Level.INFO, msg);
    }

    public void info(String msg) {
        getLogger().log(Level.INFO, msg);
    }

    public void error(String msg, Throwable t) {
        getLogger().log(Level.INFO, msg, t);
    }

    public void warn(String msg, Throwable t) {
        getLogger().log(Level.INFO, msg, t);
    }

    public void info(String msg, Throwable t) {
        getLogger().log(Level.INFO, msg, t);
    }
}
