package com.selfequalsthis.grubsplugin.modules.regionannouncer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.selfequalsthis.grubsplugin.services.RegionService;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class RegionAnnouncerEventListeners implements Listener {

	private RegionService regionService = null;
	private GrubsPlayerRegionTracker playerTracker = null;

	public RegionAnnouncerEventListeners() {
		this.regionService = Bukkit.getServer().getServicesManager().load(RegionService.class);
		this.playerTracker = GrubsPlayerRegionTracker.getInstance();
	}


	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (this.regionService == null) {
			return;
		}

		Player player = event.getPlayer();
		String currentRegion = this.regionService.getRegion(player.getLocation());
		this.playerTracker.updatePlayerRegion(player, currentRegion);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerQuit(PlayerQuitEvent event) {
		if (this.regionService == null) {
			return;
		}

		this.playerTracker.removePlayer(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		if (this.regionService == null) {
			return;
		}

		Location fromLocation = event.getFrom();
		Location toLocation = event.getTo();

		// if they are on the same block, no need to update region
		//  this case happens since this event is fired if a player just moves their head
		if (fromLocation.getBlock().equals(toLocation.getBlock())) {
			return;
		}

		Player player = event.getPlayer();
		String currentRegion = this.playerTracker.getPlayerRegion(player);
		String newLocationRegion = this.regionService.getRegion(toLocation);

		// * -> * (not on a defined a region and didn't move into one)
		if (currentRegion == null && newLocationRegion == null) {
			return;
		}

		if (currentRegion != null && newLocationRegion != null) {
			if (currentRegion.equalsIgnoreCase(newLocationRegion)) {
				// A -> A (didn't leave the region, exit now)
				return;
			}
			else {
				// A -> B (went from one region to another)
				this.playerTracker.updatePlayerRegion(player, newLocationRegion);
				GrubsMessager.sendMessage(player, GrubsMessager.MessageLevel.INFO, "Leaving region '" + currentRegion + "'");
				GrubsMessager.sendMessage(player, GrubsMessager.MessageLevel.INFO, "Entering region '" + newLocationRegion + "'");
				return;
			}
		}
		else {
			if (currentRegion == null) {
				// * -> A
				this.playerTracker.updatePlayerRegion(player, newLocationRegion);
				GrubsMessager.sendMessage(player, GrubsMessager.MessageLevel.INFO, "Entering region '" + newLocationRegion + "'");
				return;
			}
			else {
				// A -> *
				this.playerTracker.updatePlayerRegion(player, newLocationRegion);
				GrubsMessager.sendMessage(player, GrubsMessager.MessageLevel.INFO, "Leaving region '" + currentRegion + "'");
				return;
			}

		}
	}
}
