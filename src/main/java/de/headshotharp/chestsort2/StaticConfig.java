package de.headshotharp.chestsort2;

import org.bukkit.ChatColor;
import org.bukkit.Material;

public class StaticConfig {
	private StaticConfig() {
	}

	public static final ChatColor COLOR_ERROR = ChatColor.DARK_RED;
	public static final ChatColor COLOR_ERROR_HIGHLIGHT = ChatColor.RED;
	public static final ChatColor COLOR_NORMAL = ChatColor.BLUE;
	public static final ChatColor COLOR_GOOD = ChatColor.GREEN;

	public static final String PERMISSION_MANAGE = "chestsort.manage";
	public static final String PERMISSION_MANAGE_CENTRAL = "chestsort.manage.central";
	public static final String PERMISSION_RESET = "chestsort.reset";

	public static final Material MATERIAL_SIGN_CENTRAL = Material.OAK_SIGN;
	public static final Material MATERIAL_SIGN_USER = Material.BIRCH_SIGN;
	public static final Material MATERIAL_MARKER = Material.STICK;

}
