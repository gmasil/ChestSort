package de.headshotharp.chestsort.hibernate.testutils;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import de.headshotharp.chestsort.Registry;

public class H2HibernateConfigurationExtension implements BeforeEachCallback {
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        Registry.getHibernateUtils().setDatabaseConfig(Registry.getConfigService().getH2Config().getDatabase());
        Registry.getDataProvider().clearAllChests();
        Registry.getDataProvider().clearAllSigns();
    }
}
