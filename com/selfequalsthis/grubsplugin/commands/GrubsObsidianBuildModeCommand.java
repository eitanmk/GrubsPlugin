package com.selfequalsthis.grubsplugin.commands;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.GrubsCommandHandler;

public class GrubsObsidianBuildModeCommand implements GrubsCommandHandler {
	
	public static boolean obsidianBuildModeEnabled = false;
	
	private static GrubsObsidianBuildModeCommand instance = null;
	private GrubsObsidianBuildModeCommand() { }
	public static GrubsCommandHandler getInstance() {
		if (instance == null) {
			instance = new GrubsObsidianBuildModeCommand();
		}
		
		return instance;
	}

	@Override
	public boolean processCommand(Server server, Player executingPlayer, String cmdName, String[] args) {
		if (cmdName.equalsIgnoreCase("obm")) {
			if (args.length > 0) {
				if ( !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
					executingPlayer.sendMessage(ChatColor.RED + "[Obsidian] Invalid argument.");
					return false;
				}
				else {
					obsidianBuildModeEnabled = args[0].equalsIgnoreCase("on");
					executingPlayer.sendMessage(ChatColor.GREEN + "[Obsidian] Build mode is " + args[0] + ".");
					return true;
				}
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "[Obsidian] Missing command argument.");
				return false;
			}
		}
		
		return false;
	}

}
