package de.headshotharp.chestsort;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class ChestSort extends JavaPlugin {
	public static final String PERMISSION_NAME_MANAGE = "chestsort.manage";
	public static final String PERMISSION_NAME_RESET = "chestsort.reset";

	private static final String chestFilename = "plugins" + File.separator + "ChestSort" + File.separator
			+ "chests.txt";
	private static final String signsFilename = "plugins" + File.separator + "ChestSort" + File.separator + "signs.txt";
	private static final String pluginDirectory = "plugins" + File.separator + "ChestSort";
	private PluginDescriptionFile pdf;
	private PlayerListener playerListener;
	private List<Chests> chests = new ArrayList<>();
	private List<Signs> signs = new ArrayList<>();

	@Override
	public void onEnable() {
		this.pdf = getDescription();
		this.playerListener = new PlayerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		loadChests();
		System.out.println(this.pdf.getName() + " Plugin v" + this.pdf.getVersion() + " enabled");
	}

	@Override
	public void onDisable() {
		saveChests();
		System.out.println(this.pdf.getName() + " Plugin v" + this.pdf.getVersion() + " disabled");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
		if (cmd.getName().equalsIgnoreCase("ChestSort")) {
			if ((sender instanceof Player)) {
				Player player = (Player) sender;
				if (args.length > 0) {
					if (args[0].equalsIgnoreCase("create")) {
						if (player.hasPermission(PERMISSION_NAME_MANAGE)) {
							if (args.length > 2) {
								createChest(player, args[1], args[2]);
							} else if (args.length == 1) {
								createChest(player, player.getInventory().getItemInMainHand().getType().toString(),
										"" + player.getInventory().getItemInMainHand().getDurability());
							} else {
								player.sendMessage(ChatColor.RED + "/ChestSort create (optional)<material> <data-nr>");
							}
						} else {
							player.sendMessage(ChatColor.RED + "Du darfst diesen Befehl nicht benutzen!");
						}
					} else if (args[0].equalsIgnoreCase("delete")) {
						if (player.hasPermission(PERMISSION_NAME_MANAGE)) {
							deleteChest(player);
						} else {
							player.sendMessage(ChatColor.RED + "Du darfst diesen Befehl nicht benutzen!");
						}
					} else if (args[0].equalsIgnoreCase("item")) {
						player.sendMessage(ChatColor.GRAY + "Your item in hand is " + ChatColor.BLUE
								+ player.getInventory().getItemInMainHand().getType().toString() + ChatColor.GRAY + " ("
								+ ChatColor.BLUE + player.getInventory().getItemInMainHand().getDurability()
								+ ChatColor.GRAY + ")");
					} else if (args[0].equalsIgnoreCase("info")) {
						info(player);
					} else if (args[0].equalsIgnoreCase("reset")) {
						if (player.hasPermission(PERMISSION_NAME_RESET)) {
							resetPlugin();
							player.sendMessage(ChatColor.GRAY + "The chestsort plugin has been resetted");
						} else {
							player.sendMessage(ChatColor.RED + "Du darfst diesen Befehl nicht benutzen!");
						}
					} else if (args[0].equalsIgnoreCase("list")) {
						list(player);
					} else if (args[0].equalsIgnoreCase("reload")) {
						if (player.hasPermission(PERMISSION_NAME_MANAGE)) {
							reloadPlugin();
							player.sendMessage(ChatColor.GREEN + "ChestSort has been reloaded!");
						} else {
							player.sendMessage(ChatColor.RED + "Du darfst diesen Befehl nicht benutzen!");
						}
					} else if (args[0].equalsIgnoreCase("check")) {
						if (player.hasPermission(PERMISSION_NAME_MANAGE)) {
							checkChests(player);
						} else {
							player.sendMessage(ChatColor.RED + "Du darfst diesen Befehl nicht benutzen!");
						}
					} else if (args[0].equalsIgnoreCase("all")) {
						if (isSignInRange(player)) {
							allInventory(player);
						} else {
							player.sendMessage("You must be in range of a ChestSort-Sign!");
						}
					} else {
						showHelp(player);
					}
				} else {
					showHelp(player);
				}
			} else if (args.length > 0) {
				if (args[0].equalsIgnoreCase("reset")) {
					resetPlugin();
					System.out.println("ChestSort has been resetted!");
				} else if (args[0].equalsIgnoreCase("reload")) {
					reloadPlugin();
				} else {
					showHelp(null);
				}
			} else {
				showHelp(null);
			}
		}
		return true;
	}

	private void checkChests(Player player) {
		player.sendMessage("Blocks which are no chests anymore:");
		int c = 0;
		for (Chests chest : this.chests) {
			if (!(chest.getLocation().getBlock().getState() instanceof Chest)) {
				c++;
				player.sendMessage("Chest = (" + chest.getLocation().getBlockX() + ", "
						+ chest.getLocation().getBlockY() + ", " + chest.getLocation().getBlockY() + ")");
			}
		}
		player.sendMessage("Damaged chests: " + c);
	}

	private void deleteChest(Player player) {
		if (this.playerListener.getMarkedLocation() != null) {
			boolean found = false;
			if (this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.CHEST)) {
				Chests foundChest = null;
				for (Chests chest : this.chests) {
					if (this.playerListener.getMarkedLocation().equals(chest.getLocation())) {
						foundChest = chest;
						found = true;
						break;
					}
				}
				if (foundChest != null) {
					this.chests.remove(foundChest);
					player.sendMessage(ChatColor.GREEN + "The marked chest has been deleted!");
				}
			} else {
				Signs foundSign = null;
				for (Signs sign : this.signs) {
					if (this.playerListener.getMarkedLocation().equals(sign.getLocation())) {
						foundSign = sign;
						found = true;
						break;
					}
				}
				if (foundSign != null) {
					foundSign.getLocation().getBlock().breakNaturally();
					this.signs.remove(foundSign);
					player.sendMessage(ChatColor.GREEN + "The marked sign has been deleted!");
				}
			}
			if (!found) {
				if (this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.CHEST)) {
					player.sendMessage(ChatColor.RED + "The marked chest is not registered!");
				} else {
					player.sendMessage(ChatColor.RED + "The marked block is no chest anymore!");
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You have to mark a chest or furnace first");
		}
	}

	private void resetPlugin() {
		this.chests = new ArrayList<>();
		for (Signs sign : this.signs) {
			sign.getLocation().getBlock().breakNaturally();
		}
		this.signs = new ArrayList<>();
	}

	private void createChest(Player player, String material, String data) {
		Location l = this.playerListener.getMarkedLocation();
		Material m = Material.getMaterial(material.toUpperCase());
		int d = Integer.parseInt(data);
		boolean err = false;
		if (l == null) {
			err = true;
			player.sendMessage(ChatColor.RED + "You have to mark a chest first");
		} else if (m == null) {
			err = true;
			player.sendMessage(
					ChatColor.RED + "There is no material of name " + ChatColor.BLUE + material.toUpperCase());
		}
		if (!err) {
			if (l.getBlock().getType().equals(Material.CHEST)) {
				if (!m.equals(Material.AIR)) {
					this.chests.add(new Chests(l, m, d));
					player.sendMessage(ChatColor.GREEN + "A chest of type " + ChatColor.BLUE + material.toUpperCase()
							+ ChatColor.GREEN + " has been created successfully!");
				} else {
					player.sendMessage(
							ChatColor.RED + "Are you kidding me? Do you really want to create a chest of type "
									+ ChatColor.BLUE + "AIR" + ChatColor.RED + " ??");
				}
			} else {
				player.sendMessage(ChatColor.RED + "The block at marked location is no chest anymore!");
			}
		}
	}

	private void showHelp(Player player) {
		if (player != null) {
			player.sendMessage("");
			player.sendMessage(ChatColor.GRAY + "-- ChestSort v" + this.pdf.getVersion() + " help --");
			player.sendMessage(ChatColor.BLUE + "/ChestSort create (optional)<material> <data-nr> " + ChatColor.GRAY
					+ "Creates a chest of type <material> with its <data-nr> or with the item in your hand at last marked chest");
			player.sendMessage(
					ChatColor.BLUE + "/ChestSort delete " + ChatColor.GRAY + "Deletes the last marked chest");
			player.sendMessage(ChatColor.BLUE + "/ChestSort reset " + ChatColor.GRAY
					+ "Resets all locations and deletes all chests and signs");
			player.sendMessage(
					ChatColor.BLUE + "/ChestSort reload " + ChatColor.GRAY + "Reloads the plugin and its config");
			player.sendMessage(
					ChatColor.BLUE + "/ChestSort info " + ChatColor.GRAY + "Shows info about the last marked chest");
			player.sendMessage(
					ChatColor.BLUE + "/ChestSort item " + ChatColor.GRAY + "Shows info about the item in your hand");
			player.sendMessage(
					ChatColor.BLUE + "/ChestSort list " + ChatColor.GRAY + "Shows a list of all marked chests");
			player.sendMessage(ChatColor.BLUE + "/ChestSort check " + ChatColor.GRAY
					+ "Lists all blocks which are no chests anymore");
			player.sendMessage(ChatColor.BLUE + "/ChestSort all " + ChatColor.GRAY
					+ "Puts all items into chests (you must be in range of a chestsort sign)");
			player.sendMessage(ChatColor.GRAY + "-----------------------");
		} else {
			System.out.println("-- ChestSort v" + this.pdf.getVersion() + " help --");
			System.out.println("/ChestSort reset - Resets all locations and deletes all chests and signs");
			System.out.println("/ChestSort reload - Reloads the plugin and its config");
			System.out.println("------------------------");
		}
	}

	private void info(Player player) {
		if (this.playerListener.getMarkedLocation() != null) {
			if (this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.CHEST)) {
				for (Chests chest : this.chests) {
					if (this.playerListener.getMarkedLocation().equals(chest.getLocation())) {
						player.sendMessage(ChatColor.GREEN + "The marked chest is of type " + ChatColor.BLUE
								+ chest.getMaterial().toString() + " (" + chest.getData() + ")");
						return;
					}
				}
				player.sendMessage(ChatColor.GRAY + "The marked chest is not registered!");
			} else if (this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.OAK_SIGN)) {
				for (Signs sign : this.signs) {
					if (this.playerListener.getMarkedLocation().equals(sign.getLocation())) {
						player.sendMessage(ChatColor.GREEN + "The marked sign is registered!");
						return;
					}
				}
				player.sendMessage(ChatColor.GRAY + "The marked sign is not registered!");
			} else {
				player.sendMessage(ChatColor.RED + "The marked block is no chest, furnace or sign anymore!");
			}
		} else {
			player.sendMessage(ChatColor.RED + "You have to mark a chest or furnace first");
		}
	}

	private void list(Player player) {
		int i = 0;
		for (Chests chest : this.chests) {
			i++;
			player.sendMessage(
					ChatColor.GRAY + "" + i + ". " + chest.getMaterial().toString() + " (" + chest.getData() + ")");
		}
		if (i == 0) {
			player.sendMessage(ChatColor.GRAY + "There are no chests registered!");
		}
		switch (this.signs.size()) {
		case 0:
			player.sendMessage(ChatColor.GRAY + "There are no signes registered!");
			break;
		case 1:
			player.sendMessage(ChatColor.GRAY + "There is one sign registered!");
			break;
		default:
			player.sendMessage(ChatColor.GRAY + "There are " + this.signs.size() + " signs registered!");
		}
	}

	private void setAmountInHand(Player player, int amount) {
		if (amount == 0) {
			player.getInventory().setItemInMainHand(null);
		} else {
			ItemStack nItemStack = new ItemStack(player.getInventory().getItemInMainHand().getType(), amount);
			nItemStack.setDurability(player.getInventory().getItemInMainHand().getDurability());
			player.getInventory().setItemInMainHand(nItemStack);
		}
	}

	public void fillChest(Player player) {
		int anzCorrectChests = 0;
		boolean placed = false;
		if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			for (Chests chest : this.chests) {
				if ((chest.getMaterial() == player.getInventory().getItemInMainHand().getType())
						&& (chest.getData() == player.getInventory().getItemInMainHand().getDurability())) {
					Chest ch = (Chest) chest.getLocation().getBlock().getState();
					for (int x = 0; x < ch.getInventory().getSize(); x++) {
						if (player.getInventory().getItemInMainHand().getAmount() != 0) {
							if (ch.getInventory().getContents()[x] != null) {
								if ((ch.getInventory().getContents()[x].getType() == player.getInventory()
										.getItemInMainHand().getType())
										&& (ch.getInventory().getContents()[x].getDurability() == player.getInventory()
												.getItemInMainHand().getDurability())) {
									anzCorrectChests++;
									int amount = ch.getInventory().getContents()[x].getAmount();
									int maxAmount = ch.getInventory().getContents()[x].getMaxStackSize();
									if (amount < maxAmount) {
										int toStack = maxAmount - amount;
										int plAmount = player.getInventory().getItemInMainHand().getAmount();
										if (plAmount > toStack) {
											ItemStack is = new ItemStack(chest.getMaterial(), maxAmount,
													(short) chest.getData());
											ch.getInventory().setItem(x, is);
											chest.getLocation().getBlock().getState().update(true);
											setAmountInHand(player, plAmount - toStack);
											placed = false;
										} else {
											ItemStack is = new ItemStack(chest.getMaterial(), plAmount + amount,
													(short) chest.getData());
											ch.getInventory().setItem(x, is);
											chest.getLocation().getBlock().getState().update(true);
											setAmountInHand(player, 0);
											placed = true;
										}
									}
								}
							} else {
								ItemStack is = new ItemStack(chest.getMaterial(),
										player.getInventory().getItemInMainHand().getAmount(), (short) chest.getData());
								ch.getInventory().setItem(x, is);
								chest.getLocation().getBlock().getState().update(true);
								setAmountInHand(player, 0);
								placed = true;
							}
						}
					}
					chest.getLocation().getBlock().getState().update(true);
				}
			}
			if (!placed) {
				if (anzCorrectChests > 0) {
					player.sendMessage(ChatColor.GRAY + "All chests of type " + ChatColor.BLUE
							+ player.getInventory().getItemInMainHand().getType().toString() + ChatColor.GRAY + " ("
							+ ChatColor.BLUE + player.getInventory().getItemInMainHand().getDurability()
							+ ChatColor.GRAY + ") are full");
				} else {
					player.sendMessage(ChatColor.GRAY + "There is no chest of type " + ChatColor.BLUE
							+ player.getInventory().getItemInMainHand().getType().toString() + ChatColor.GRAY + " ("
							+ ChatColor.BLUE + player.getInventory().getItemInMainHand().getDurability()
							+ ChatColor.GRAY + ")");
				}
			}
		}
	}

	private ItemStack insertInBox(ItemStack stack) {
		if (stack.getType() == Material.AIR) {
			return stack;
		}
		if (stack.getAmount() == 0) {
			return stack;
		}
		for (Chests chest : this.chests) {
			if ((chest.getMaterial() == stack.getType()) && (chest.getData() == stack.getDurability())) {
				try {
					Chest ch = (Chest) chest.getLocation().getBlock().getState();
					for (int x = 0; x < ch.getInventory().getSize(); x++) {
						if (stack.getAmount() != 0) {
							if (ch.getInventory().getContents()[x] != null) {
								int amount = ch.getInventory().getContents()[x].getAmount();
								int maxAmount = ch.getInventory().getContents()[x].getMaxStackSize();
								if (amount < maxAmount) {
									int toStack = maxAmount - amount;
									int plAmount = stack.getAmount();
									if (plAmount > toStack) {
										ItemStack is = new ItemStack(chest.getMaterial(), maxAmount,
												(short) chest.getData());
										ch.getInventory().setItem(x, is);
										chest.getLocation().getBlock().getState().update(true);
										stack.setAmount(plAmount - toStack);
									} else {
										ItemStack is = new ItemStack(chest.getMaterial(), amount + plAmount,
												(short) chest.getData());
										ch.getInventory().setItem(x, is);
										chest.getLocation().getBlock().getState().update(true);
										stack.setAmount(0);
										return stack;
									}
								}
							} else {
								ItemStack is = new ItemStack(chest.getMaterial(), stack.getAmount(),
										(short) chest.getData());
								ch.getInventory().setItem(x, is);
								chest.getLocation().getBlock().getState().update(true);
								stack.setAmount(0);
								return stack;
							}
						} else {
							return stack;
						}
					}
					ch.update(true);
				} catch (Exception e) {
					e.printStackTrace();
					System.out.println("ChestSort Error: Chest = (" + chest.getLocation().getBlockX() + ", "
							+ chest.getLocation().getBlockY() + ", " + chest.getLocation().getBlockY() + ")");
				}
			}
		}
		return stack;
	}

	private void allInventory(Player player) {
		for (int i = 0; i < player.getInventory().getContents().length; i++) {
			if (player.getInventory().getContents()[i] != null) {
				ItemStack left = insertInBox(new ItemStack(player.getInventory().getContents()[i]));
				if (left.getAmount() == 0) {
					left = null;
				}
				player.getInventory().setItem(i, left);
			}
		}
	}

	private boolean isSignInRange(Player player) {
		for (Signs sign : signs) {
			if ((sign.getLocation().getWorld() != player.getLocation().getWorld())
					|| (sign.getLocation().distance(player.getLocation()) <= 5.0D)) {
				return true;
			}
		}
		return false;
	}

	public List<Chests> getChests() {
		return this.chests;
	}

	public List<Signs> getSigns() {
		return this.signs;
	}

	private void loadChests() {
		try {
			new File(pluginDirectory).mkdir();
			if (new File(chestFilename).exists()) {
				FileInputStream fstream = new FileInputStream(chestFilename);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				this.chests = new ArrayList<Chests>();
				String strLine;
				while ((strLine = br.readLine()) != null) {
					if (!strLine.equalsIgnoreCase("null")) {
						String[] str = strLine.split(":");
						if (str.length > 5) {
							this.chests.add(new Chests(
									new Location(getServer().getWorld(str[0]), Integer.parseInt(str[1]),
											Integer.parseInt(str[2]), Integer.parseInt(str[3])),
									Material.getMaterial(str[4]), Integer.parseInt(str[5])));
						}
					}
				}
				in.close();
				br.close();
			}
			if (new File(signsFilename).exists()) {
				FileInputStream fstream = new FileInputStream(signsFilename);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				this.signs = new ArrayList<Signs>();
				String strLine;
				while ((strLine = br.readLine()) != null) {
					if (!strLine.equalsIgnoreCase("null")) {
						String[] str = strLine.split(":");
						this.signs.add(new Signs(new Location(getServer().getWorld(str[0]), Integer.parseInt(str[1]),
								Integer.parseInt(str[2]), Integer.parseInt(str[3]))));
					}
				}
				in.close();
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveChests() {
		try {
			new File(pluginDirectory).mkdir();
			// chests
			File f = new File(chestFilename);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			Writer noutput = new BufferedWriter(new FileWriter(f));
			for (Chests chest : this.chests) {
				String writeStr = chest.getLocation().getWorld().getName() + ":" + chest.getLocation().getBlockX() + ":"
						+ chest.getLocation().getBlockY() + ":" + chest.getLocation().getBlockZ() + ":"
						+ chest.getMaterial().toString().toUpperCase() + ":" + chest.getData() + "\n";
				noutput.write(writeStr);
			}
			String writeStr = "null";
			noutput.write(writeStr);
			noutput.close();
			// signs
			f = new File(signsFilename);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			noutput = new BufferedWriter(new FileWriter(f));
			for (Signs sign : this.signs) {
				writeStr = sign.getLocation().getWorld().getName() + ":" + sign.getLocation().getBlockX() + ":"
						+ sign.getLocation().getBlockY() + ":" + sign.getLocation().getBlockZ() + "\n";
				noutput.write(writeStr);
			}
			writeStr = "null";
			noutput.write(writeStr);
			noutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void reloadPlugin() {
		saveChests();
		loadChests();
		System.out.println("ChestSort has been reloaded!");
	}
}
