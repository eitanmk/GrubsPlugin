package com.selfequalsthis.grubsplugin.module.regionannouncer;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.event.AbstractGrubsEventListeners;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionAnnouncerEventListeners extends AbstractGrubsEventListeners {

	private RegionAnnouncerModule moduleRef;
	private RegionService regionService = null;
	private GrubsPlayerRegionTracker playerTracker = null;

	public RegionAnnouncerEventListeners(RegionAnnouncerModule module) {
		this.moduleRef = module;
		Optional<RegionService> optService = this.moduleRef.getGame().getServiceManager().provide(RegionService.class);
		if (optService.isPresent()) {
			this.regionService = optService.get();
		}
		this.playerTracker = GrubsPlayerRegionTracker.getInstance();
	}

	private String getRegion(Location<World> loc) {
		return this.regionService.getRegion(loc, false);
	}

	private void announceEnter(Player player, String region) {
		player.sendMessage(Texts.of(TextColors.GREEN, "Entering region '" + region + "'"));
	}

	private void announceLeave(Player player, String region) {
		player.sendMessage(Texts.of(TextColors.GREEN, "Leaving region '" + region + "'"));
	}

	@Listener
	public void onPlayerJoin(ClientConnectionEvent.Join event) {
		if (this.regionService == null) {
			return;
		}

		Player player = event.getTargetEntity();
		String currentRegion = this.getRegion(player.getLocation());
		this.playerTracker.updatePlayerRegion(player, currentRegion);
	}

	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
		if (this.regionService == null) {
			return;
		}

		this.playerTracker.removePlayer(event.getTargetEntity());
	}

	@Listener
	public void onPlayerMove(DisplaceEntityEvent.Move event) {
		if (this.regionService == null) {
			return;
		}

		Location<World> fromLocation = event.getFromTransform().getLocation();
		Location<World> toLocation = event.getToTransform().getLocation();

		// if they are on the same block, no need to update region
		//  this case happens since this event is fired if a player just moves their head
		if (fromLocation.getBlockPosition().equals(toLocation.getBlockPosition())) {
			return;
		}

		Player player = (Player) event.getTargetEntity();
		String currentRegion = this.playerTracker.getPlayerRegion(player);
		String newLocationRegion = this.getRegion(toLocation);

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
				this.announceLeave(player, currentRegion);
				this.announceEnter(player, newLocationRegion);
				return;
			}
		}
		else {
			if (currentRegion == null) {
				// * -> A
				this.playerTracker.updatePlayerRegion(player, newLocationRegion);
				this.announceEnter(player, newLocationRegion);
				return;
			}
			else {
				// A -> *
				this.playerTracker.updatePlayerRegion(player, newLocationRegion);
				this.announceLeave(player, currentRegion);
				return;
			}

		}
	}
}
