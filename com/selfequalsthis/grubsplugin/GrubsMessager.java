package com.selfequalsthis.grubsplugin;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;

public class GrubsMessager {

	public enum MessageLevel {
		MONITOR,
		INFO,
		INQUIRY,
		ERROR
	}
	
	private static ChatColor getColorForLevel(MessageLevel level) {
		ChatColor retColor;
		
		switch (level) {
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
	
	public static void sendMessage(Player player, MessageLevel level, String message) {
		player.sendMessage(getColorForLevel(level) + message);
	}
	
	public static void sendMessage(Server server, MessageLevel level, String message) {
		server.broadcastMessage(getColorForLevel(level) + message);
	}
}
