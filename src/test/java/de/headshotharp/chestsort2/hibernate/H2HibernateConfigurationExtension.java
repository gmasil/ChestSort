package de.headshotharp.chestsort2.hibernate;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.headshotharp.chestsort2.ConfigService;
import de.headshotharp.chestsort2.DataProvider;
import de.headshotharp.chestsort2.HibernateUtils;

public class H2HibernateConfigurationExtension implements BeforeEachCallback {
	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		HibernateUtils.setDatabaseConfig(ConfigService.getH2Config());
		DataProvider.clearAllChests();
		DataProvider.clearAllSigns();
	}
}
