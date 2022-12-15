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
package de.headshotharp.chestsort.config;

import de.headshotharp.plugin.hibernate.config.HibernateConfig;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Config {

    private HibernateConfig database = new HibernateConfig();

    public static Config getDefaultConfig() {
        Config defaultConfig = new Config();
        defaultConfig.getDatabase().setDriver("com.mysql.cj.jdbc.Driver");
        defaultConfig.getDatabase().setDialect("org.hibernate.dialect.MySQLDialect");
        defaultConfig.getDatabase().setUrl("jdbc:mysql://localhost:3306/dbname?useSSL=false");
        defaultConfig.getDatabase().setUsername("user");
        defaultConfig.getDatabase().setPassword("pass");
        return defaultConfig;
    }

    public static Config getH2Config() {
        Config h2Config = new Config();
        h2Config.getDatabase().setDriver("org.h2.Driver");
        h2Config.getDatabase().setDialect("org.hibernate.dialect.H2Dialect");
        h2Config.getDatabase().setUrl("jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        h2Config.getDatabase().setUsername("sa");
        h2Config.getDatabase().setPassword("");
        return h2Config;
    }
}
