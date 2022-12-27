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
package de.headshotharp.chestsort;

import static de.headshotharp.chestsort.StaticConfig.MATERIAL_MARKER;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE;
import static de.headshotharp.chestsort.StaticConfig.PERMISSION_MANAGE_CENTRAL;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.LinkedList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.hibernate.DataProvider;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.testutils.ConfigureH2Hibernate;
import de.headshotharp.chestsort.utils.PlayerInteractEventMock;
import de.headshotharp.chestsort.utils.PlayerMock;
import de.headshotharp.chestsort.utils.PluginMock;

@ConfigureH2Hibernate
@Story("The PlayerEventListener implementation is tested")
class PlayerEventListenerTest extends GherkinTest {

    // mark chests

    @Scenario("A chest is successfully marked")
    void testMarkChestSuccessfully(Reference<PlayerEventListener> listener, Reference<PlayerInteractEvent> event,
            Reference<List<String>> messages) {
        given("the user has nothing marked yet", () -> {
            Location loc = new Location(null, 10, 10, 10);
            PluginMock pluginMock = new PluginMock() //
                    .withBlockAt(Material.CHEST, loc);
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build();
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withClickedBlock(pluginMock, loc) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .build());
            listener.set(new PlayerEventListener(null, pluginMock.getPlugin()));
        });
        when("the user right clicks a chest with a STICK in his hand", () -> {
            listener.get().onPlayerInteract(event.get());
        });
        then("the user is receives a message that he marked a chest", () -> {
            assertThat(messages.get().get(0), containsString("Marked chest at (world, 10, 10, 10)"));
            assertThat(messages.get(), hasSize(1));
        });
        and("the correct location is marked", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get().getMarkedLocation("Peter");
            assertMarkedLocation(marked, "world", 10, 10, 10);
        });
    }

    @Scenario("Two chests are successfully marked")
    void testMarkTwoChestsSuccessfully(Reference<PlayerEventListener> listener, Reference<PlayerInteractEvent> event,
            Reference<List<String>> messages) {
        given("the user has marked a chest at (world, 10, 10, 10)", () -> {
            PluginMock pluginMock = new PluginMock() //
                    .withChestAt(10, 10, 10) //
                    .withChestAt(20, 20, 20);
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build();
            // mark first location
            PlayerInteractEvent preparationEvent = new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withClickedBlock(pluginMock, 10, 10, 10) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .build();
            listener.set(new PlayerEventListener(null, pluginMock.getPlugin()));
            listener.get().onPlayerInteract(preparationEvent);
            messages.get().clear();
            assertMarkedLocation(listener.get().getMarkedLocation("Peter"), "world", 10, 10, 10);
            assertThat(listener.get().getPreviouslyMarkedLocation("Peter"), is(nullValue()));
            // prepare real event
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withClickedBlock(pluginMock, 20, 20, 20) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .build());
        });
        when("the user right clicks a chest with a STICK in his hand at (world, 20, 20, 20)", () -> {
            listener.get().onPlayerInteract(event.get());
        });
        then("the user is receives a message that he marked a chest", () -> {
            assertThat(messages.get().get(0), containsString("Marked chest at (world, 20, 20, 20)"));
            assertThat(messages.get(), hasSize(1));
        });
        and("the correct location is marked", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get().getMarkedLocation("Peter");
            assertMarkedLocation(marked, "world", 20, 20, 20);
        });
        and("the previously marked location is (world, 10, 10, 10)", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get()
                    .getPreviouslyMarkedLocation("Peter");
            assertMarkedLocation(marked, "world", 10, 10, 10);
        });
    }

    @Scenario("A sign is successfully marked")
    void testSignChestSuccessfully(Reference<PlayerEventListener> listener, Reference<PlayerInteractEvent> event,
            Reference<List<String>> messages) {
        given("the user has nothing marked yet", () -> {
            Location loc = new Location(null, 10, 10, 10);
            PluginMock pluginMock = new PluginMock() //
                    .withBlockAt(Material.OAK_SIGN, loc);
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build();
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withClickedBlock(pluginMock, loc) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .build());
            listener.set(new PlayerEventListener(null, pluginMock.getPlugin()));
        });
        when("the user right clicks a OAK_SIGN with a STICK in his hand", () -> {
            listener.get().onPlayerInteract(event.get());
        });
        then("the user is receives a message that he marked a sign", () -> {
            assertThat(messages.get().get(0), containsString("Marked sign at (world, 10, 10, 10)"));
            assertThat(messages.get(), hasSize(1));
        });
        and("the correct location is marked", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get().getMarkedLocation("Peter");
            assertThat(marked, is(not(nullValue())));
            assertThat(marked.getWorld(), is(equalTo("world")));
            assertThat(marked.getX(), is(equalTo(10)));
            assertThat(marked.getY(), is(equalTo(10)));
            assertThat(marked.getZ(), is(equalTo(10)));
        });
    }

    @Scenario("Nothing is marked when a normal block is clicked with STICK in hand")
    void testNothingMarkedWhenNormalBlockClicked(Reference<PlayerEventListener> listener,
            Reference<PlayerInteractEvent> event, Reference<List<String>> messages) {
        given("the user has nothing marked yet", () -> {
            Location loc = new Location(null, 10, 10, 10);
            PluginMock pluginMock = new PluginMock() //
                    .withBlockAt(Material.GRASS, loc);
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build();
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withClickedBlock(pluginMock, loc) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .build());
            listener.set(new PlayerEventListener(null, pluginMock.getPlugin()));
        });
        when("the user right clicks a GRASS block with a STICK in his hand", () -> {
            listener.get().onPlayerInteract(event.get());
        });
        then("the user does not receive a message", () -> {
            assertThat(messages.get(), hasSize(0));
        });
        and("he still has no marked location", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get().getMarkedLocation("Peter");
            assertThat(marked, is(nullValue()));
        });
    }

    @Scenario("A user marks a chest without mark permissions")
    void testMarkChestWithoutPermissions(Reference<PlayerEventListener> listener, Reference<PlayerInteractEvent> event,
            Reference<List<String>> messages) {
        given("the user has nothing marked yet", () -> {
            Location loc = new Location(null, 10, 10, 10);
            PluginMock pluginMock = new PluginMock() //
                    .withBlockAt(Material.CHEST, loc);
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withPermission(PERMISSION_MANAGE, false) //
                    .withPermission(PERMISSION_MANAGE_CENTRAL, false) //
                    .withItemInMainHand(MATERIAL_MARKER) //
                    .redirectMessages(messages.get()) //
                    .build();
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .withClickedBlock(pluginMock, loc) //
                    .build());
            listener.set(new PlayerEventListener(null, pluginMock.getPlugin()));
        });
        when("the user right clicks a chest with a STICK in his hand", () -> {
            listener.get().onPlayerInteract(event.get());
        });
        then("the user does not receive a message", () -> {
            assertThat(messages.get(), hasSize(0));
        });
        and("he still has no marked location", () -> {
            de.headshotharp.chestsort.hibernate.dao.generic.Location marked = listener.get().getMarkedLocation("Peter");
            assertThat(marked, is(nullValue()));
        });
    }

    // insert sign click

    @Scenario("A user inserts STONE by clicking a chestsort sign")
    void testInsertItemByClickingSign(DataProvider dp, Reference<PluginMock> pluginMock,
            Reference<PlayerEventListener> listener, Reference<PlayerInteractEvent> event,
            Reference<List<String>> messages) {
        given("a central chest exists at (world, 10, 10, 10)", () -> {
            pluginMock.set(new PluginMock() //
                    .withChestAt(10, 10, 10));
            dp.chests().persist(new ChestDAO("world", 10, 10, 10, Material.STONE.toString()));
        });
        and("a central sign exists at (world, 5, 5, 5)", () -> {
            pluginMock.get().withBlockAt(Material.OAK_SIGN, 5, 5, 5);
            dp.signs().persist(new SignDAO("world", 5, 5, 5));
        });
        when("the user right clicks the sign with a STONE in his hand", () -> {
            messages.set(new LinkedList<>());
            Player player = new PlayerMock() //
                    .withName("Peter") //
                    .withItemInMainHand(Material.STONE) //
                    .redirectMessages(messages.get()) //
                    .build();
            event.set(new PlayerInteractEventMock() //
                    .withPlayer(player) //
                    .withAction(Action.RIGHT_CLICK_BLOCK) //
                    .withClickedBlock(pluginMock.get(), 5, 5, 5) //
                    .build());
            listener.set(new PlayerEventListener(dp, pluginMock.get().getPlugin()));
            listener.get().onPlayerInteract(event.get());
        });
        then("the user does not receive a message", () -> {
            assertThat(messages.get(), hasSize(0));
        });
        and("an item was added to the chest", () -> {
            verify(pluginMock.get().getChestAt(10, 10, 10).getInventory(), times(1)).addItem(any());
        });
        and("the user has an empty hand", () -> {
            ItemStack itemInMainHand = event.get().getPlayer().getInventory().getItemInMainHand();
            assertThat(itemInMainHand.getType(), is(equalTo(Material.AIR)));
        });
    }

    // Utils

    private void assertMarkedLocation(de.headshotharp.chestsort.hibernate.dao.generic.Location marked, String world,
            int x, int y, int z) {
        assertThat(marked, is(not(nullValue())));
        assertThat(marked.getWorld(), is(equalTo("world")));
        assertThat(marked.getX(), is(equalTo(x)));
        assertThat(marked.getY(), is(equalTo(y)));
        assertThat(marked.getZ(), is(equalTo(z)));
    }
}
