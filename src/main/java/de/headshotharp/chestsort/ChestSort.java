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
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Furnace;
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
	private static final String furnacesFilename = "plugins" + File.separator + "ChestSort" + File.separator
			+ "furnaces.txt";
	private static final String roomsFilename = "plugins" + File.separator + "ChestSort" + File.separator + "rooms.txt";
	private static final String signsFilename = "plugins" + File.separator + "ChestSort" + File.separator + "signs.txt";
	private static final String pluginDirectory = "plugins" + File.separator + "ChestSort";
	private PluginDescriptionFile pdf;
	private PlayerListener playerListener;
	private ArrayList<Chests> chests = new ArrayList<Chests>();
	private ArrayList<Furnaces> furnaces = new ArrayList<Furnaces>();
	private ArrayList<Signs> signs = new ArrayList<Signs>();
	private ArrayList<Room> rooms = new ArrayList<Room>();

	public void onEnable() {
		this.pdf = getDescription();
		this.playerListener = new PlayerListener(this);
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.playerListener, this);
		loadChests();
		System.out.println(this.pdf.getName() + " Plugin v" + this.pdf.getVersion() + " enabled");
	}

	public void onDisable() {
		saveChests();
		System.out.println(this.pdf.getName() + " Plugin v" + this.pdf.getVersion() + " disabled");
	}

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
					} else if (args[0].equalsIgnoreCase("rooms")) {
						player.sendMessage("rooms: " + rooms.size());
						for (Room room : this.rooms) {
							player.sendMessage(room.getName());
						}
					} else if (args[0].equalsIgnoreCase("search")) {
						Chests chest = search(player.getInventory().getItemInMainHand());
						if (chest == null) {
							player.sendMessage("Not found.");
						} else {
							for (Room room : this.rooms) {
								if (room.isIn(chest.getLocation())) {
									player.sendMessage("Chest found in room: " + room.getName());
									return true;
								}
							}
							player.sendMessage("Chest outside all known rooms: " + chest.getLocation().toString());
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

	public Chests search(ItemStack stack) {
		for (Chests chest : this.chests) {
			if ((chest.getMaterial() == stack.getType()) && (chest.getData() == stack.getDurability()))
				return chest;
		}
		return null;
	}

	public Chests searchContains(ItemStack stack) {
		for (Chests chest : this.chests) {
			if ((chest.getMaterial() == stack.getType()) && (chest.getData() == stack.getDurability())) {
				Chest ch = (Chest) chest.getLocation().getBlock().getState();
				for (int x = 0; x < ch.getInventory().getSize(); x++) {
					if (ch.getInventory().getContents()[x] != null) {
						if (ch.getInventory().getContents()[x].getType() == stack.getType()
								&& ch.getInventory().getContents()[x].getDurability() == stack.getDurability())
							return chest;
					}
				}
			}
		}
		return null;
	}

	public void checkChests(Player player) {
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

	public void deleteChest(Player player) {
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
			} else if ((this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.FURNACE))
					|| (this.playerListener.getMarkedLocation().getBlock().getType()
							.equals(Material.BURNING_FURNACE))) {
				Furnaces foundFurnace = null;
				for (Furnaces furnace : this.furnaces) {
					if (this.playerListener.getMarkedLocation().equals(furnace.getLocation())) {
						foundFurnace = furnace;
						found = true;
						break;
					}
				}
				if (foundFurnace != null) {
					this.furnaces.remove(foundFurnace);
					player.sendMessage(ChatColor.GREEN + "The marked furnace has been deleted!");
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
				} else if ((this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.FURNACE))
						|| (this.playerListener.getMarkedLocation().getBlock().getType()
								.equals(Material.BURNING_FURNACE))) {
					player.sendMessage(ChatColor.RED + "The marked furnace is not registered!");
				} else {
					player.sendMessage(ChatColor.RED + "The marked block is no chest or furnace anymore!");
				}
			}
		} else {
			player.sendMessage(ChatColor.RED + "You have to mark a chest or furnace first");
		}
	}

	public void resetPlugin() {
		this.chests = new ArrayList<Chests>();
		this.furnaces = new ArrayList<Furnaces>();
		for (Signs sign : this.signs) {
			sign.getLocation().getBlock().breakNaturally();
		}
		this.signs = new ArrayList<Signs>();
	}

	public void createChest(Player player, String material, String data) {
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
			} else if ((l.getBlock().getType().equals(Material.FURNACE))
					|| (l.getBlock().getType().equals(Material.BURNING_FURNACE))) {
				boolean isRegistered = false;
				for (Furnaces furnace : this.furnaces) {
					if (this.playerListener.getMarkedLocation().equals(furnace.getLocation())) {
						player.sendMessage(ChatColor.GRAY + "This furnace is already registered!");
						isRegistered = true;
					}
				}
				if (!isRegistered) {
					this.furnaces.add(new Furnaces(this.playerListener.getMarkedLocation()));
					player.sendMessage(ChatColor.GREEN + "This furnace has been created successfully!");
				}
			} else {
				player.sendMessage(ChatColor.RED + "The block at marked location is no chest or furnace anymore!");
			}
		}
	}

	public void showHelp(Player player) {
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

	public void info(Player player) {
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
			} else if ((this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.FURNACE))
					|| (this.playerListener.getMarkedLocation().getBlock().getType()
							.equals(Material.BURNING_FURNACE))) {
				for (Furnaces furnace : this.furnaces) {
					if (this.playerListener.getMarkedLocation().equals(furnace.getLocation())) {
						player.sendMessage(ChatColor.GREEN + "The marked furnace is registered!");
						return;
					}
				}
				player.sendMessage(ChatColor.GRAY + "The marked furnace is not registered!");
			} else if (this.playerListener.getMarkedLocation().getBlock().getType().equals(Material.SIGN_POST)) {
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

	public void list(Player player) {
		int i = 0;
		for (Chests chest : this.chests) {
			i++;
			player.sendMessage(
					ChatColor.GRAY + "" + i + ". " + chest.getMaterial().toString() + " (" + chest.getData() + ")");
		}
		if (i == 0) {
			player.sendMessage(ChatColor.GRAY + "There are no chests registered!");
		}
		switch (this.furnaces.size()) {
		case 0:
			player.sendMessage(ChatColor.GRAY + "There are no furnaces registered!");
			break;
		case 1:
			player.sendMessage(ChatColor.GRAY + "There is one furnace registered!");
			break;
		default:
			player.sendMessage(ChatColor.GRAY + "There are " + this.furnaces.size() + " furnaces registered!");
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

	public void setAmountInHand(Player player, int amount) {
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
		if (player.getInventory().getItemInMainHand().getType().equals(Material.COAL)) {
			for (Furnaces furnace : this.furnaces) {
				Furnace fu = (Furnace) furnace.getLocation().getBlock().getState();
				if (fu.getInventory().getContents()[1] == null) {
					if (fu.getInventory().getContents()[0] == null) {
						ItemStack holder = new ItemStack(Material.APPLE, 13);
						fu.getInventory().addItem(new ItemStack[] { holder });
						fu.getInventory().addItem(new ItemStack[] { player.getInventory().getItemInMainHand() });
						fu.getInventory().remove(holder);
						setAmountInHand(player, 0);
					} else {
						fu.getInventory().addItem(new ItemStack[] { player.getInventory().getItemInMainHand() });
						setAmountInHand(player, 0);
					}
				} else if (fu.getInventory().getContents()[1].getType().equals(Material.COAL)) {
					if (fu.getInventory().getContents()[1].getAmount() < fu.getInventory().getContents()[1]
							.getMaxStackSize()) {
						int toStack = fu.getInventory().getContents()[1].getMaxStackSize()
								- fu.getInventory().getContents()[1].getAmount();
						if (toStack < player.getInventory().getItemInMainHand().getAmount()) {
							fu.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COAL, toStack) });
							setAmountInHand(player, player.getInventory().getItemInMainHand().getAmount() - toStack);
						} else {
							fu.getInventory().addItem(new ItemStack[] { player.getInventory().getItemInMainHand() });
							setAmountInHand(player, 0);
						}
					}
				}
				fu.update();
			}
		}
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

	public ItemStack insertInBox(ItemStack stack) {
		if (stack.getType() == Material.AIR) {
			return stack;
		}
		if (stack.getAmount() == 0) {
			return stack;
		}
		if (stack.getType() == Material.COAL) {
			for (Furnaces furnace : this.furnaces) {
				Furnace fu = (Furnace) furnace.getLocation().getBlock().getState();
				if (fu.getInventory().getContents()[1] == null) {
					if (fu.getInventory().getContents()[0] == null) {
						ItemStack holder = new ItemStack(Material.APPLE, 13);
						fu.getInventory().addItem(new ItemStack[] { holder });
						fu.getInventory().addItem(new ItemStack[] { stack });
						fu.getInventory().remove(holder);
						stack.setAmount(0);
						return stack;
					}
					fu.getInventory().addItem(new ItemStack[] { stack });
					stack.setAmount(0);
					return stack;
				}
				if (fu.getInventory().getContents()[1].getType().equals(Material.COAL)) {
					if (fu.getInventory().getContents()[1].getAmount() < fu.getInventory().getContents()[1]
							.getMaxStackSize()) {
						int toStack = fu.getInventory().getContents()[1].getMaxStackSize()
								- fu.getInventory().getContents()[1].getAmount();
						if (toStack < stack.getAmount()) {
							fu.getInventory().addItem(new ItemStack[] { new ItemStack(Material.COAL, toStack) });
							stack.setAmount(stack.getAmount() - toStack);
						} else {
							fu.getInventory().addItem(new ItemStack[] { stack });
							stack.setAmount(0);
							return stack;
						}
					}
				}
				fu.update();
			}
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

	public void allInventory(Player player) {
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

	public boolean isSignInRange(Player player) {
		for (Signs sign : signs) {
			if ((sign.getLocation().getWorld() != player.getLocation().getWorld())
					|| (sign.getLocation().distance(player.getLocation()) <= 5.0D))
				return true;
		}
		return false;
	}

	public ArrayList<Chests> getChests() {
		return this.chests;
	}

	public ArrayList<Furnaces> getFurnaces() {
		return this.furnaces;
	}

	public ArrayList<Signs> getSigns() {
		return this.signs;
	}

	public void loadChests() {
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
			if (new File(furnacesFilename).exists()) {
				FileInputStream fstream = new FileInputStream(furnacesFilename);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));

				this.furnaces = new ArrayList<Furnaces>();
				String strLine;
				while ((strLine = br.readLine()) != null) {
					if (!strLine.equalsIgnoreCase("null")) {
						String[] str = strLine.split(":");
						this.furnaces.add(new Furnaces(new Location(getServer().getWorld(str[0]),
								Integer.parseInt(str[1]), Integer.parseInt(str[2]), Integer.parseInt(str[3]))));
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
			if (new File(roomsFilename).exists()) {
				FileInputStream fstream = new FileInputStream(roomsFilename);
				DataInputStream in = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				this.rooms = new ArrayList<Room>();
				String strLine;
				while ((strLine = br.readLine()) != null) {
					if (!strLine.equalsIgnoreCase("null")) {
						String[] str = strLine.split(":");
						Location locA = new Location(getServer().getWorld(str[0]), Integer.parseInt(str[1]),
								Integer.parseInt(str[2]), Integer.parseInt(str[3]));
						Location locB = new Location(getServer().getWorld(str[4]), Integer.parseInt(str[5]),
								Integer.parseInt(str[6]), Integer.parseInt(str[7]));
						this.rooms.add(new Room(str[8], locA, locB));
						System.out.println("loading room: " + str[8]);
					}
				}
				in.close();
				br.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void saveChests() {
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
			// furnaces
			f = new File(furnacesFilename);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			noutput = new BufferedWriter(new FileWriter(f));
			for (Furnaces furnace : this.furnaces) {
				writeStr = furnace.getLocation().getWorld().getName() + ":" + furnace.getLocation().getBlockX() + ":"
						+ furnace.getLocation().getBlockY() + ":" + furnace.getLocation().getBlockZ() + "\n";
				noutput.write(writeStr);
			}
			writeStr = "null";
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
			// rooms
			f = new File(roomsFilename);
			if (f.exists()) {
				f.delete();
			}
			f.createNewFile();
			noutput = new BufferedWriter(new FileWriter(f));
			for (Room room : this.rooms) {
				writeStr = room.getLocA().getWorld().getName() + ":" + room.getLocA().getBlockX() + ":"
						+ room.getLocA().getBlockY() + ":" + room.getLocA().getBlockZ() + ":";
				writeStr += room.getLocB().getWorld().getName() + ":" + room.getLocB().getBlockX() + ":"
						+ room.getLocB().getBlockY() + ":" + room.getLocB().getBlockZ() + ":"
						+ room.getName().replace(":", "") + "\n";
				noutput.write(writeStr);
				System.out.println("savibng room: " + room.getName());
			}
			writeStr = "null";
			noutput.write(writeStr);
			noutput.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void reloadPlugin() {
		saveChests();
		loadChests();
		System.out.println("ChestSort has been reloaded!");
	}
}
