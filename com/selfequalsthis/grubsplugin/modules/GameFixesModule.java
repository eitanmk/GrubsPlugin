package com.selfequalsthis.grubsplugin.modules;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;

public class GameFixesModule extends AbstractGrubsModule {
	
	public GameFixesModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameFixesModule]: ";
	}
	
	@Override
	public void enable() {		
		this.registerCommand("eject");
	}

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
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "You are not in a vehicle.");
			}
			
			return true;
		}
		
		return false;
	}

}
