package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class WeatherControlModule extends AbstractGrubsModule {
	
	private WeatherControlWeatherListener weatherListener;
	
	public WeatherControlModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[WeatherControlModule]: ";
		this.weatherListener = new WeatherControlWeatherListener();
	}
	
	@Override
	public void enable() {
		this.registerCommand("strike");
		this.registerCommand("zap");
		this.registerCommand("storm");
		this.registerCommand("thunder");	
		this.registerEvent(this.weatherListener);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
				
		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		if (cmdName.equalsIgnoreCase("strike")) {
			this.handleStrike(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("zap")) {
			this.handleZap(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("storm")) {
			this.handleStormToggle(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("thunder")) {
			this.handleThunderToggle(args, executingPlayer);
		}
				
		return true;
	}
	
	private void handleStrike(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			// there is a player as an arg
			// find the player's object
			List<Player> matches = Bukkit.matchPlayer(args[0]);
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
				}
				else {
					// unambiguous. get 'em!
					Player target = matches.get(0);
					Location playerLoc = target.getLocation();
					World targetWorld = target.getWorld();
					targetWorld.strikeLightningEffect(playerLoc);
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + args[0] + "'."
				);
			}
		}
		else {
			// aim for cursor
			Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
			World targetWorld = executingPlayer.getWorld();
			targetWorld.strikeLightningEffect(target);
		}
	}
	
	private void handleZap(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			// there is a player as an arg
			// find the player's object
			List<Player> matches = Bukkit.matchPlayer(args[0]);
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
				}
				else {
					// unambiguous. get 'em!
					Player target = matches.get(0);
					Location playerLoc = target.getLocation();
					World targetWorld = target.getWorld();
					targetWorld.strikeLightning(playerLoc);
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + args[0] + "'."
				);
			}
		}
		else {
			// aim for cursor
			Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
			World targetWorld = executingPlayer.getWorld();
			targetWorld.strikeLightning(target);
		}
	}
	
	private void handleStormToggle(String[] args, Player executingPlayer) {
		if (args.length == 0) {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Argument missing."
			);
		}
		
		if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Invalid argument."
			);
		}

		boolean onFlag = args[0].equalsIgnoreCase("on");
		World worldObj = executingPlayer.getWorld();
		worldObj.setStorm(onFlag);
	}
	
	private void handleThunderToggle(String[] args, Player executingPlayer) {
		if (args.length == 0) {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Argument missing."
			);
		}
		
		if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Invalid argument."
			);
		}

		boolean onFlag = args[0].equalsIgnoreCase("on");
		World worldObj = executingPlayer.getWorld();
		worldObj.setThundering(onFlag);
	}
}
