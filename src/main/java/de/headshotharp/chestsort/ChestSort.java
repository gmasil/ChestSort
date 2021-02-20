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
package de.headshotharp.chestsort;

import java.io.IOException;

import org.bukkit.event.Listener;

public class ChestSort extends SpigotPlugin implements Listener {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            Registry.getConfigService().readConfig();
        } catch (IOException e) {
            error("Error while loading config", e);
            return;
        }
        Registry.setSpigotPlugin(this);
        try {
            Registry.getHibernateUtils().setDatabaseConfig(Registry.getConfigService().getConfig().getDatabase());
            Registry.getHibernateUtils().getSessionFactory();
        } catch (Exception e) {
            error("Error while connecting to database", e);
            return;
        }
        try {
            getCommand("chestsort").setExecutor(Registry.getCommandRegistry());
            getCommand("chestsort").setTabCompleter(Registry.getCommandRegistry());
            getServer().getPluginManager().registerEvents(Registry.getPlayerEventListener(), this);
            Registry.getCommandRegistry().registerDefaultCommands();
        } catch (Exception e) {
            error("Error while registering commands", e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        try {
            Registry.getConfigService().saveDefaultConfig();
        } catch (IOException e) {
            error("Error while saving default config", e);
        }
    }
}
