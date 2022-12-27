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

import de.headshotharp.chestsort.hibernate.dao.ChestDAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import de.headshotharp.plugin.hibernate.GenericDataProvider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaDelete;
import jakarta.persistence.criteria.Join;

public class ChestDataProvider extends GenericDataProvider<ChestDAO> {

    protected ChestDataProvider(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @Override
    public Class<ChestDAO> getEntityClass() {
        return ChestDAO.class;
    }

    public List<ChestDAO> findAllChests() {
        return findAll();
    }

    public List<ChestDAO> findChest(ChestDAO chest) {
        return findAllByPredicate((builder, criteria, chestRef, predicates) -> {
            Join<Object, Object> locationRef = chestRef.join(DataProvider.WH_LOCATION);
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_WORLD), chest.getLocation().getWorld()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_X), chest.getLocation().getX()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Y), chest.getLocation().getY()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Z), chest.getLocation().getZ()));
            predicates.add(builder.equal(chestRef.get(DataProvider.WH_MATERIAL), chest.getMaterial()));
            predicates.add(builder.equal(chestRef.get(DataProvider.WH_CENTRAL), chest.isCentral()));
            if (chest.getUsername() == null) {
                predicates.add(builder.isNull(chestRef.get(DataProvider.WH_USERNAME)));
            } else {
                predicates.add(builder.equal(chestRef.get(DataProvider.WH_USERNAME), chest.getUsername()));
            }
        });
    }

    public List<ChestDAO> findAllChestsAt(Location location) {
        return findAllByPredicate((builder, criteria, chestRef, predicates) -> {
            Join<Object, Object> locationRef = chestRef.join(DataProvider.WH_LOCATION);
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_WORLD), location.getWorld()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_X), location.getX()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Y), location.getY()));
            predicates.add(builder.equal(locationRef.get(DataProvider.WH_Z), location.getZ()));
        });
    }

    public List<ChestDAO> findAllChestsByMaterialAndUser(String material, String username) {
        return findAllByPredicate((builder, criteria, chestRef, predicates) -> {
            if (material != null) {
                predicates.add(builder.equal(chestRef.get(DataProvider.WH_MATERIAL), material));
            }
            predicates.add(builder.equal(chestRef.get(DataProvider.WH_CENTRAL), username == null));
            if (username != null) {
                predicates.add(builder.equal(chestRef.get(DataProvider.WH_USERNAME), username));
            }
        });
    }

    public List<ChestDAO> findAllCentralChestsByMaterial(String material) {
        return findAllChestsByMaterialAndUser(material, null);
    }

    public List<ChestDAO> findAllCentralChests() {
        return findAllChestsByMaterialAndUser(null, null);
    }

    public List<ChestDAO> findAllChestsByUser(String username) {
        return findAllChestsByMaterialAndUser(null, username);
    }

    public int clearAllChests() {
        return execInTransaction(session -> {
            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaDelete<ChestDAO> criteria = builder.createCriteriaDelete(ChestDAO.class);
            criteria.from(ChestDAO.class);
            return session.createMutationQuery(criteria).executeUpdate();
        });
    }
}
