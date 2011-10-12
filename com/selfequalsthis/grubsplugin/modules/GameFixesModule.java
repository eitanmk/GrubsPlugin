package com.selfequalsthis.grubsplugin.modules;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class GameFixesModule extends AbstractGrubsModule {
	
	public GameFixesModule() {
		this.logPrefix = "[GameFixesModule]: ";
		
		this.commandNames.add("eject");
	}
	
	@Override
	public void enable(JavaPlugin plugin) {
		super.enable(plugin);
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
				executingPlayer.sendMessage(ChatColor.RED + "You are not in a vehicle.");
			}
			
			return true;
		}
		
		return false;
	}

}
