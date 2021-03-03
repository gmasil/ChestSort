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

import static de.headshotharp.chestsort.StaticConfig.MATERIAL_MARKER;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.PlayerEventListener;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import de.headshotharp.chestsort.hibernate.testutils.ConfigureH2Hibernate;
import de.headshotharp.chestsort.utils.PlayerMock;

@ConfigureH2Hibernate
@Story("The create command implementation is tested")
class CreateCommandTest extends GherkinTest {
    private static CreateCommand defaultCreateCommand;

    @BeforeAll
    public static void init(DataProvider dp) {
        defaultCreateCommand = new CreateCommand(null, dp, null);
    }

    @Scenario("Execute create command without enough arguments")
    void testCreateWithoutEnoughArguments(DataProvider dp, Reference<List<String>> messages, Reference<Player> player) {
        given("the user Peter exists", () -> {
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create user", () -> {
            new CreateCommand(null, dp, null).execute(player.get(), "create", "user");
        });
        then("the user gets a usage message", () -> {
            assertThat(messages.get().get(0), containsString("4Usage: /chestsort create <central/user> <material>"));
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("Execute create command with nonexistent parameter")
    void testCreateWithNonexistentArgument(DataProvider dp, Reference<List<String>> messages,
            Reference<Player> player) {
        given("the user Peter exists", () -> {
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create -global- STONE", () -> {
            new CreateCommand(null, dp, null).execute(player.get(), "create", "-global-", "STONE");
        });
        then("the user gets a usage message", () -> {
            assertThat(messages.get().get(0), containsString("4Usage: /chestsort create <central/user> <material>"));
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("Execute create command when nothing is marked")
    void testCreateWithoutMarkedLocation(DataProvider dp, Reference<List<String>> messages, Reference<Player> player,
            Reference<CreateCommand> createCommand) {
        given("the user has not marked anything", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp, null);
            createCommand.set(new CreateCommand(null, dp, listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create user STONE", () -> {
            createCommand.get().execute(player.get(), "create", "user", "STONE");
        });
        then("the user is notified to mark a chest first", () -> {
            assertThat(messages.get().get(0), containsString("You have to mark a chest first"));
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("Successfully create a central chest")
    void testCreateCentralChestSuccessfully(DataProvider dp, Reference<List<String>> messages, Reference<Player> player,
            Reference<CreateCommand> createCommand) {
        given("the user Peter has marked a chest at (world, 13, 14, 15)", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp, null);
            listener.setMarkedLocation("Peter", new Location("world", 13, 14, 15));
            createCommand.set(new CreateCommand(null, dp, listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create central STONE", () -> {
            createCommand.get().execute(player.get(), "create", "central", "STONE");
        });
        then("the user is notified that the chest was created", () -> {
            assertThat(messages.get().get(0), containsString("Chest of type STONE was created successfully"));
            assertThat(messages.get(), hasSize(1));
        });
        and("a chest is registered for type STONE at (world, 13, 14, 15)", () -> {
            List<ChestDAO> chests = dp.findAllChestsAt(new Location("world", 13, 14, 15));
            assertThat(chests, hasSize(1));
            assertThat(chests.get(0).getMaterial(), is(equalTo("STONE")));
            assertThat(chests.get(0).isCentral(), is(equalTo(true)));
        });
    }

    @Scenario("Create a central chest when a database error occurs")
    void testCreateCentralChestWiehDatabaseError(Reference<DataProvider> dp, Reference<List<String>> messages,
            Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the database connection has an error", () -> {
            dp.set(mock(DataProvider.class));
        });
        and("the user Peter has marked a chest at (world, 13, 14, 15)", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp.get(), null);
            listener.setMarkedLocation("Peter", new Location("world", 13, 14, 15));
            createCommand.set(new CreateCommand(null, dp.get(), listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create central STONE", () -> {
            createCommand.get().execute(player.get(), "create", "central", "STONE");
        });
        then("the user is notified about a database error", () -> {
            assertThat(messages.get().get(0),
                    containsString("The chest could not be persisted in the database, this should never occur"));
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("Create a central chest with unknown material")
    void testCreateCentralChestWithUnknownMaterial(DataProvider dp, Reference<List<String>> messages,
            Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the user Peter has marked a chest at (world, 13, 14, 15)", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp, null);
            listener.setMarkedLocation("Peter", new Location("world", 13, 14, 15));
            createCommand.set(new CreateCommand(null, dp, listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create central SPECIAL_MATERIAL_UNKNOWN", () -> {
            createCommand.get().execute(player.get(), "create", "central", "SPECIAL_MATERIAL_UNKNOWN");
        });
        then("the user is notified that the material does not exist", () -> {
            assertThat(messages.get().get(0), containsString("The material SPECIAL_MATERIAL_UNKNOWN does not exist"));
        });
        then("the user is notified that the material does not exist", () -> {
            assertThat(messages.get().get(1), containsString(createCommand.get().usage()));
            assertThat(messages.get(), hasSize(2));
        });
    }

    @Scenario("Create a central chest which already exists")
    void testCreateCentralChestWhichAlreadyExists(DataProvider dp, Reference<List<String>> messages,
            Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the user Peter has marked a chest at (world, 13, 14, 15)", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp, null);
            listener.setMarkedLocation("Peter", new Location("world", 13, 14, 15));
            createCommand.set(new CreateCommand(null, dp, listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        and("a central chest of type STONE exists at (world, 13, 14, 15)", () -> {
            dp.persistChest(new ChestDAO("world", 13, 14, 15, "STONE"));
        });
        when("the user executes the command /chestsort create central STONE", () -> {
            createCommand.get().execute(player.get(), "create", "central", "STONE");
        });
        then("the user is notified that a chest with that material already exist", () -> {
            assertThat(messages.get().get(0), containsString("Chest is already registered with type"));
        });
    }

    @Scenario("Successfully create a user chest")
    void testCreateUserChestSuccessfully(DataProvider dp, Reference<List<String>> messages, Reference<Player> player,
            Reference<CreateCommand> createCommand) {
        given("the user Peter has marked a chest at (world, 13, 14, 15)", () -> {
            PlayerEventListener listener = new PlayerEventListener(dp, null);
            listener.setMarkedLocation("Peter", new Location("world", 13, 14, 15));
            createCommand.set(new CreateCommand(null, dp, listener));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create user SAND", () -> {
            createCommand.get().execute(player.get(), "create", "user", "SAND");
        });
        then("the user is notified that the chest was created", () -> {
            assertThat(messages.get().get(0), containsString("Chest of type SAND was created successfully"));
            assertThat(messages.get(), hasSize(1));
        });
        and("a chest is registered for type STONE at (world, 13, 14, 15)", () -> {
            List<ChestDAO> chests = dp.findAllChestsAt(new Location("world", 13, 14, 15));
            assertThat(chests, hasSize(1));
            assertThat(chests.get(0).getMaterial(), is(equalTo("SAND")));
            assertThat(chests.get(0).isCentral(), is(equalTo(false)));
            assertThat(chests.get(0).getUsername(), is(equalTo("Peter")));
        });
    }

    @Scenario("Create user chest without permissions")
    void testCreateUserChestWithoutPermissions(DataProvider dp, Reference<List<String>> messages,
            Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the user Peter does not have permissions to create a user warehouse", () -> {
            createCommand.set(new CreateCommand(null, dp, null));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE, false) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create user SAND", () -> {
            createCommand.get().execute(player.get(), "create", "user", "SAND");
        });
        then("the user is notified that he does not have permissions", () -> {
            assertThat(messages.get().get(0), containsString("You dont have permissions to manage chests"));
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("Create central chest without central permissions, but with user permissions")
    void testCreateCentralChestWithoutPermissionsButWithUserPermissions(DataProvider dp,
            Reference<List<String>> messages, Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the user Peter does not have permissions to manage central chests, but permissions for user chests",
                () -> {
                    createCommand.set(new CreateCommand(null, dp, null));
                    messages.set(new LinkedList<>());
                    player.set(new PlayerMock() //
                            .withName("Peter") //
                            .withPermission(PERMISSION_MANAGE) //
                            .withPermission(PERMISSION_MANAGE_CENTRAL, false) //
                            .withItemInMainHand(MATERIAL_MARKER) //
                            .redirectMessages(messages.get()) //
                            .build());
                });
        when("the user executes the command /chestsort create central SAND", () -> {
            createCommand.get().execute(player.get(), "create", "central", "SAND");
        });
        then("the user is notified that he does not have permissions", () -> {
            assertThat(messages.get().get(0), containsString("You dont have permissions to manage the central chests"));
        });
        then("the user gets suggestion to manage user chests", () -> {
            assertThat(messages.get().get(1), containsString(
                    "If you want to create a chest for your personal warehouse please use the command /chestsort create user"));
            assertThat(messages.get(), hasSize(2));
        });
    }

    @Scenario("Create central chest without central permissionsand without user permissions")
    void testCreateCentralChestWithoutPermissionsAndWithoutUserPermissions(DataProvider dp,
            Reference<List<String>> messages, Reference<Player> player, Reference<CreateCommand> createCommand) {
        given("the user Peter does not have permissions to manage chests (central and user)", () -> {
            createCommand.set(new CreateCommand(null, dp, null));
            messages.set(new LinkedList<>());
            player.set(new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE, false) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL, false) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build());
        });
        when("the user executes the command /chestsort create central SAND", () -> {
            createCommand.get().execute(player.get(), "create", "central", "SAND");
        });
        then("the user is notified that he does not have permissions", () -> {
            assertThat(messages.get().get(0), containsString("You dont have permissions to manage the central chests"));
        });
        then("the user does not get suggestion to manage user chests", () -> {
            assertThat(messages.get(), hasSize(1));
        });
    }

    @Scenario("the command is only applicable to for text 'create' case insensitive")
    void testIsApplicable() {
        Player player = Mockito.mock(Player.class);
        assertThat(defaultCreateCommand.isApplicable(player, "item"), is(equalTo(false)));
        assertThat(defaultCreateCommand.isApplicable(player, "create"), is(equalTo(true)));
        assertThat(defaultCreateCommand.isApplicable(player, "CreAte"), is(equalTo(true)));
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
        return defaultCreateCommand.onTabComplete(null, "create", args);
    }
}
