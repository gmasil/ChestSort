package de.headshotharp.chestsort;

import org.bukkit.Location;

public class Block {
	private Location loc;

	public Block(Location loc) {
		this.loc = loc;
	}

	public Location getLocation() {
		return loc;
	}
}
