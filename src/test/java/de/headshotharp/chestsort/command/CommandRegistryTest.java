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
package de.headshotharp.chestsort.command;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

import java.util.List;
import java.util.stream.Collectors;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;

@Story("The command registry implementation is tested")
public class CommandRegistryTest extends GherkinTest {
    @Scenario("The command registry finds all commands during scan")
    public void testCommandRegistryFindsAllCommands(Reference<CommandRegistry> registry)
            throws InstantiationException, IllegalAccessException {
        given("the command registry is empty", () -> {
            registry.set(new CommandRegistry());
        });
        when("the command registry scans for commands", () -> {
            registry.get().scanCommands();
        });
        then("all commands are found", () -> {
            List<String> commands = registry.get().getCommands().stream().map(c -> c.getName())
                    .collect(Collectors.toList());
            assertThat(commands, containsInAnyOrder("all", "create", "delete", "info", "reset"));
        });
    }
}
