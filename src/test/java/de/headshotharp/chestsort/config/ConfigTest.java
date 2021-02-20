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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;

@Story("The config can be serialized to YAML and deserialized to Java object")
public class ConfigTest extends GherkinTest {
    @Scenario("A config is serialized and deserialized without any changes to the data")
    public void testConfigSerialization(Reference<Config> config, Reference<String> serializedConfig,
            Reference<Config> deserializedConfig) {
        final ObjectMapper mapper = new YAMLMapper();
        given("a config object exists with some data", () -> {
            config.set(new Config());
            config.get().getDatabase().setDriver("com.mysql.cj.jdbc.Driver");
            config.get().getDatabase().setDialect("org.hibernate.dialect.MySQL57Dialect");
            config.get().getDatabase().setUrl("jdbc:mysql://localhost:3306/dbname?useSSL=false");
            config.get().getDatabase().setUsername("peter");
            config.get().getDatabase().setPassword("geheim");
        });
        when("the config is serialized to YAML", () -> {
            serializedConfig.set(mapper.writeValueAsString(config.get()));
        });
        and("the YAML config is deserialized back to a Config object", () -> {
            deserializedConfig.set(mapper.readValue(serializedConfig.get(), Config.class));
        });
        then("the original config and the deserialized config are identical", () -> {
            assertThat(config.get(), is(equalTo(deserializedConfig.get())));
        });
    }
}
