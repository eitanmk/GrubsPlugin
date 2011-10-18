package com.selfequalsthis.grubsplugin.modules;

import java.util.HashMap;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;

public class GameInfoModule extends AbstractGrubsModule {

	public GameInfoModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameInfoModule]: ";
	}
	
	@Override
	public void enable() {
		this.registerCommand("dataval");
		this.registerCommand("dataname");
		this.registerCommand("gettime");
		this.registerCommand("getcoords");
		this.registerCommand("sendcoords");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("dataval")) {
			if (args.length > 0) {
				// lookup the text typed
				HashMap<String,Integer> matches = this.matchMaterialName(args[0]);
				if (matches.size() > 0) {
					for (String key : matches.keySet()) {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							key + " = " + matches.get(key)
						);
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No matches for '" + args[0] + "'."
					);
				}
			}
			else {
				Material target = executingPlayer.getTargetBlock(null, 256).getType();
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					target.toString().toLowerCase() + " = " + target.getId()
				);
			}
			return true;
		}
		
		if (cmdName.equalsIgnoreCase("dataname")) {
			if (args.length > 0) {
				try {
					int val = Integer.parseInt(args[0]);
					String res = matchMaterialId(val);
					if (res != "") {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							"" + val + " = " + res
						);
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.ERROR,
							"No material for id: " + val + "."
						);
					}
				}
				catch (NumberFormatException nex) { }
			}
			else {
				Material target = executingPlayer.getTargetBlock(null, 256).getType();
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					"" + target.getId() + " = " + target.toString().toLowerCase()
				);
			}
			return true;
		}


		if (cmdName.equalsIgnoreCase("gettime")) {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO,
				"Current time is: " + executingPlayer.getWorld().getTime()
			);
			return true;
		}


		if (cmdName.equalsIgnoreCase("getcoords")) {
			if (args.length > 0) {
				String argName = args[0];
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
						Location loc = target.getLocation();
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							target.getName() + ": " + this.getCoordsStrFromLocation(loc)
						);
						return true;
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + argName + "'."
					);
					return true;
				}
			}
			else {
				Location loc = executingPlayer.getLocation();
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					executingPlayer.getName() + ": " + this.getCoordsStrFromLocation(loc)
				);
			}
			 
			return true;
		}
		
		if (cmdName.equalsIgnoreCase("sendcoords")) {
			if (args.length > 0) {
				String argName = args[0];
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
						Location loc = executingPlayer.getLocation();
						GrubsMessager.sendMessage(
							target, 
							GrubsMessager.MessageLevel.INFO,
							executingPlayer.getName() + ": " + this.getCoordsStrFromLocation(loc)
						);
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.INFO,
							"Coordinates sent to " + target.getName()
						);
						return true;
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + argName + "'."
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
			}
			 
			return true;
		}

		return false;
	}
	
	private HashMap<String,Integer> matchMaterialName(String name) {
		HashMap<String,Integer> results = new HashMap<String,Integer>();
		
		Material[] materialNames = Material.values();
		for (Material m : materialNames) {
			if (m.toString().indexOf(name.toUpperCase()) != -1) {
				results.put(m.toString().toLowerCase(), m.getId());
			}
		}
		
		return results;
	}
	
	private String matchMaterialId(int id) {
		Material material = Material.getMaterial(id);
		return material.toString().toLowerCase();
	}
	
	private String getCoordsStrFromLocation(Location loc) {
		return "x: " + (int)loc.getX() + ", z: " + (int)loc.getZ() + " Altitude: " + (int)(loc.getY() + 1);
	}

}
