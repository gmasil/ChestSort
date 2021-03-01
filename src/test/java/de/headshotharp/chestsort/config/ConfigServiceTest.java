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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;

@Story("The ConfigService is able to save and load config files correctly")
public class ConfigServiceTest extends GherkinTest {
    @Scenario("The default configuration can be saved and successfully reloaded")
    public void testDefaultConfigCreation(Reference<ConfigService> configService, Reference<Config> config) {
        given("no config file exists", () -> {
            configService.set(new ConfigService());
            configService.get().setConfigFile(new File("target/plugins/ChestSort", "config.yaml"));
            configService.get().getConfigFile().delete();
        });
        when("the default config is saved", () -> {
            configService.get().saveDefaultConfig();
        });
        and("the saved config is loaded again", () -> {
            config.set(configService.get().readConfig());
        });
        then("the loaded config is identical to the default config", () -> {
            assertThat(config.get(), is(equalTo(configService.get().getDefaultConfig())));
        });
    }
}
