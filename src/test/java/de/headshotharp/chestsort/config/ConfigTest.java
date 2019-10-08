package de.headshotharp.chestsort.config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.config.Config;

@Story("The config can be serialized to YAML and deserialized to Java object")
public class ConfigTest extends GherkinTest {
	@Scenario("A config is serialized and deserialized without any changes to the data")
	public void testConfigSerialization(Reference<Config> config, Reference<String> serializedConfig,
			Reference<Config> deserializedConfig) {
		final ObjectMapper mapper = new YAMLMapper();
		given("a config object exists with some data", () -> {
			config.set(new Config());
			config.get().getDatabase().setDriver("com.mysql.cj.jdbc.Driver");
			config.get().getDatabase().setDialect("org.hibernate.dialect.MySQL57Dialect");
			config.get().getDatabase().setUrl("jdbc:mysql://localhost:3306/dbname?useSSL=false");
			config.get().getDatabase().setUsername("peter");
			config.get().getDatabase().setPassword("geheim");
		});
		when("the config is serialized to YAML", () -> {
			serializedConfig.set(mapper.writeValueAsString(config.get()));
		});
		and("the YAML config is deserialized back to a Config object", () -> {
			deserializedConfig.set(mapper.readValue(serializedConfig.get(), Config.class));
		});
		then("the original config and the deserialized config are identical", () -> {
			assertThat(config.get(), is(equalTo(deserializedConfig.get())));
		});
	}
}
