package de.headshotharp.chestsort2;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import java.sql.SQLException;

import org.bukkit.Material;
import org.hibernate.Session;
import org.junit.jupiter.api.Test;

import de.headshotharp.chestsort2.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.ConfigureH2Hibernate;

@ConfigureH2Hibernate
public class HibernateUtilsTest {
	@Test
	public void testSessionFactory() throws SQLException {
		// open session and transaction
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		// in transaction
		assertThat(
				session.createQuery("SELECT c FROM " + ChestDAO.class.getName() + " c", ChestDAO.class).getResultList(),
				hasSize(0));
		ChestDAO chest = new ChestDAO("world", 3, 5, 7, Material.COBBLESTONE.toString());
		session.persist(chest);
		// commit and close transaction
		assertThat(
				session.createQuery("SELECT c FROM " + ChestDAO.class.getName() + " c", ChestDAO.class).getResultList(),
				hasSize(1));
		session.getTransaction().commit();
		session.close();
		// start new session and transaction
		session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		// in transaction verify that the persisted data is still there
		assertThat(
				session.createQuery("SELECT c FROM " + ChestDAO.class.getName() + " c", ChestDAO.class).getResultList(),
				hasSize(1));
		session.getTransaction().commit();
		session.close();
	}
}
