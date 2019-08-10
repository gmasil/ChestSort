package de.headshotharp.chestsort2;

import java.util.List;

import org.hibernate.Session;

import de.headshotharp.chestsort2.dao.ChestDAO;
import de.headshotharp.chestsort2.dao.SignDAO;
import de.headshotharp.chestsort2.dao.generic.DAO;

public class DataProvider {
	private DataProvider() {
	}

	/* CHEST */

	public static List<ChestDAO> findAllChests() {
		return queryForList("SELECT c FROM " + ChestDAO.class.getName() + " c", ChestDAO.class);
	}

	public static void persistChest(ChestDAO chest) {
		persist(chest);
	}

	public static void deleteChest(ChestDAO chest) {
		delete(chest);
	}

	public static void clearAllChests() {
		update("DELETE FROM " + ChestDAO.class.getName());
	}

	/* SIGN */

	public static List<SignDAO> findAllSigns() {
		return queryForList("SELECT c FROM " + SignDAO.class.getName() + " c", SignDAO.class);
	}

	public static void persistSign(SignDAO sign) {
		persist(sign);
	}

	public static void deleteSign(SignDAO sign) {
		delete(sign);
	}

	public static void clearAllSigns() {
		update("DELETE FROM " + SignDAO.class.getName());
	}

	/* UTILS */

	private static <T> List<T> queryForList(String query, Class<T> clazz) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		List<T> list = session.createQuery(query, clazz).getResultList();
		session.getTransaction().commit();
		session.close();
		return list;
	}

	private static void update(String query) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.createQuery(query).executeUpdate();
		session.getTransaction().commit();
		session.close();
	}

	public static void persist(DAO o) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.persist(o);
		session.getTransaction().commit();
		session.close();
	}

	public static void delete(DAO o) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.delete(o);
		session.getTransaction().commit();
		session.close();
	}
}
