package de.headshotharp.chestsort2.hibernate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

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
}
