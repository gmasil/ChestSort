package de.headshotharp.chestsort2.hibernate;

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
import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.SignDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;
import de.headshotharp.chestsort2.hibernate.testutils.ConfigureH2Hibernate;

@ConfigureH2Hibernate
@Story("The DataProvider can perform all necessary operations on the database")
public class DataProviderTest extends GherkinTest {

	/* CHESTS */

	@Scenario("A central chest can be found by all attributes")
	public void testFindCentralChestByAllAttributes(Reference<List<ChestDAO>> chests) {
		given("a central chest exist at (world, 6, 9, 4) with material cobblestone", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
		and("a central chest exist at (world, 6, 9, 4) with material stone", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.STONE.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(2));
		});
		and("a central chest exist at (world, 6, 9, 4) with material sand", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.SAND.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(3));
		});
		and("a user chest for user 'Peter' exist at (world, 6, 9, 4) with material cobblestone", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter"));
			assertThat(DataProvider.findAllChests(), hasSize(4));
		});
		when("central cobblestone chests are searched at (world, 6, 9, 4)", () -> {
			chests.set(DataProvider.findChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString())));
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
	public void testFinduserChestByAllAttributes(Reference<List<ChestDAO>> chests) {
		given("a central chest exist at (world, 6, 9, 4) with material cobblestone", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
		and("a central chest exist at (world, 6, 9, 4) with material stone", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.STONE.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(2));
		});
		and("a central chest exist at (world, 6, 9, 4) with material sand", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.SAND.toString()));
			assertThat(DataProvider.findAllChests(), hasSize(3));
		});
		and("a user chest for user 'Peter' exist at (world, 6, 9, 4) with material cobblestone", () -> {
			DataProvider.persistChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter"));
			assertThat(DataProvider.findAllChests(), hasSize(4));
		});
		when("user cobblestone chests are searched at (world, 6, 9, 4) for user 'Peter'", () -> {
			chests.set(
					DataProvider.findChest(new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString(), "Peter")));
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
	public void testFindChestByLocation(Reference<List<ChestDAO>> chests) {
		given("a single chest exists at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
			ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
			DataProvider.persistChest(chest);
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
		when("chests are searched at (world, 6, 9, 4)", () -> {
			chests.set(DataProvider.findAllChestsAt(new Location("world", 6, 9, 4)));
		});
		then("a single chest is found", () -> {
			assertThat(chests.get(), hasSize(1));
			assertThat(chests.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
		});
	}

	@Scenario("A chest is not found in the wrong location")
	public void testDontFindChestAtWrongLocation(Reference<List<ChestDAO>> chests) {
		given("a single chest exists at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
			ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
			DataProvider.persistChest(chest);
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
		when("chests are searched at (world, 6, 9, 400)", () -> {
			chests.set(DataProvider.findAllChestsAt(new Location("world", 6, 9, 400)));
		});
		then("no chest is found", () -> {
			assertThat(chests.get(), hasSize(0));
		});
	}

	@Scenario("A chest can be persisted")
	public void testPersistChests() {
		given("no chest exists in the database", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
		});
		when("a chest is persisted", () -> {
			ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
			DataProvider.persistChest(chest);
		});
		then("a chest exists in database", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
	}

	@Scenario("A chest can be deleted")
	public void testDeleteChest() {
		given("a single chest exists in the database", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
			ChestDAO chest = new ChestDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
			DataProvider.persistChest(chest);
			assertThat(DataProvider.findAllChests(), hasSize(1));
		});
		when("the chest is deleted", () -> {
			ChestDAO chest = DataProvider.findAllChests().get(0);
			DataProvider.deleteChest(chest);
		});
		then("no chest exists in database", () -> {
			assertThat(DataProvider.findAllChests(), hasSize(0));
		});
	}

	/* SIGNS */

	@Scenario("A central sign can be found by all attributes")
	public void testFindCentralSignByAllAttributes(Reference<List<SignDAO>> signs) {
		given("a central sign exist at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4));
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
		and("a user sign for user 'Panda' exist at (world, 6, 9, 4)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4, "Panda"));
			assertThat(DataProvider.findAllSigns(), hasSize(2));
		});
		and("a user sign for user 'Peter' exist at (world, 6, 9, 400)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 400, "Peter"));
			assertThat(DataProvider.findAllSigns(), hasSize(3));
		});
		and("a user sign for user 'Peter' exist at (world, 6, 9, 4)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4, "Peter"));
			assertThat(DataProvider.findAllSigns(), hasSize(4));
		});
		when("central signs are searched at (world, 6, 9, 4)", () -> {
			signs.set(DataProvider.findSign(new SignDAO("world", 6, 9, 4)));
		});
		then("a single sign is found", () -> {
			assertThat(signs.get(), hasSize(1));
			assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
			assertThat(signs.get().get(0).getUsername(), is(nullValue()));
			assertThat(signs.get().get(0).isCentral(), is(true));
		});
	}

	@Scenario("A user sign can be found by all attributes")
	public void testFindUserSignByAllAttributes(Reference<List<SignDAO>> signs) {
		given("a central sign exist at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4));
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
		and("a user sign for user 'Panda' exist at (world, 6, 9, 4)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4, "Panda"));
			assertThat(DataProvider.findAllSigns(), hasSize(2));
		});
		and("a user sign for user 'Peter' exist at (world, 6, 9, 400)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 400, "Peter"));
			assertThat(DataProvider.findAllSigns(), hasSize(3));
		});
		and("a user sign for user 'Peter' exist at (world, 6, 9, 4)", () -> {
			DataProvider.persistSign(new SignDAO("world", 6, 9, 4, "Peter"));
			assertThat(DataProvider.findAllSigns(), hasSize(4));
		});
		when("user signs for user 'Peter' are searched at (world, 6, 9, 4)", () -> {
			signs.set(DataProvider.findSign(new SignDAO("world", 6, 9, 4, "Peter")));
		});
		then("a single sign is found", () -> {
			assertThat(signs.get(), hasSize(1));
			assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
			assertThat(signs.get().get(0).getUsername(), is("Peter"));
			assertThat(signs.get().get(0).isCentral(), is(false));
		});
	}

	@Scenario("A sign can be found by its location")
	public void testFindSignByLocation(Reference<List<SignDAO>> signs) {
		given("a single sign exists at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
			SignDAO sign = new SignDAO("world", 6, 9, 4);
			DataProvider.persistSign(sign);
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
		when("signs are searched at (world, 6, 9, 4)", () -> {
			signs.set(DataProvider.findAllSignsAt(new Location("world", 6, 9, 4)));
		});
		then("a single sign is found", () -> {
			assertThat(signs.get(), hasSize(1));
			assertThat(signs.get().get(0).getLocation(), is(equalTo(new Location("world", 6, 9, 4))));
		});
	}

	@Scenario("A sign is not found in the wrong location")
	public void testDontFindSignAtWrongLocation(Reference<List<SignDAO>> signs) {
		given("a single sign exists at (world, 6, 9, 4)", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
			SignDAO sign = new SignDAO("world", 6, 9, 4, Material.COBBLESTONE.toString());
			DataProvider.persistSign(sign);
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
		when("signs are searched at (world, 6, 9, 400)", () -> {
			signs.set(DataProvider.findAllSignsAt(new Location("world", 6, 9, 400)));
		});
		then("no sign is found", () -> {
			assertThat(signs.get(), hasSize(0));
		});
	}

	@Scenario("Only signs in a given area are found")
	public void testFindAllSignsAround(Reference<List<SignDAO>> signs) {
		given("there is a cobblestone sign at (world, 0, 0, 0)", () -> {
			DataProvider.persistSign(new SignDAO("world", 0, 0, 0));
		});
		and("a cobblestone sign at (world, 0, 2, 4)", () -> {
			DataProvider.persistSign(new SignDAO("world", 0, 2, 4));
		});
		and("a sandstone sign at (world, 0, 7, 14)", () -> {
			DataProvider.persistSign(new SignDAO("world", 0, 7, 14));
		});
		when("all signs in a radius of 5 around (world, 0, 0, 0) are searched", () -> {
			signs.set(DataProvider.findAllSignsAround(new Location("world", 0, 0, 0), 5));
		});
		then("two signs are found", () -> {
			assertThat(signs.get(), hasSize(2));
		});
	}

	@Scenario("A sign can be persisted")
	public void testPersistSign() {
		given("no sign exists in the database", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
		});
		when("a sign is persisted", () -> {
			SignDAO sign = new SignDAO("world", 6, 4, 9);
			DataProvider.persistSign(sign);
		});
		then("a sign exists in database", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
	}

	@Scenario("A sign can be deleted")
	public void testDeleteSign() {
		given("a single sign exists in the database", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
			SignDAO sign = new SignDAO("world", 6, 4, 9);
			DataProvider.persistSign(sign);
			assertThat(DataProvider.findAllSigns(), hasSize(1));
		});
		when("the sign is deleted", () -> {
			SignDAO sign = DataProvider.findAllSigns().get(0);
			DataProvider.deleteSign(sign);
		});
		then("no sign exists in database", () -> {
			assertThat(DataProvider.findAllSigns(), hasSize(0));
		});
	}
}
