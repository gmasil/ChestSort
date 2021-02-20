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

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.headshotharp.chestsort.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort.hibernate.dao.generic.Location;

@Entity
@Table
public class ChestDAO extends DAO {
    @Embedded
    private Location location;
    private String material;
    private boolean central = true;
    private String username = null;

    public ChestDAO() {
    }

    public ChestDAO(Location location, String material) {
        this.location = location;
        this.material = material;
    }

    public ChestDAO(Location location, String material, String username) {
        this(location, material);
        if (username != null) {
            this.username = username;
            this.central = false;
        }
    }

    public ChestDAO(String world, int x, int y, int z, String material) {
        this(new Location(world, x, y, z), material);
    }

    public ChestDAO(String world, int x, int y, int z, String material, String username) {
        this(new Location(world, x, y, z), material, username);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
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
            return "central chest of type " + getMaterial();
        }
        return "user chest of type " + getMaterial() + " (" + username + ")";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (central ? 1231 : 1237);
        result = prime * result + ((location == null) ? 0 : location.hashCode());
        result = prime * result + ((material == null) ? 0 : material.hashCode());
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
        ChestDAO other = (ChestDAO) obj;
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
        if (material == null) {
            if (other.material != null) {
                return false;
            }
        } else if (!material.equals(other.material)) {
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
        return "ChestDAO [location=" + location + ", material=" + material + ", central=" + central + ", username="
                + username + "]";
    }
}
