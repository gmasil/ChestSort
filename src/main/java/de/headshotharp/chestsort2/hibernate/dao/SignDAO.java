package de.headshotharp.chestsort2.hibernate.dao;

import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;

import de.headshotharp.chestsort2.hibernate.dao.generic.DAO;
import de.headshotharp.chestsort2.hibernate.dao.generic.Location;

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
		this.central = false;
		this.username = username;
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
}
