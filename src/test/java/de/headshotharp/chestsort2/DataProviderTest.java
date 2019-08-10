package de.headshotharp.chestsort2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.bukkit.Material;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort2.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.ConfigureH2Hibernate;

@ConfigureH2Hibernate
@Story("The DataProvider can perform all necessary operations on the database")
public class DataProviderTest extends GherkinTest {
	@Scenario("A chest can be persisted in the database")
	public void testFindAllChests() {
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
}
