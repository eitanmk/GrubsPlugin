package com.selfequalsthis.grubsplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.GrubsCommandHandler;

public class GrubsEjectCommand implements GrubsCommandHandler {
	
	private static GrubsEjectCommand instance = null;
	private GrubsEjectCommand() { }
	public static GrubsCommandHandler getInstance() {
		if (instance == null) {
			instance = new GrubsEjectCommand();
		}
		
		return instance;
	}
	
	@Override
	public boolean processCommand(Server server, Player executingPlayer, String cmdName, String[] args) {

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
