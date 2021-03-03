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

import de.headshotharp.chestsort.command.CommandRegistry;
import de.headshotharp.chestsort.config.ConfigService;
import de.headshotharp.chestsort.hibernate.DataProvider;

public class ChestSort extends SpigotPlugin implements Listener {
    private ConfigService configService = new ConfigService();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            configService.readConfig();
        } catch (IOException e) {
            error("Error while loading config, stopping", e);
            return;
        }
        DataProvider dp;
        try {
            dp = new DataProvider(configService.getConfig().getDatabase());
            info("Connected to database");
        } catch (Exception e) {
            error("Error while connecting to database, stopping", e);
            return;
        }
        try {
            PlayerEventListener playerEventListener = new PlayerEventListener(dp, this);
            CommandRegistry commandRegistry = new CommandRegistry(this, dp, playerEventListener);
            getCommand("chestsort").setExecutor(commandRegistry);
            getCommand("chestsort").setTabCompleter(commandRegistry);
            getServer().getPluginManager().registerEvents(playerEventListener, this);
        } catch (Exception e) {
            error("Error while registering commands", e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        try {
            configService.saveDefaultConfig();
        } catch (IOException e) {
            error("Error while saving default config", e);
        }
    }
}
