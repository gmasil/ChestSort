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
package de.headshotharp.chestsort.hibernate.testutils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

import de.headshotharp.chestsort.config.Config;
import de.headshotharp.chestsort.hibernate.DataProvider;

public class H2HibernateConfigurationExtension implements BeforeEachCallback, ParameterResolver {

    private DataProvider dp;

    public H2HibernateConfigurationExtension() {
        dp = new DataProvider(Config.getH2Config().getDatabase());
    }

    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        dp.clearAllChests();
        dp.clearAllSigns();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> expectedType = parameterContext.getParameter().getType();
        return expectedType.equals(DataProvider.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        return dp;
    }
}
