package com.selfequalsthis.grubsplugin.modules.permissions;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class PermissionsEventListeners implements Listener {

	private PermissionsModule permissionsModuleRef;
	
	public PermissionsEventListeners(PermissionsModule module) {
		this.permissionsModuleRef = module;
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player p = event.getPlayer();
		this.permissionsModuleRef.setupPlayerPermissions(p);
	}
}
