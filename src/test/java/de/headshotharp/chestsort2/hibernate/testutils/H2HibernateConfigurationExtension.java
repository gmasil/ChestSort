package de.headshotharp.chestsort2.hibernate.testutils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.headshotharp.chestsort2.Registry;
import de.headshotharp.chestsort2.hibernate.DataProvider;

public class H2HibernateConfigurationExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		Registry.getHibernateUtils().setDatabaseConfig(Registry.getConfigService().getH2Config().getDatabase());
		DataProvider.clearAllChests();
		DataProvider.clearAllSigns();
	}
}
