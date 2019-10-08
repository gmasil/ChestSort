package de.headshotharp.chestsort.hibernate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;

import de.gmasil.gherkin.extension.GherkinTest;
import de.gmasil.gherkin.extension.Reference;
import de.gmasil.gherkin.extension.Scenario;
import de.gmasil.gherkin.extension.Story;
import de.headshotharp.chestsort.Registry;

@Story("Session factory is tested for intended behaviour")
public class HibernateUtilsTest extends GherkinTest {
	@Scenario("A SessionFactory is created without database config")
	public void testSessionFactoryCreationWithoutDatabaseConfig(Reference<IllegalStateException> thrownException) {
		given("no database config exists", () -> {
			Registry.getHibernateUtils().setDatabaseConfig(null);
		});
		when("the session factory is created", () -> {
			try {
				Registry.getHibernateUtils().createSessionFactory();
			} catch (IllegalStateException e) {
				thrownException.set(e);
			}
		});
		then("an IllegalStateException with message 'HibernateUtils has no database config' is thrown", () -> {
			assertThat(thrownException.get(), is(not(nullValue())));
			assertThat(thrownException.get().getMessage(), is(equalTo("HibernateUtils has no database config")));
		});
	}
}
