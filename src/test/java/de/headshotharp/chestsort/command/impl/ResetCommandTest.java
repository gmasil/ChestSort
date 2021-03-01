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
package de.headshotharp.chestsort.command.impl;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.StaticConfig;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.testutils.ConfigureH2Hibernate;

@ConfigureH2Hibernate
@Story("The reset command implementation is tested")
class ResetCommandTest extends GherkinTest {
    public static ResetCommand resetCommand;

    @BeforeAll
    public static void init(DataProvider dp) {
        resetCommand = new ResetCommand(null, dp);
    }

    @Scenario("Dryrun will not delete anything")
    void testDryrunWillNotDeleteAnything(DataProvider dp) {
        ResetCommand resetCommand = new ResetCommand(null, dp);
        given("a central chest exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 1, 2, "GOLD"));
        });
        and("two user chests for Peter exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 0, 0, "STONE", "Peter"));
            dp.persistChest(new ChestDAO("world", 0, 0, 1, "COBBLESTONE", "Peter"));
        });
        when("Peter executes command /chestsort reset user chests", () -> {
            Player player = mock(Player.class);
            Mockito.when(player.getName()).thenReturn("Peter");
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_MANAGE))).thenReturn(true);
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_RESET))).thenReturn(false);
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                assertThat(arg, containsString("Would delete 0 signs and 2 chests"));
                return null;
            }).when(player).sendMessage(anyString());
            resetCommand.execute(player, "reset", "user", "chests");
        });
        then("there are still two user chests", () -> {
            List<ChestDAO> chests = dp.findAllChestsByUser("Peter");
            assertThat(chests, hasSize(2));
        });
        and("there is still a central chest", () -> {
            List<ChestDAO> chests = dp.findAllCentralChests();
            assertThat(chests, hasSize(1));
        });
    }

    @Scenario("Resetting user chests will not delete central chests")
    void testUserResetWillKeepCentral(DataProvider dp) {
        given("a central chest exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 1, 2, "GOLD"));
        });
        and("two user chests for Peter exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 0, 0, "STONE", "Peter"));
            dp.persistChest(new ChestDAO("world", 0, 0, 1, "COBBLESTONE", "Peter"));
        });
        when("Peter executes command /chestsort reset user chests confirm", () -> {
            Player player = mock(Player.class);
            Mockito.when(player.getName()).thenReturn("Peter");
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_MANAGE))).thenReturn(true);
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_RESET))).thenReturn(false);
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                assertThat(arg, containsString("Deleted 0 signs and 2 chests"));
                return null;
            }).when(player).sendMessage(anyString());
            resetCommand.execute(player, "reset", "user", "chests", "confirm");
        });
        then("there are no more user chests", () -> {
            List<ChestDAO> chests = dp.findAllChestsByUser("Peter");
            assertThat(chests, hasSize(0));
        });
        and("there is still a central chest", () -> {
            List<ChestDAO> chests = dp.findAllCentralChests();
            assertThat(chests, hasSize(1));
        });
    }

    @Scenario("Resetting central chests will not delete user chests")
    void testCentralResetWillKeepUser(DataProvider dp) {
        given("a central chest exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 1, 2, "GOLD"));
        });
        and("two user chests for Peter exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 0, 0, "STONE", "Peter"));
            dp.persistChest(new ChestDAO("world", 0, 0, 1, "COBBLESTONE", "Peter"));
        });
        when("Peter executes command /chestsort reset central all confirm", () -> {
            Player player = mock(Player.class);
            Mockito.when(player.getName()).thenReturn("Peter");
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_MANAGE))).thenReturn(true);
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_RESET))).thenReturn(true);
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                assertThat(arg, containsString("Deleted 0 signs and 1 chests"));
                return null;
            }).when(player).sendMessage(anyString());
            resetCommand.execute(player, "reset", "central", "all", "confirm");
        });
        then("there are no more central chests", () -> {
            List<ChestDAO> chests = dp.findAllCentralChests();
            assertThat(chests, hasSize(0));
        });
        and("there are still two user chests", () -> {
            List<ChestDAO> chests = dp.findAllChestsByUser("Peter");
            assertThat(chests, hasSize(2));
        });
    }

    @Scenario("Resetting user chests without permission is canceled")
    void testResetUserWithoutPermissions(DataProvider dp) {
        given("a central chest exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 1, 2, "GOLD"));
        });
        and("two user chests for Peter exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 0, 0, "STONE", "Peter"));
            dp.persistChest(new ChestDAO("world", 0, 0, 1, "COBBLESTONE", "Peter"));
        });
        when("Peter executes command /chestsort reset user all confirm", () -> {
            Player player = mock(Player.class);
            Mockito.when(player.getName()).thenReturn("Peter");
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_MANAGE))).thenReturn(false);
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_RESET))).thenReturn(false);
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                assertThat(arg, containsString("You don't have permissions to manage ChestSort"));
                return null;
            }).when(player).sendMessage(anyString());
            resetCommand.execute(player, "reset", "user", "all", "confirm");
        });
        then("there is still a central chest", () -> {
            List<ChestDAO> chests = dp.findAllCentralChests();
            assertThat(chests, hasSize(1));
        });
        then("there are still two user chests", () -> {
            List<ChestDAO> chests = dp.findAllChestsByUser("Peter");
            assertThat(chests, hasSize(2));
        });
    }

    @Scenario("Resetting central chests will not work without permissions")
    void testResettingWithoutPermissions(DataProvider dp) {
        given("a central chest exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 1, 2, "GOLD"));
        });
        and("two user chests for Peter exist", () -> {
            dp.persistChest(new ChestDAO("world", 0, 0, 0, "STONE", "Peter"));
            dp.persistChest(new ChestDAO("world", 0, 0, 1, "COBBLESTONE", "Peter"));
        });
        when("Peter executes command /chestsort reset central chests confirm", () -> {
            Player player = mock(Player.class);
            Mockito.when(player.getName()).thenReturn("Peter");
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_MANAGE))).thenReturn(true);
            Mockito.when(player.hasPermission(eq(StaticConfig.PERMISSION_RESET))).thenReturn(false);
            doAnswer(invocation -> {
                String arg = "" + invocation.getArgument(0);
                assertThat(arg, containsString("4You don't have permissions to reset ChestSort"));
                return null;
            }).when(player).sendMessage(anyString());
            resetCommand.execute(player, "reset", "central", "chests", "confirm");
        });
        then("there are no more user chests", () -> {
            List<ChestDAO> chests = dp.findAllChestsByUser("Peter");
            assertThat(chests, hasSize(2));
        });
        and("there is still a central chest", () -> {
            List<ChestDAO> chests = dp.findAllCentralChests();
            assertThat(chests, hasSize(1));
        });
    }

    @Test
    void testNotEnoughArguments() {
        Player player = mock(Player.class);
        Mockito.when(player.getName()).thenReturn("Peter");
        doAnswer(invocation -> {
            String arg = "" + invocation.getArgument(0);
            assertThat(arg, containsString(resetCommand.usage()));
            return null;
        }).when(player).sendMessage(anyString());
        resetCommand.execute(player, "reset");
        resetCommand.execute(player, "reset", "central", "");
        resetCommand.execute(player, "reset", "user", "");
        resetCommand.execute(player, "reset", "unknown", "");
        resetCommand.execute(player, "reset", "unknown", "all");
        resetCommand.execute(player, "reset", "unknown", "chests");
        resetCommand.execute(player, "reset", "unknown", "signs");
        resetCommand.execute(player, "reset", "unknown", "unknown");
    }

    @Test
    void testOnTabComplete() {
        assertThat(getTabComplete(), contains("central", "user"));
        assertThat(getTabComplete("c"), contains("central"));
        assertThat(getTabComplete("cent"), contains("central"));
        assertThat(getTabComplete("cENT"), contains("central"));
        assertThat(getTabComplete("central"), contains("central"));
        assertThat(getTabComplete("centralsd"), hasSize(0));
        assertThat(getTabComplete("central", ""), contains("all", "chests", "signs"));
        assertThat(getTabComplete("central", "a"), contains("all"));
        assertThat(getTabComplete("central", "chests"), contains("chests"));
        assertThat(getTabComplete("central", "chests", ""), hasSize(0));
        assertThat(getTabComplete("central", "chests", "conf"), hasSize(0));
        assertThat(getTabComplete("central", "chests", "confirm"), hasSize(0));
    }

    @Test
    void testUsage() {
        assertThat(resetCommand.usage(),
                is(equalTo("Usage: /chestsort reset <central/user> <all/chests/signs> [confirm]")));
    }

    private List<String> getTabComplete(String... args) {
        return resetCommand.onTabComplete(null, "reset", args);
    }
}
