package com.selfequalsthis.grubsplugin.modules.gamefixes;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GameFixesEventListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		World world = event.getPlayer().getWorld();
		Chunk destChunk = world.getChunkAt(event.getTo());
		world.refreshChunk(destChunk.getX(), destChunk.getZ());
	}
	
}
