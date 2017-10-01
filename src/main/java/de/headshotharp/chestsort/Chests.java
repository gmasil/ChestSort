package de.headshotharp.chestsort;

import org.bukkit.Location;
import org.bukkit.Material;

public class Chests extends Block {
	Material material;
	int data;

	public Chests(Location loc, Material material, int data) {
		super(loc);
		this.material = material;
		this.data = data;
	}

	public Material getMaterial() {
		return this.material;
	}

	public void setMaterial(String name) {
		try {
			this.material = Material.getMaterial(name.toUpperCase());
		} catch (Exception e) {
			this.material = Material.AIR;
			System.out.println("[ChestSort] Error while loading material");
		}
	}

	public void setData(int data) {
		this.data = data;
	}

	public int getData() {
		return data;
	}
}
