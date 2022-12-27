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
package de.headshotharp.chestsort.hibernate.dao;

import de.headshotharp.chestsort.hibernate.dao.generic.DataAccessObject;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Entity
@Table
public class SignDAO implements DataAccessObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Location location;
    private boolean central = true;
    private String username = null;

    public SignDAO() {
    }

    public SignDAO(String world, int x, int y, int z, String username) {
        this(new Location(world, x, y, z), username);
    }

    public SignDAO(Location location, String username) {
        this.location = location;
        if (username != null) {
            this.username = username;
            central = false;
        }
    }

    public SignDAO(String world, int x, int y, int z) {
        this(new Location(world, x, y, z));
    }

    public SignDAO(Location location) {
        this.location = location;
    }

    public String getTextBlockString() {
        if (isCentral()) {
            return "central sign";
        }
        return "user sign (" + username + ")";
    }
}
