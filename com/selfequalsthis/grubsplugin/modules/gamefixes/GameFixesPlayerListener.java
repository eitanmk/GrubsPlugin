package com.selfequalsthis.grubsplugin.modules.gamefixes;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerTeleportEvent;

public class GameFixesPlayerListener extends PlayerListener {

	public void onPlayerTeleport(PlayerTeleportEvent event) {
		World world = event.getPlayer().getWorld();
		Chunk destChunk = world.getChunkAt(event.getTo());
		world.refreshChunk(destChunk.getX(), destChunk.getZ());
	}
	
}
