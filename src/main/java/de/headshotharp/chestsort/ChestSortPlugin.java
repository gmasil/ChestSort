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

import java.io.File;
import java.io.IOException;

import de.headshotharp.chestsort.config.Config;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.listener.PlayerEventListener;
import de.headshotharp.plugin.base.LoggablePlugin;
import de.headshotharp.plugin.base.command.CommandRegistry;
import de.headshotharp.plugin.base.config.ConfigService;

public class ChestSortPlugin extends LoggablePlugin {

    private ConfigService<Config> configService;

    @Override
    public void onEnable() {
        configService = new ConfigService<>(Config.class, new File("plugins/ChestSort/config.yaml"));
        saveDefaultConfig();
        Config config;
        try {
            config = configService.readConfig();
        } catch (IOException e) {
            throw new IllegalStateException("Error while loading config", e);
        }
        DataProvider dp;
        try {
            dp = new DataProvider(config.getDatabase(), ChestSortPlugin.class);
            info("Connected to database");
        } catch (Exception e) {
            throw new IllegalStateException("Error while connecting to database", e);
        }
        try {
            PlayerEventListener playerEventListener = new PlayerEventListener(dp, this);
            CommandRegistry<ChestSortPlugin> commandRegistry = new CommandRegistry<>(this, ChestSortPlugin.class, dp,
                    playerEventListener);
            getCommand("chestsort").setExecutor(commandRegistry);
            getCommand("chestsort").setTabCompleter(commandRegistry);
            getServer().getPluginManager().registerEvents(playerEventListener, this);
        } catch (Exception e) {
            throw new IllegalStateException("Error while registering commands", e);
        }
    }

    @Override
    public void saveDefaultConfig() {
        try {
            if (!configService.getConfigFile().exists()) {
                configService.saveConfig(Config.getDefaultConfig());
            }
        } catch (IOException e) {
            warn("Could not save default config", e);
        }
    }
}
