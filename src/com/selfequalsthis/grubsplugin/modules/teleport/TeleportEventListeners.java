package com.selfequalsthis.grubsplugin.modules.teleport;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class TeleportEventListeners implements Listener {

	private TeleportModule tpModule;

	public TeleportEventListeners(TeleportModule module) {
		this.tpModule = module;
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player deadPlayer = event.getEntity();
		this.tpModule.savePlayerSpecialLocation(deadPlayer, "grave");
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player quittingPlayer = event.getPlayer();
		this.tpModule.savePlayerSpecialLocation(quittingPlayer, "quit");
	}
}
