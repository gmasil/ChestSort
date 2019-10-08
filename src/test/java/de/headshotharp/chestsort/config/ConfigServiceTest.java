package de.headshotharp.chestsort.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.Registry;
import de.headshotharp.chestsort.config.Config;

@Story("The ConfigService is able to save and load config files correctly")
public class ConfigServiceTest extends GherkinTest {
	@Scenario("The default configuration can be saved and successfully reloaded")
	public void testDefaultConfigCreation(Reference<Config> config) {
		given("no config file exists", () -> {
			Registry.getConfigService().setConfigFile(new File("target/plugins/ChestSort", "config.yaml"));
			Registry.getConfigService().getConfigFile().delete();
		});
		when("the default config is saved", () -> {
			Registry.getConfigService().saveDefaultConfig();
		});
		and("the saved config is loaded again", () -> {
			config.set(Registry.getConfigService().readConfig());
		});
		then("the loaded config is identical to the default config", () -> {
			assertThat(config.get(), is(equalTo(Registry.getConfigService().getDefaultConfig())));
		});
	}
}
