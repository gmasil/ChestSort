package de.headshotharp.chestsort2.dao;

import javax.persistence.Entity;
import javax.persistence.Table;

import de.headshotharp.chestsort2.dao.generic.DAO;
import de.headshotharp.chestsort2.dao.generic.Location;

@Entity
@Table
public class SignDAO extends DAO {
	private Location location;
	private boolean central = true;
	private String username = null;

	public SignDAO() {
	}

	public SignDAO(Location location, String username) {
		this.location = location;
		this.central = false;
		this.username = username;
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
}
