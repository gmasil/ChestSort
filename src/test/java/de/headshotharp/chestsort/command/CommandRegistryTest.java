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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;

import java.util.List;
import java.util.stream.Collectors;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.ChestSortPlugin;
import de.headshotharp.chestsort.config.Config;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.listener.PlayerEventListener;
import de.headshotharp.chestsort.utils.PluginMock;
import de.headshotharp.plugin.base.command.CommandRegistry;

@Story("The command registry implementation is tested")
public class CommandRegistryTest extends GherkinTest {

    @Scenario("The command registry finds all commands during scan")
    void testCommandRegistryFindsAllCommands(Reference<CommandRegistry<ChestSortPlugin>> registry)
            throws InstantiationException, IllegalAccessException {
        given("the command registry is empty", () -> {
        });
        when("the command registry scans for commands", () -> {
            ChestSortPlugin pluginMock = new PluginMock().getPlugin();
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                System.out.println(arg);
                return null;
            }).when(pluginMock).info(anyString());
            // injectables
            DataProvider dp = new DataProvider(Config.getH2Config().getDatabase(), ChestSortPlugin.class);
            PlayerEventListener listener = new PlayerEventListener(dp, pluginMock);
            // create registry
            registry.set(new CommandRegistry<>("chestsort", pluginMock, ChestSortPlugin.class, true, dp, listener));
        });
        then("all commands are found", () -> {
            List<String> commands = registry.get().getCommands().stream().map(c -> c.getName())
                    .collect(Collectors.toList());
            assertThat(commands, containsInAnyOrder("all", "area", "create", "delete", "info", "reset"));
        });
    }
}
