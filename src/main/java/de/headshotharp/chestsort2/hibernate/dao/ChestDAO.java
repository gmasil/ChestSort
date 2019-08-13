package de.headshotharp.chestsort2.hibernate.dao;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.headshotharp.chestsort2.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

@Entity
@Table
public class ChestDAO extends DAO {
	@Embedded
	private Location location;
	private String material;

	public ChestDAO() {
	}

	public ChestDAO(Location location, String material) {
		this.location = location;
		this.material = material;
	}

	public ChestDAO(String world, int x, int y, int z, String material) {
		this.location = new Location(world, x, y, z);
		this.material = material;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((location == null) ? 0 : location.hashCode());
		result = prime * result + ((material == null) ? 0 : material.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ChestDAO other = (ChestDAO) obj;
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
		return true;
	}
}
