package com.selfequalsthis.grubsplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class GrubsMessager {
	
	public enum MessageLevel {
		PLAIN,
		MONITOR,
		INFO,
		INQUIRY,
		ERROR
	}
	
	private static ChatColor getColorForLevel(MessageLevel level) {
		ChatColor retColor;
		
		switch (level) {
		case PLAIN:
			retColor = ChatColor.WHITE;
			break;
		case MONITOR:
			retColor = ChatColor.LIGHT_PURPLE;
			break;
		case INFO:
			retColor = ChatColor.GREEN;
			break;
		case INQUIRY:
			retColor = ChatColor.GOLD;
			break;
		case ERROR:
			retColor = ChatColor.RED;
			break;
		default:
			retColor = ChatColor.WHITE;
		}
		
		return retColor;
	}
	
	public static void sendMessage(CommandSender target, MessageLevel level, String message) {
		target.sendMessage(getColorForLevel(level) + message);
	}
	
	public static void broadcast(MessageLevel level, String message) {
		Bukkit.broadcastMessage(getColorForLevel(level) + message);
	}
}
