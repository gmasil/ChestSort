package de.headshotharp.chestsort2.hibernate;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.headshotharp.chestsort2.Registry;
import de.headshotharp.chestsort2.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort2.hibernate.dao.SignDAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

public class DataProvider {
	// delete
	private static final String DELETE_FROM = "DELETE FROM %s ";
	private static final String DELETE_FROM_CHESTS = String.format(DELETE_FROM, ChestDAO.class.getName());
	private static final String DELETE_FROM_SIGNS = String.format(DELETE_FROM, SignDAO.class.getName());
	// select
	private static final String SELECT_FROM = "FROM %s t ";
	private static final String SELECT_FROM_CHESTS = String.format(SELECT_FROM, ChestDAO.class.getName());
	private static final String SELECT_FROM_SIGNS = String.format(SELECT_FROM, SignDAO.class.getName());

	private DataProvider() {
	}

	/* CHEST */

	public static List<ChestDAO> findAllChests() {
		return queryForList(SELECT_FROM_CHESTS, ChestDAO.class);
	}

	public static void persistChest(ChestDAO chest) {
		persist(chest);
	}

	public static void deleteChest(ChestDAO chest) {
		delete(chest);
	}

	public static void clearAllChests() {
		update(DELETE_FROM_CHESTS);
	}

	/* SIGN */

	public static List<SignDAO> findAllSigns() {
		return queryForList(SELECT_FROM_SIGNS, SignDAO.class);
	}

	public static List<SignDAO> findAllSignsAround(Location location, int radius) {
		return inTransaction(session -> {
			String query = SELECT_FROM_SIGNS
					+ "WHERE t.location.world = :world AND t.location.x >= :lowerx AND t.location.x <= :higherx AND t.location.y >= :lowery AND t.location.y <= :highery AND t.location.z >= :lowerz AND t.location.z <= :higherz";
			return session.createQuery(query, SignDAO.class).setParameter("world", location.getWorld())
					.setParameter("lowerx", location.getX() - radius).setParameter("higherx", location.getX() + radius)
					.setParameter("lowery", location.getY() - radius).setParameter("highery", location.getY() + radius)
					.setParameter("lowerz", location.getZ() - radius).setParameter("higherz", location.getZ() + radius)
					.getResultList();
		});
	}

	public static void persistSign(SignDAO sign) {
		persist(sign);
	}

	public static void deleteSign(SignDAO sign) {
		delete(sign);
	}

	public static void clearAllSigns() {
		update(DELETE_FROM_SIGNS);
	}

	/* UTILS */

	private static <T> T inTransaction(InTransactionExecutor<T> ite) {
		Session session = Registry.getHibernateUtils().getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		T ret = ite.executeInTransaction(session);
		transaction.commit();
		session.close();
		return ret;
	}

	private static <T> List<T> queryForList(String query, Class<T> clazz) {
		return inTransaction(session -> session.createQuery(query, clazz).getResultList());
	}

	private static void update(String query) {
		inTransaction(session -> session.createQuery(query).executeUpdate());
	}

	public static void persist(DAO o) {
		inTransaction(session -> {
			session.persist(o);
			return null;
		});
	}

	public static void delete(DAO o) {
		inTransaction(session -> {
			session.delete(o);
			return null;
		});
	}
}
