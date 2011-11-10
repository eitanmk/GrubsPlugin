package com.selfequalsthis.grubsplugin.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;

public class TeleportModule extends AbstractGrubsModule {

	private HashMap<String,Location> teleportPresets = new HashMap<String,Location>();
	private Properties teleportProperties = new Properties();

	private String teleportMainDirectory = "plugins/TeleportPresets";
	private File TeleportPresetFile = new File(teleportMainDirectory + File.separator + "presets.dat"); 
	
	public TeleportModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[TeleportModule]: ";
		this.dataFileName = "teleports.dat";
	}
	
	@Override
	public void enable() {		
		this.registerCommand("goto");
		this.registerCommand("fetch");
		this.registerCommand("send");
		this.registerCommand("tpset");
		this.registerCommand("tpdel");
		this.registerCommand("tplist");
		
		File dataFile = this.getDataFile();
		if (dataFile != null) {
		
			if (TeleportPresetFile.exists()){
				this.log("Old preset file exists. Moving to new location.");
				boolean succeeded = TeleportPresetFile.renameTo(dataFile);
				if (!succeeded) {
					this.log("Failed to move preset file to new location!");
					return;
				}
			}
			else {
				this.log("Can remove the old data file code. It's been migrated already.");
			}
			
			this.log("Loading Teleport presets.");
			loadTeleportPresets();
			this.log("Loaded " + teleportPresets.size() + " presets.");
		}
		
	}

	@Override
	public void disable() {
		this.log("Saving Teleport presets.");
		saveTeleportPresets();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("goto")) {
			if (args.length > 0) {
				String argName = args[0];

				// check presets
				if (argName.equalsIgnoreCase("last")) {
					if (getLastLocation(executingPlayer) != null) {
						// copy the object, b/c we don't want to overwrite the reference
						Location playerLastLocation = getLastLocation(executingPlayer).clone();
						// save the current location
						saveLastLocation(executingPlayer);
						executingPlayer.teleport(playerLastLocation);
						return true;
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.ERROR,
							"No previous location set."
						);
						return true;
					}
				}
				else if (teleportPresets.containsKey(argName)) {
					saveLastLocation(executingPlayer);
					executingPlayer.teleport(teleportPresets.get(argName));
					return true;
				}
				else {
					// match players
					List<Player> matches = this.pluginRef.getServer().matchPlayer(argName);
					if (matches.size() > 0) {
						if (matches.size() > 1) {
							String matchStr = "";
							for (Player player : matches) {
								matchStr = matchStr + player.getName() + " ";
							}
							GrubsMessager.sendMessage(
								executingPlayer, 
								GrubsMessager.MessageLevel.INFO,
								"Multiple matches: " + matchStr
							);
							return true;
						}
						else {
							Player target = matches.get(0);
							saveLastLocation(executingPlayer);
							executingPlayer.teleport(target.getLocation());
							return true;
						}
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.ERROR,
							"No presets or players matching '" + argName + "'."
						);
						return true;
					}
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
				return false;
			}
		}
		else if (cmdName.equalsIgnoreCase("fetch")) {
			if (args.length > 0) {
				List<Player> matches = this.pluginRef.getServer().matchPlayer(args[0]);
				if (matches.size() > 0) {
					if (matches.size() > 1) {
						String matchStr = "";
						for (Player player : matches) {
							matchStr = matchStr + player.getName() + " ";
						}
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							"Multiple matches: " + matchStr
						);
						return true;
					}
					else {
						Player target = matches.get(0);
						saveLastLocation(target);
						target.teleport(executingPlayer.getLocation());
						return true;
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + args[0] + "'."
					);
					return true;
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
				return false;
			}
		}
		else if (cmdName.equalsIgnoreCase("send")) {
			if (args.length == 2) {
				List<Player> targetMatches = this.pluginRef.getServer().matchPlayer(args[0]);
				if (targetMatches.size() > 0) {
					if (targetMatches.size() > 1) {
						String matchStr = "";
						for (Player player : targetMatches) {
							matchStr = matchStr + player.getName() + " ";
						}
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							"Multiple matches: " + matchStr
						);
						return true;
					}
					else {
						Player target = targetMatches.get(0);

						if (teleportPresets.containsKey(args[1])) {
							saveLastLocation(target);
							target.teleport(teleportPresets.get(args[1]));
							return true;
						}
						else {
							List<Player> destMatches = this.pluginRef.getServer().matchPlayer(args[1]);
							if (destMatches.size() > 0) {
								if (destMatches.size() > 1) {
									String matchStr = "";
									for (Player player : destMatches) {
										matchStr = matchStr + player.getName() + " ";
									}
									GrubsMessager.sendMessage(
										executingPlayer, 
										GrubsMessager.MessageLevel.INFO,
										"Multiple matches: " + matchStr
									);
									return true;
								}
								else {
									Player dest = destMatches.get(0);
									saveLastLocation(target);
									target.teleport(dest.getLocation());
									return true;
								}
							}
							else {
								GrubsMessager.sendMessage(
									executingPlayer, 
									GrubsMessager.MessageLevel.ERROR,
									"No players matching '" + args[1] + "'."
								);
								return true;
							}
						}
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + args[0] + "'."
					);
					return true;
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
				return false;
			}
		}
		else if (cmdName.equalsIgnoreCase("tpset")) {
			if (args.length > 0) {
				String argName = args[0];
				if (teleportPresets.containsKey(argName)) {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"Preset '" + argName + "' already exists."
					);
					return true;
				}
				else {
					teleportPresets.put(argName, executingPlayer.getLocation());
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INFO,
						"Preset '" + argName + "' saved."
					);
					saveTeleportPresets();
					return true;
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
				return false;
			}
		}
		else if (cmdName.equalsIgnoreCase("tpdel")) {
			if (args.length > 0) {
				String argName = args[0];
				if (teleportPresets.containsKey(argName)) {
					teleportPresets.remove(argName);
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INFO,
						"Preset '" + argName + "' deleted."
					);
					saveTeleportPresets();
					return true;
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"Preset '" + argName + "' not found."
					);
					return true;
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
				return false;
			}
		}
		else if (cmdName.equalsIgnoreCase("tplist")) {
			boolean useSeparator = false;
			String msgIdentifier = "[Teleport] ";
			String list = "";
			Set<String> keys = teleportPresets.keySet();

			if (keys.size() > 0) {
				for (String s : keys) {
					if (s.indexOf("_last") != -1) {
						continue;
					}
					
					if ( (msgIdentifier.length() + list.length() + 2 + s.length()) > 60) {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							msgIdentifier + list
						);
						list = "";
						useSeparator = false;
					}
					
					if (useSeparator) {
						list += ", ";
					}
					else {
						useSeparator = true;
					}
					
					
					list += s;	
				}

				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					msgIdentifier + list
				);
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					msgIdentifier + "No presets in list."
				);
			}

			return true;
		}
		
		return false;
	}
	
	private void loadTeleportPresets() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing can be loaded!");
			return;
		}
		
		try {
			FileInputStream in = new FileInputStream(dataFile);
			teleportProperties.load(in);
			in.close();
			
			for (Object key : teleportProperties.keySet()) {
				String realKey = (String) key;
				String rawValue = teleportProperties.getProperty(realKey);
				
				String[] parts = rawValue.split(",");
				Location realLoc = new Location(this.pluginRef.getServer().getWorld(parts[0]),
												Double.parseDouble(parts[1]),
												Double.parseDouble(parts[2]),
												Double.parseDouble(parts[3]),
												Float.parseFloat(parts[4]),
												Float.parseFloat(parts[5]));
								
				teleportPresets.put(realKey, realLoc);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}	
	}
	
	private void saveTeleportPresets() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing will be saved!");
			return;
		}
		
		double locParts[] = new double[3];
		float  viewParts[] = new float[2];
		String settingStr = "";
		
		// empty the teleport properties file
		teleportProperties.clear();
		
		for (String s : teleportPresets.keySet()) {
			Location curLoc = teleportPresets.get(s);
			locParts[0] = curLoc.getX();
			locParts[1] = curLoc.getY();
			locParts[2] = curLoc.getZ();
			viewParts[0] = curLoc.getYaw();
			viewParts[1] = curLoc.getPitch();
			
			settingStr = curLoc.getWorld().getName() +  "," +
						 Double.toString(locParts[0]) + "," +
						 Double.toString(locParts[1]) + "," +
						 Double.toString(locParts[2]) + "," +
						 Float.toString(viewParts[0]) + "," +
						 Float.toString(viewParts[1]);
			
			teleportProperties.put(s, settingStr);
		}
		
		this.log("Writing Teleport presets file.");
		try {
			FileOutputStream out = new FileOutputStream(dataFile);
			teleportProperties.store(out, "Do NOT edit this file manually!");
			out.flush();
			out.close();
		}
		catch (IOException ex) {
			this.log("Error writing Teleport presets file!");
			ex.printStackTrace();
		}
	}

	private String getLastLocationKey(Player player) {
		return player.getName() + "_last";
	}
	
	private void saveLastLocation(Player player) {
		teleportPresets.put(getLastLocationKey(player), player.getLocation());
	}
	
	private Location getLastLocation(Player player) {
		String key = getLastLocationKey(player);
		if (teleportPresets.containsKey(key)) {
			return teleportPresets.get(key);
		}
		else {
			return null;
		}
	}
}
