package com.selfequalsthis.grubsplugin.modules;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.IGrubsModule;

public class GameFixesModule implements CommandExecutor, IGrubsModule {

	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[GameFixesModule]: ";
	private JavaPlugin pluginRef;
	
	public GameFixesModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
	}
	
	@Override
	public void enable() {
		log.info(logPrefix + "Initializing command handlers.");
		this.pluginRef.getCommand("eject").setExecutor(this);
	}

	@Override
	public void disable() {	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("eject")) {
			if (executingPlayer.isInsideVehicle()) {
				executingPlayer.leaveVehicle();
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "You are not in a vehicle.");
			}
			
			return true;
		}
		
		return false;
	}

}
