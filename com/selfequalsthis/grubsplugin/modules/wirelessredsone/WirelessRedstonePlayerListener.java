package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.util.logging.Logger;

import org.bukkit.World;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

public class WirelessRedstonePlayerListener extends PlayerListener {
	protected final Logger log = Logger.getLogger("Minecraft");

	private GrubsWirelessRedstone controllerRef;
	
	public WirelessRedstonePlayerListener(GrubsWirelessRedstone gwr) {
		this.controllerRef = gwr;
	}
	
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		World world = event.getPlayer().getWorld();
		int numPlayers = world.getPlayers().size();
		
		// before this player logs out, so there is still 1 in world
		if (numPlayers == 1) {
			this.controllerRef.saveChannels();
		}
	}
}
