package com.selfequalsthis.grubsplugin.modules.locks;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class LocksEventListeners implements Listener {
	LocksModule module;

	public LocksEventListeners(LocksModule locksModule) {
		module = locksModule;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.hasBlock()) {
			Block block = event.getClickedBlock();
			if (block.getType() == Material.CHEST) {
				module.showPinScreen(event.getPlayer());
				event.setCancelled(true);
			}
		}
	}
}
