package com.selfequalsthis.grubsplugin.modules.regions;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import com.selfequalsthis.grubsplugin.service.RegionService;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class RegionsEventListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		RegionService regionService = Bukkit.getServer().getServicesManager().load(RegionService.class);
		if (regionService == null) {
			return;
		}

		if (event.getFrom().getBlock().equals(event.getTo().getBlock())) {
			return;
		}

		Player player = event.getPlayer();
		String toRegionName = regionService.getRegion(event.getTo());
		if (toRegionName != null) {
			GrubsMessager.sendMessage(player, GrubsMessager.MessageLevel.INFO, "Entering region '" + toRegionName + "'");
		}
	}

}
