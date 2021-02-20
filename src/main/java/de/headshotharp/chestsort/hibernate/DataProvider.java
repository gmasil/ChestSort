package de.headshotharp.chestsort.hibernate;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Session;
import org.hibernate.Transaction;

import de.headshotharp.chestsort.Registry;
import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

public class DataProvider {
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
            Join<Object, Object> locationRef = chestRef.join("location");
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get("world"), chest.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get("x"), chest.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get("y"), chest.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get("z"), chest.getLocation().getZ()));
            predicates.add(builder.equal(chestRef.get("material"), chest.getMaterial()));
            predicates.add(builder.equal(chestRef.get("central"), chest.isCentral()));
            // predicate depending on null values
            if (chest.getUsername() == null) {
                predicates.add(builder.isNull(chestRef.get("username")));
            } else {
                predicates.add(builder.equal(chestRef.get("username"), chest.getUsername()));
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
            Join<Object, Object> locationRef = chestRef.join("location");
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get("world"), location.getWorld()));
            predicates.add(builder.equal(locationRef.get("x"), location.getX()));
            predicates.add(builder.equal(locationRef.get("y"), location.getY()));
            predicates.add(builder.equal(locationRef.get("z"), location.getZ()));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findAllCentralChestsByMaterial(String material) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            Root<ChestDAO> chestRef = criteria.from(ChestDAO.class);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(chestRef.get("material"), material));
            predicates.add(builder.equal(chestRef.get("central"), true));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
    }

    public List<ChestDAO> findAllChestsByMaterialAndUser(String material, String username) {
        return inTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaQuery<ChestDAO> criteria = builder.createQuery(ChestDAO.class);
            Root<ChestDAO> chestRef = criteria.from(ChestDAO.class);
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(chestRef.get("material"), material));
            predicates.add(builder.equal(chestRef.get("central"), false));
            predicates.add(builder.equal(chestRef.get("username"), username));
            // add predicates
            criteria.where(builder.and(predicates.toArray(new Predicate[0])));
            return session.createQuery(criteria).getResultList();
        });
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
            return session.createQuery(criteria).executeUpdate();
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
            Join<Object, Object> locationRef = signRef.join("location");
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get("world"), sign.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get("x"), sign.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get("y"), sign.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get("z"), sign.getLocation().getZ()));
            predicates.add(builder.equal(signRef.get("central"), sign.isCentral()));
            // predicate depending on null values
            if (sign.getUsername() == null) {
                predicates.add(builder.isNull(signRef.get("username")));
            } else {
                predicates.add(builder.equal(signRef.get("username"), sign.getUsername()));
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
            Join<Object, Object> locationRef = signRef.join("location");
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get("world"), location.getWorld()));
            predicates.add(builder.equal(locationRef.get("x"), location.getX()));
            predicates.add(builder.equal(locationRef.get("y"), location.getY()));
            predicates.add(builder.equal(locationRef.get("z"), location.getZ()));
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
            Join<Object, Object> locationRef = signRef.join("location");
            // set predicates
            List<Predicate> predicates = new LinkedList<>();
            predicates.add(builder.equal(locationRef.get("world"), location.getWorld()));
            predicates.add(builder.between(locationRef.get("x"), location.getX() - radius, location.getX() + radius));
            predicates.add(builder.between(locationRef.get("y"), location.getY() - radius, location.getY() + radius));
            predicates.add(builder.between(locationRef.get("z"), location.getZ() - radius, location.getZ() + radius));
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
            return session.createQuery(criteria).executeUpdate();
        });
    }

    /* UTILS */

    private <T> T inTransaction(InTransactionExecutor<T> ite) {
        Session session = Registry.getHibernateUtils().getSessionFactory().openSession();
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
            session.delete(o);
            return null;
        });
    }
}
