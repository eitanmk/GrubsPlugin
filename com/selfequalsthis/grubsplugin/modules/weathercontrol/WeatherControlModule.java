package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

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
		this.registerEvent(Event.Type.WEATHER_CHANGE, this.weatherListener, Priority.Monitor);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("strike") || cmdName.equalsIgnoreCase("zap")) {
			if (args.length > 0) {
				// there is a player as an arg
				// find the player's object
				List<Player> matches = this.pluginRef.getServer().matchPlayer(args[0]);
				if (matches.size() > 0) {
					if (matches.size() > 1) {
						String matchStr = "";
						for (Player player : matches) {
							matchStr = matchStr + player.getName() + " ";
						}
						executingPlayer.sendMessage(ChatColor.RED + "[Weather] Multiple matches: " + matchStr);
						return false;
					}
					else {
						// unambiguous. get 'em!
						Player target = matches.get(0);
						Location playerLoc = target.getLocation();
						World targetWorld = target.getWorld();
						if (cmdName.equalsIgnoreCase("strike")) {
							targetWorld.strikeLightningEffect(playerLoc);
							return true;
						}
						else if (cmdName.equalsIgnoreCase("zap")) {
							targetWorld.strikeLightning(playerLoc);
							return true;
						}
					}
				}
				else {
					executingPlayer.sendMessage(ChatColor.RED + "[Weather] No players matching '" + args[0] + "'.");
					return false;
				}
			}
			else {
				// aim for cursor
				Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
				World targetWorld = executingPlayer.getWorld();
				if (cmdName.equalsIgnoreCase("strike")) {
					targetWorld.strikeLightningEffect(target);
					return true;
				}
				else if (cmdName.equalsIgnoreCase("zap")) {
					targetWorld.strikeLightning(target);
					return true;
				}
			}
			
		}
		else if (cmdName.equalsIgnoreCase("storm") || cmdName.equalsIgnoreCase("thunder")) {
			if (args.length == 0) {
				executingPlayer.sendMessage(ChatColor.RED + "[Weather] Argument missing.");
				return false;
			}
			
			if ( !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
				executingPlayer.sendMessage(ChatColor.RED + "[Weather] Invalid argument.");
				return false;
			}

			boolean onFlag = args[0].equalsIgnoreCase("on");
			World worldObj = executingPlayer.getWorld();
			if (cmdName.equalsIgnoreCase("storm")) {
				worldObj.setStorm(onFlag);
				return true;
			}
			else if (cmdName.equalsIgnoreCase("thunder")) {
				worldObj.setThundering(onFlag);
				return true;
			}
		}
		
		return false;
	}
	
}
