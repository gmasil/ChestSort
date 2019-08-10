package de.headshotharp.chestsort2;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;

import de.headshotharp.chestsort2.Config.Database;
import de.headshotharp.chestsort2.dao.ChestDAO;
import de.headshotharp.chestsort2.dao.SignDAO;

public class HibernateUtils {
	private static SessionFactory sessionFactory;
	private static Database databaseConfig;

	private HibernateUtils() {
	}

	public static void setDatabaseConfig(Config config) {
		databaseConfig = config.getDatabase();
	}

	public static SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			if (databaseConfig == null) {
				throw new IllegalStateException("HibernateUtils has no database config");
			}
			// boilerplate
			Configuration configuration = new Configuration();
			Properties properties = new Properties();
			// config mapper
			properties.put(Environment.DRIVER, databaseConfig.getDriver());
			properties.put(Environment.URL, databaseConfig.getUrl());
			properties.put(Environment.USER, databaseConfig.getUsername());
			properties.put(Environment.PASS, databaseConfig.getPassword());
			properties.put(Environment.DIALECT, databaseConfig.getDialect());
			properties.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
			properties.put(Environment.HBM2DDL_AUTO, "update");
			// boilerplate
			configuration.setProperties(properties);
			// configure entity classes
			configuration.addAnnotatedClass(ChestDAO.class);
			configuration.addAnnotatedClass(SignDAO.class);
			// setup session factory
			ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
					.applySettings(configuration.getProperties()).build();
			sessionFactory = configuration.buildSessionFactory(serviceRegistry);
		}
		return sessionFactory;
	}
}
