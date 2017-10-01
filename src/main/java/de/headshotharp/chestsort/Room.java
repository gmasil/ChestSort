package de.headshotharp.chestsort;

import org.bukkit.Location;

public class Room {
	private String name;
	private Location locA, locB;

	public Room(String name, Location locA, Location locB) {
		this.name = name;
		this.locA = locA;
		this.locB = locB;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocA() {
		return locA;
	}

	public void setLocA(Location locA) {
		this.locA = locA;
	}

	public Location getLocB() {
		return locB;
	}

	public void setLocB(Location locB) {
		this.locB = locB;
	}

	public boolean isIn(Location loc) {
		if (!locA.getWorld().getName().equals(locB.getWorld().getName()))
			return false;
		if (!locA.getWorld().getName().equals(loc.getWorld().getName()))
			return false;
		if (Math.min(locA.getBlockX(), locB.getBlockX()) > loc.getBlockX())
			return false;
		if (Math.max(locA.getBlockX(), locB.getBlockX()) < loc.getBlockX())
			return false;
		if (Math.min(locA.getBlockY(), locB.getBlockY()) > loc.getBlockY())
			return false;
		if (Math.max(locA.getBlockY(), locB.getBlockY()) < loc.getBlockY())
			return false;
		if (Math.min(locA.getBlockZ(), locB.getBlockZ()) > loc.getBlockZ())
			return false;
		if (Math.max(locA.getBlockZ(), locB.getBlockZ()) < loc.getBlockZ())
			return false;
		return true;
	}
}
