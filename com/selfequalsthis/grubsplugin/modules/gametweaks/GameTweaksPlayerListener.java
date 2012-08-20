package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.selfequalsthis.grubsplugin.GrubsMessager;

public class GameTweaksPlayerListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		World w = p.getWorld();
		GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.PLAIN, "Welcome, " + p.getDisplayName() + "!");
		GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.INFO, "Current game time is: " + w.getTime());
		
		if (w.getPlayers().size() > 0) {
			String playerListStr = "";
			boolean useSeparator = false;
			
			for (Player player : w.getPlayers()) {
				if (useSeparator) {
					playerListStr += ", ";
				}
				else {
					useSeparator = true;
				}
				playerListStr += player.getDisplayName();
			}
			
			GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.INQUIRY, "Currently playing: " + playerListStr);
		}
		else {
			GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.INQUIRY, "No other players currently here.");
		}
	}
	
}
