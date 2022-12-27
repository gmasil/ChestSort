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
package de.headshotharp.chestsort.hibernate;

import java.util.List;

import org.hibernate.SessionFactory;

import de.headshotharp.plugin.hibernate.HibernateUtils;
import de.headshotharp.plugin.hibernate.config.HibernateConfig;

public class DataProvider {

    public static final String WH_LOCATION = "location";
    public static final String WH_WORLD = "world";
    public static final String WH_X = "x";
    public static final String WH_Y = "y";
    public static final String WH_Z = "z";
    public static final String WH_MATERIAL = "material";
    public static final String WH_CENTRAL = "central";
    public static final String WH_USERNAME = "username";

    private ChestDataProvider chestDataProvider;
    private SignDataProvider signDataProvider;

    public DataProvider(SessionFactory sessionFactory) {
        chestDataProvider = new ChestDataProvider(sessionFactory);
        signDataProvider = new SignDataProvider(sessionFactory);
    }

    public DataProvider(HibernateConfig hibernateConfig, Class<?> baseClass) {
        this(new HibernateUtils(hibernateConfig, baseClass).createSessionFactory());
    }

    public DataProvider(HibernateConfig hibernateConfig, List<Class<?>> daoClasses) {
        this(new HibernateUtils(hibernateConfig, daoClasses).createSessionFactory());
    }

    public ChestDataProvider chests() {
        return chestDataProvider;
    }

    public SignDataProvider signs() {
        return signDataProvider;
    }
}
