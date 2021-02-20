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
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;

@Story("The create command implementation is tested")
class CreateCommandTest extends GherkinTest {
    private CreateCommand createCommand = new CreateCommand();

    @Scenario("the command is only applicable to for text 'create' case insensitive")
    void testIsApplicable() {
        Player player = Mockito.mock(Player.class);
        assertThat(createCommand.isApplicable(player, "item"), is(equalTo(false)));
        assertThat(createCommand.isApplicable(player, "create"), is(equalTo(true)));
        assertThat(createCommand.isApplicable(player, "CreAte"), is(equalTo(true)));
    }

    @Test
    void testOnTabComplete() {
        assertThat(getTabComplete(), contains("central", "user"));
        assertThat(getTabComplete("c"), contains("central"));
        assertThat(getTabComplete("cent"), contains("central"));
        assertThat(getTabComplete("cENT"), contains("central"));
        assertThat(getTabComplete("central"), contains("central"));
        assertThat(getTabComplete("centralsd"), hasSize(0));
        assertThat(getTabComplete("central", "").get(0), is(equalTo(Material.ACACIA_BOAT.toString())));
        assertThat(getTabComplete("central", "san"),
                contains("SAND", "SANDSTONE", "SANDSTONE_SLAB", "SANDSTONE_STAIRS", "SANDSTONE_WALL"));
        assertThat(getTabComplete("central", "SANDST").get(0), is(equalTo(Material.SANDSTONE.toString())));
        assertThat(getTabComplete("central", "COB", ""), hasSize(0));
        assertThat(getTabComplete("central", "COB", "something"), hasSize(0));
    }

    private List<String> getTabComplete(String... args) {
        return createCommand.onTabComplete(null, "create", args);
    }
}
