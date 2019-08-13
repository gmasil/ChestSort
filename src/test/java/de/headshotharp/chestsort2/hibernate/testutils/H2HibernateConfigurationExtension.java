package de.headshotharp.chestsort2.hibernate.testutils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.headshotharp.chestsort2.config.ConfigService;
import de.headshotharp.chestsort2.hibernate.DataProvider;
import de.headshotharp.chestsort2.hibernate.HibernateUtils;

public class H2HibernateConfigurationExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		HibernateUtils.setDatabaseConfig(ConfigService.getH2Config().getDatabase());
		DataProvider.clearAllChests();
		DataProvider.clearAllSigns();
	}
}
