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

import java.util.List;

import org.hibernate.SessionFactory;

import de.headshotharp.chestsort.hibernate.dao.SignDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import de.headshotharp.plugin.hibernate.GenericDataProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Join;

public class SignDataProvider extends GenericDataProvider<SignDAO> {

    protected SignDataProvider(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<SignDAO> getEntityClass() {
        return SignDAO.class;
    }

    public List<SignDAO> findAllSigns() {
        return findAll();
    }

    public List<SignDAO> findSign(SignDAO sign) {
        return findAllByPredicate((builder, criteria, signRef, predicates) -> {
            Join<Object, Object> locationRef = signRef.join(DataProvider.WH_LOCATION);
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_WORLD), sign.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_X), sign.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Y), sign.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Z), sign.getLocation().getZ()));
            predicates.add(builder.equal(signRef.get(DataProvider.WH_CENTRAL), sign.isCentral()));
            // predicate depending on null values
            if (sign.getUsername() == null) {
                predicates.add(builder.isNull(signRef.get(DataProvider.WH_USERNAME)));
            } else {
                predicates.add(builder.equal(signRef.get(DataProvider.WH_USERNAME), sign.getUsername()));
            }
        });
    }

    public List<SignDAO> findAllSignsAt(Location location) {
        return findAllByPredicate((builder, criteria, signRef, predicates) -> {
            Join<Object, Object> locationRef = signRef.join(DataProvider.WH_LOCATION);
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_WORLD), location.getWorld()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_X), location.getX()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Y), location.getY()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Z), location.getZ()));
        });
    }

    public List<SignDAO> findAllCentralSigns() {
        return findAllSignsByUser(null);
    }

    public List<SignDAO> findAllSignsByUser(String username) {
        return findAllByPredicate((builder, criteria, signRef, predicates) -> {
            predicates.add(builder.equal(signRef.get(DataProvider.WH_CENTRAL), username == null));
            if (username != null) {
                predicates.add(builder.equal(signRef.get(DataProvider.WH_USERNAME), username));
            }
        });
    }

    public List<SignDAO> findAllSignsAround(Location location, int radius) {
        return findAllByPredicate((builder, criteria, signRef, predicates) -> {
            Join<Object, Object> locationRef = signRef.join(DataProvider.WH_LOCATION);
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_WORLD), location.getWorld()));
            predicates.add(builder.between(locationRef.get(DataProvider.WH_X), location.getX() - radius,
                    location.getX() + radius));
            predicates.add(builder.between(locationRef.get(DataProvider.WH_Y), location.getY() - radius,
                    location.getY() + radius));
            predicates.add(builder.between(locationRef.get(DataProvider.WH_Z), location.getZ() - radius,
                    location.getZ() + radius));
        });
    }

    public int clearAllSigns() {
        return execInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<SignDAO> criteria = builder.createCriteriaDelete(SignDAO.class);
            criteria.from(SignDAO.class);
            return session.createMutationQuery(criteria).executeUpdate();
        });
    }
}
