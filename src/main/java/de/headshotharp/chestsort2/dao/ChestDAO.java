package de.headshotharp.chestsort2.dao;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.headshotharp.chestsort2.dao.generic.DAO;
import de.headshotharp.chestsort2.dao.generic.Location;

@Entity
@Table
public class ChestDAO extends DAO {
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
}
