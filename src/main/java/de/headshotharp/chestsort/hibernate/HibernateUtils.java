package de.headshotharp.chestsort.hibernate;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import de.headshotharp.chestsort.config.Config.DatabaseConfig;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;

public class HibernateUtils {
	private SessionFactory sessionFactory;
	private DatabaseConfig databaseConfig;

	public void setDatabaseConfig(DatabaseConfig config) {
		databaseConfig = config;
	}

	public SessionFactory getSessionFactory() {
		if (sessionFactory == null) {
			createSessionFactory();
		}
		return sessionFactory;
	}

	protected void createSessionFactory() {
		if (databaseConfig == null) {
			throw new IllegalStateException("HibernateUtils has no database config");
		}
		// boilerplate
		Configuration configuration = new Configuration();
		Properties properties = new Properties();
		// config mapper
		properties.put(AvailableSettings.DRIVER, databaseConfig.getDriver());
		properties.put(AvailableSettings.URL, databaseConfig.getUrl());
		properties.put(AvailableSettings.USER, databaseConfig.getUsername());
		properties.put(AvailableSettings.PASS, databaseConfig.getPassword());
		properties.put(AvailableSettings.DIALECT, databaseConfig.getDialect());
		properties.put(AvailableSettings.CURRENT_SESSION_CONTEXT_CLASS, "thread");
		properties.put(AvailableSettings.HBM2DDL_AUTO, "update");
		properties.put(AvailableSettings.CONNECTION_PROVIDER, "org.hibernate.connection.C3P0ConnectionProvider");
		properties.put(AvailableSettings.C3P0_MIN_SIZE, "5");
		properties.put(AvailableSettings.C3P0_MAX_SIZE, "20");
		properties.put(AvailableSettings.C3P0_ACQUIRE_INCREMENT, "5");
		properties.put(AvailableSettings.C3P0_TIMEOUT, "600");
		// boilerplate
		configuration.setProperties(properties);
		// configure entity classes
		configuration.addAnnotatedClass(ChestDAO.class);
		configuration.addAnnotatedClass(SignDAO.class);
		// setup session factory
		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
		sessionFactory = configuration.buildSessionFactory(serviceRegistry);
	}
}
