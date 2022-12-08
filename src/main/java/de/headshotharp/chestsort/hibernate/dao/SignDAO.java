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

import de.headshotharp.chestsort.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table
public class SignDAO extends DAO {

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

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isCentral() {
        return central;
    }

    public void setCentral(boolean central) {
        this.central = central;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTextBlockString() {
        if (isCentral()) {
            return "central sign";
        }
        return "user sign (" + username + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (central ? 1231 : 1237);
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SignDAO other = (SignDAO) obj;
        if (central != other.central) {
            return false;
        }
        if (location == null) {
            if (other.location != null) {
                return false;
            }
        } else if (!location.equals(other.location)) {
            return false;
        }
        if (username == null) {
            if (other.username != null) {
                return false;
            }
        } else if (!username.equals(other.username)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "SignDAO [location=" + location + ", central=" + central + ", username=" + username + "]";
    }
}
