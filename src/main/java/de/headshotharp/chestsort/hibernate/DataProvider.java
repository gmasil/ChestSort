/**
 * ChestSort
 * Copyright © 2021 gmasil.de
 *
 * This file is part of ChestSort.
 *
 * ChestSort is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ChestSort is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChestSort. If not, see <https://www.gnu.org/licenses/>.
 */
package de.headshotharp.chestsort.hibernate;

import java.util.LinkedList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import de.headshotharp.chestsort.config.Config.DatabaseConfig;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class DataProvider {

    private static final String WH_LOCATION = "location";
    private static final String WH_WORLD = "world";
    private static final String WH_X = "x";
    private static final String WH_Y = "y";
    private static final String WH_Z = "z";
    private static final String WH_MATERIAL = "material";
    private static final String WH_CENTRAL = "central";
    private static final String WH_USERNAME = "username";

    private SessionFactory sessionFactory;

    public DataProvider(DatabaseConfig databaseConfig) {
        sessionFactory = new HibernateUtils(databaseConfig).createSessionFactory();
    }

    /* CHEST */

    public List<ChestDAO> findAllChests() {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            criteria.from(ChestDAO.class);
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findChest(ChestDAO chest) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            Root<ChestDAO> chestRef = criteria.from(ChestDAO.class);
            Join<Object, Object> locationRef = chestRef.join(WH_LOCATION);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get(WH_WORLD), chest.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get(WH_X), chest.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get(WH_Y), chest.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get(WH_Z), chest.getLocation().getZ()));
            predicates.add(builder.equal(chestRef.get(WH_MATERIAL), chest.getMaterial()));
            predicates.add(builder.equal(chestRef.get(WH_CENTRAL), chest.isCentral()));
            // predicate depending on null values
            if (chest.getUsername() == null) {
                predicates.add(builder.isNull(chestRef.get(WH_USERNAME)));
            } else {
                predicates.add(builder.equal(chestRef.get(WH_USERNAME), chest.getUsername()));
            }
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findAllChestsAt(Location location) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            Root<ChestDAO> chestRef = criteria.from(ChestDAO.class);
            Join<Object, Object> locationRef = chestRef.join(WH_LOCATION);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get(WH_WORLD), location.getWorld()));
            predicates.add(builder.equal(locationRef.get(WH_X), location.getX()));
            predicates.add(builder.equal(locationRef.get(WH_Y), location.getY()));
            predicates.add(builder.equal(locationRef.get(WH_Z), location.getZ()));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findAllCentralChestsByMaterial(String material) {
        return findAllChestsByMaterialAndUser(material, null);
    }

    public List<ChestDAO> findAllCentralChests() {
        return findAllChestsByMaterialAndUser(null, null);
    }

    public List<ChestDAO> findAllChestsByMaterialAndUser(String material, String username) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            Root<ChestDAO> chestRef = criteria.from(ChestDAO.class);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            if (material != null) {
                predicates.add(builder.equal(chestRef.get(WH_MATERIAL), material));
            }
            predicates.add(builder.equal(chestRef.get(WH_CENTRAL), username == null));
            if (username != null) {
                predicates.add(builder.equal(chestRef.get(WH_USERNAME), username));
            }
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findAllChestsByUser(String username) {
        return findAllChestsByMaterialAndUser(null, username);
    }

    public void persistChest(ChestDAO chest) {
        persist(chest);
    }

    public void deleteChest(ChestDAO chest) {
        delete(chest);
    }

    public int clearAllChests() {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<ChestDAO> criteria = builder.createCriteriaDelete(ChestDAO.class);
            criteria.from(ChestDAO.class);
            return session.createMutationQuery(criteria).executeUpdate();
        });
    }

    /* SIGN */

    public List<SignDAO> findAllSigns() {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SignDAO> criteria = builder.createQuery(SignDAO.class);
            criteria.from(SignDAO.class);
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<SignDAO> findSign(SignDAO sign) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SignDAO> criteria = builder.createQuery(SignDAO.class);
            Root<SignDAO> signRef = criteria.from(SignDAO.class);
            Join<Object, Object> locationRef = signRef.join(WH_LOCATION);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get(WH_WORLD), sign.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get(WH_X), sign.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get(WH_Y), sign.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get(WH_Z), sign.getLocation().getZ()));
            predicates.add(builder.equal(signRef.get(WH_CENTRAL), sign.isCentral()));
            // predicate depending on null values
            if (sign.getUsername() == null) {
                predicates.add(builder.isNull(signRef.get(WH_USERNAME)));
            } else {
                predicates.add(builder.equal(signRef.get(WH_USERNAME), sign.getUsername()));
            }
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<SignDAO> findAllSignsAt(Location location) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SignDAO> criteria = builder.createQuery(SignDAO.class);
            Root<SignDAO> signRef = criteria.from(SignDAO.class);
            Join<Object, Object> locationRef = signRef.join(WH_LOCATION);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get(WH_WORLD), location.getWorld()));
            predicates.add(builder.equal(locationRef.get(WH_X), location.getX()));
            predicates.add(builder.equal(locationRef.get(WH_Y), location.getY()));
            predicates.add(builder.equal(locationRef.get(WH_Z), location.getZ()));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<SignDAO> findAllCentralSigns() {
        return findAllSignsByUser(null);
    }

    public List<SignDAO> findAllSignsByUser(String username) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SignDAO> criteria = builder.createQuery(SignDAO.class);
            Root<SignDAO> signRef = criteria.from(SignDAO.class);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(signRef.get(WH_CENTRAL), username == null));
            if (username != null) {
                predicates.add(builder.equal(signRef.get(WH_USERNAME), username));
            }
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<SignDAO> findAllSignsAround(Location location, int radius) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<SignDAO> criteria = builder.createQuery(SignDAO.class);
            Root<SignDAO> signRef = criteria.from(SignDAO.class);
            Join<Object, Object> locationRef = signRef.join(WH_LOCATION);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get(WH_WORLD), location.getWorld()));
            predicates.add(builder.between(locationRef.get(WH_X), location.getX() - radius, location.getX() + radius));
            predicates.add(builder.between(locationRef.get(WH_Y), location.getY() - radius, location.getY() + radius));
            predicates.add(builder.between(locationRef.get(WH_Z), location.getZ() - radius, location.getZ() + radius));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public void persistSign(SignDAO sign) {
        persist(sign);
    }

    public void deleteSign(SignDAO sign) {
        delete(sign);
    }

    public int clearAllSigns() {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<SignDAO> criteria = builder.createCriteriaDelete(SignDAO.class);
            criteria.from(SignDAO.class);
            return session.createMutationQuery(criteria).executeUpdate();
        });
    }

    /* UTILS */

    private <T> T inTransaction(InTransactionExecutor<T> ite) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        T ret = ite.executeInTransaction(session);
        transaction.commit();
        session.close();
        return ret;
    }

    public void persist(DAO o) {
        inTransaction(session -> {
            session.persist(o);
            return null;
        });
    }

    public void delete(DAO o) {
        inTransaction(session -> {
            session.remove(o);
            return null;
        });
    }
}
