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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;

import java.util.List;

import org.bukkit.Material;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import de.headshotharp.chestsort.hibernate.testutils.ConfigureH2Hibernate;

@ConfigureH2Hibernate
@Story("The DataProvider can perform all necessary operations on the database")
class DataProviderTest extends GherkinTest {

    /* CHESTS */

    @Scenario("A central chest can be found by all attributes")
    void testFindCentralChestByAllAttributes(Reference<List<ChestDAO>> chests, DataProvider dp) {
        given("a central chest exist at (world, 6, 9, 4) with material cobblestone", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString()));
            assertThat(dp.findAllChests(), hasSize(1));
        });
        and("a central chest exist at (world, 6, 9, 4) with material stone", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.STONE.toString()));
            assertThat(dp.findAllChests(), hasSize(2));
        });
        and("a central chest exist at (world, 6, 9, 4) with material sand", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.SAND.toString()));
            assertThat(dp.findAllChests(), hasSize(3));
        });
        and("a user chest for user 'Peter' exist at (world, 6, 9, 4) with material cobblestone", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter"));
            assertThat(dp.findAllChests(), hasSize(4));
        });
        when("central cobblestone chests are searched at (world, 6, 9, 4)", () -> {
            chests.set(dp.findChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString())));
        });
        then("a single chest is found", () -> {
            assertThat(chests.get(), hasSize(1));
            assertThat(chests.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
            assertThat(chests.get().get(0).getMaterial(), is(equalTo(Material.COBBLESTONE.toString())));
            assertThat(chests.get().get(0).getUsername(), is(nullValue()));
            assertThat(chests.get().get(0).isCentral(), is(true));
        });
    }

    @Scenario("A user chest can be found by all attributes")
    void testFinduserChestByAllAttributes(Reference<List<ChestDAO>> chests, DataProvider dp) {
        given("a central chest exist at (world, 6, 9, 4) with material cobblestone", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString()));
            assertThat(dp.findAllChests(), hasSize(1));
        });
        and("a central chest exist at (world, 6, 9, 4) with material stone", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.STONE.toString()));
            assertThat(dp.findAllChests(), hasSize(2));
        });
        and("a central chest exist at (world, 6, 9, 4) with material sand", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.SAND.toString()));
            assertThat(dp.findAllChests(), hasSize(3));
        });
        and("a user chest for user 'Peter' exist at (world, 6, 9, 4) with material cobblestone", () -> {
            dp.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter"));
            assertThat(dp.findAllChests(), hasSize(4));
        });
        when("user cobblestone chests are searched at (world, 6, 9, 4) for user 'Peter'", () -> {
            chests.set(dp.findChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter")));
        });
        then("a single chest is found", () -> {
            assertThat(chests.get(), hasSize(1));
            assertThat(chests.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
            assertThat(chests.get().get(0).getMaterial(), is(equalTo(Material.COBBLESTONE.toString())));
            assertThat(chests.get().get(0).getUsername(), is(equalTo("Peter")));
            assertThat(chests.get().get(0).isCentral(), is(false));
        });
    }

    @Scenario("A chest can be found by its location")
    void testFindChestByLocation(Reference<List<ChestDAO>> chests, DataProvider dp) {
        given("a single chest exists at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
            dp.persistChest(chest);
            assertThat(dp.findAllChests(), hasSize(1));
        });
        when("chests are searched at (world, 6, 9, 4)", () -> {
            chests.set(dp.findAllChestsAt(new Location("world", 6, 9, 4)));
        });
        then("a single chest is found", () -> {
            assertThat(chests.get(), hasSize(1));
            assertThat(chests.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
        });
    }

    @Scenario("A central chest can be found by its material")
    void testFindAllCentralChestsByMaterial(Reference<List<ChestDAO>> chests, DataProvider dp) {
        given("a central chest exists with material STONE", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            ChestDAO chest = new ChestDAO("world", 0, 0, 0, Material.STONE.toString());
            dp.persistChest(chest);
            assertThat(dp.findAllChests(), hasSize(1));
        });
        and("another central chest exists with material DIRT", () -> {
            ChestDAO chest = new ChestDAO("world", 1, 2, 3, Material.DIRT.toString());
            dp.persistChest(chest);
            assertThat(dp.findAllChests(), hasSize(2));
        });
        when("central chests with material STONE are searched", () -> {
            chests.set(dp.findAllCentralChestsByMaterial("STONE"));
        });
        then("a single chest is found", () -> {
            assertThat(chests.get(), hasSize(1));
            assertThat(chests.get().get(0).getLocation(), is(equalTo(new Location("world", 0, 0, 0))));
            assertThat(chests.get().get(0).getMaterial(), is(equalTo("STONE")));
        });
    }

    @Scenario("A chest is not found in the wrong location")
    void testDontFindChestAtWrongLocation(Reference<List<ChestDAO>> chests, DataProvider dp) {
        given("a single chest exists at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
            dp.persistChest(chest);
            assertThat(dp.findAllChests(), hasSize(1));
        });
        when("chests are searched at (world, 6, 9, 400)", () -> {
            chests.set(dp.findAllChestsAt(new Location("world", 6, 9, 400)));
        });
        then("no chest is found", () -> {
            assertThat(chests.get(), hasSize(0));
        });
    }

    @Scenario("A chest can be persisted")
    void testPersistChests(DataProvider dp) {
        given("no chest exists in the database", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
        });
        when("a chest is persisted", () -> {
            ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
            dp.persistChest(chest);
        });
        then("a chest exists in database", () -> {
            assertThat(dp.findAllChests(), hasSize(1));
        });
    }

    @Scenario("A chest can be deleted")
    void testDeleteChest(DataProvider dp) {
        given("a single chest exists in the database", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
            ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
            dp.persistChest(chest);
            assertThat(dp.findAllChests(), hasSize(1));
        });
        when("the chest is deleted", () -> {
            ChestDAO chest = dp.findAllChests().get(0);
            dp.deleteChest(chest);
        });
        then("no chest exists in database", () -> {
            assertThat(dp.findAllChests(), hasSize(0));
        });
    }

    /* SIGNS */

    @Scenario("A central sign can be found by all attributes")
    void testFindCentralSignByAllAttributes(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a central sign exist at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            dp.persistSign(new SignDAO("world", 6, 9, 4));
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        and("a user sign for user 'Panda' exist at (world, 6, 9, 4)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 4, "Panda"));
            assertThat(dp.findAllSigns(), hasSize(2));
        });
        and("a user sign for user 'Peter' exist at (world, 6, 9, 400)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 400, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(3));
        });
        and("a user sign for user 'Peter' exist at (world, 6, 9, 4)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 4, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(4));
        });
        when("central signs are searched at (world, 6, 9, 4)", () -> {
            signs.set(dp.findSign(new SignDAO("world", 6, 9, 4)));
        });
        then("a single sign is found", () -> {
            assertThat(signs.get(), hasSize(1));
            assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
            assertThat(signs.get().get(0).getUsername(), is(nullValue()));
            assertThat(signs.get().get(0).isCentral(), is(true));
        });
    }

    @Scenario("A user sign can be found by all attributes")
    void testFindUserSignByAllAttributes(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a central sign exist at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            dp.persistSign(new SignDAO("world", 6, 9, 4));
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        and("a user sign for user 'Panda' exist at (world, 6, 9, 4)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 4, "Panda"));
            assertThat(dp.findAllSigns(), hasSize(2));
        });
        and("a user sign for user 'Peter' exist at (world, 6, 9, 400)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 400, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(3));
        });
        and("a user sign for user 'Peter' exist at (world, 6, 9, 4)", () -> {
            dp.persistSign(new SignDAO("world", 6, 9, 4, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(4));
        });
        when("user signs for user 'Peter' are searched at (world, 6, 9, 4)", () -> {
            signs.set(dp.findSign(new SignDAO("world", 6, 9, 4, "Peter")));
        });
        then("a single sign is found", () -> {
            assertThat(signs.get(), hasSize(1));
            assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
            assertThat(signs.get().get(0).getUsername(), is("Peter"));
            assertThat(signs.get().get(0).isCentral(), is(false));
        });
    }

    @Scenario("A sign can be found by its location")
    void testFindSignByLocation(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a single sign exists at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            SignDAO sign = new SignDAO("world", 6, 9, 4);
            dp.persistSign(sign);
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        when("signs are searched at (world, 6, 9, 4)", () -> {
            signs.set(dp.findAllSignsAt(new Location("world", 6, 9, 4)));
        });
        then("a single sign is found", () -> {
            assertThat(signs.get(), hasSize(1));
            assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
        });
    }

    @Scenario("A sign is not found in the wrong location")
    void testDontFindSignAtWrongLocation(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a single sign exists at (world, 6, 9, 4)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            SignDAO sign = new SignDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
            dp.persistSign(sign);
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        when("signs are searched at (world, 6, 9, 400)", () -> {
            signs.set(dp.findAllSignsAt(new Location("world", 6, 9, 400)));
        });
        then("no sign is found", () -> {
            assertThat(signs.get(), hasSize(0));
        });
    }

    @Scenario("Only signs for the correct user are found")
    void testFindAllSignsByUser(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a central sign exist at (world, 1, 2, 3)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            dp.persistSign(new SignDAO("world", 1, 2, 3));
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        and("a user sign for user 'Panda' exist at (world, 10,20,30)", () -> {
            dp.persistSign(new SignDAO("world", 10, 20, 30, "Panda"));
            assertThat(dp.findAllSigns(), hasSize(2));
        });
        and("a user sign for user 'Peter' exist at (world, 100,200,300)", () -> {
            dp.persistSign(new SignDAO("world", 100, 200, 300, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(3));
        });
        and("a user sign for user 'Peter' exist at (world, 110, 220, 330)", () -> {
            dp.persistSign(new SignDAO("world", 110, 220, 330, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(4));
        });
        when("user signs for user 'Peter' are searched ", () -> {
            signs.set(dp.findAllSignsByUser("Peter"));
        });
        then("two signs are found", () -> {
            assertThat(signs.get(), hasSize(2));
            assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 100, 200, 300))));
            assertThat(signs.get().get(0).getUsername(), is("Peter"));
            assertThat(signs.get().get(0).isCentral(), is(false));
            assertThat(signs.get().get(1).getLocation(), is(equalTo(new Location("world", 110, 220, 330))));
            assertThat(signs.get().get(1).getUsername(), is("Peter"));
            assertThat(signs.get().get(1).isCentral(), is(false));
        });
    }

    @Scenario("Only central signs are found if requested")
    void testFindAllCentralSigns(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("a central sign exist at (world, 1, 2, 3)", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            dp.persistSign(new SignDAO("world", 1, 2, 3));
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        and("a user sign for user 'Panda' exist at (world, 10,20,30)", () -> {
            dp.persistSign(new SignDAO("world", 10, 20, 30, "Panda"));
            assertThat(dp.findAllSigns(), hasSize(2));
        });
        and("a user sign for user 'Peter' exist at (world, 100,200,300)", () -> {
            dp.persistSign(new SignDAO("world", 100, 200, 300, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(3));
        });
        and("a user sign for user 'Peter' exist at (world, 110, 220, 330)", () -> {
            dp.persistSign(new SignDAO("world", 110, 220, 330, "Peter"));
            assertThat(dp.findAllSigns(), hasSize(4));
        });
        when("central signs are searched ", () -> {
            signs.set(dp.findAllCentralSigns());
        });
        then("a single signs are found", () -> {
            assertThat(signs.get(), hasSize(1));
            assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 1, 2, 3))));
            assertThat(signs.get().get(0).isCentral(), is(true));
        });
    }

    @Scenario("Only signs in a given area are found")
    void testFindAllSignsAround(Reference<List<SignDAO>> signs, DataProvider dp) {
        given("there is a sign at (world, 0, 0, 0)", () -> {
            dp.persistSign(new SignDAO("world", 0, 0, 0));
        });
        and("a sign at (world, 0, 2, 4)", () -> {
            dp.persistSign(new SignDAO("world", 0, 2, 4));
        });
        and("a sign at (world, 0, 7, 14)", () -> {
            dp.persistSign(new SignDAO("world", 0, 7, 14));
        });
        when("all signs in a radius of 5 around (world, 0, 0, 0) are searched", () -> {
            signs.set(dp.findAllSignsAround(new Location("world", 0, 0, 0), 5));
        });
        then("two signs are found", () -> {
            assertThat(signs.get(), hasSize(2));
        });
    }

    @Scenario("A sign can be persisted")
    void testPersistSign(DataProvider dp) {
        given("no sign exists in the database", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
        });
        when("a sign is persisted", () -> {
            SignDAO sign = new SignDAO("world", 6, 4, 9);
            dp.persistSign(sign);
        });
        then("a sign exists in database", () -> {
            assertThat(dp.findAllSigns(), hasSize(1));
        });
    }

    @Scenario("A sign can be deleted")
    void testDeleteSign(DataProvider dp) {
        given("a single sign exists in the database", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
            SignDAO sign = new SignDAO("world", 6, 4, 9);
            dp.persistSign(sign);
            assertThat(dp.findAllSigns(), hasSize(1));
        });
        when("the sign is deleted", () -> {
            SignDAO sign = dp.findAllSigns().get(0);
            dp.deleteSign(sign);
        });
        then("no sign exists in database", () -> {
            assertThat(dp.findAllSigns(), hasSize(0));
        });
    }
}
