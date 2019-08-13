package de.headshotharp.chestsort2.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;

@Story("The ConfigService is able to save and load config files correctly")
public class ConfigServiceTest extends GherkinTest {
	@Scenario("The default configuration can be saved and successfully reloaded")
	public void testDefaultConfigCreation(Reference<Config> config) {
		given("no config file exists", () -> {
			ConfigService.setConfigFile(new File("target/plugins/ChestSort", "config.yaml"));
			ConfigService.getConfigFile().delete();
		});
		when("the default config is saved", () -> {
			ConfigService.saveDefaultConfig();
		});
		and("the saved config is loaded again", () -> {
			config.set(ConfigService.readConfig());
		});
		then("the loaded config is identical to the default config", () -> {
			assertThat(config.get(), is(equalTo(ConfigService.getDefaultConfig())));
		});
	}
}
