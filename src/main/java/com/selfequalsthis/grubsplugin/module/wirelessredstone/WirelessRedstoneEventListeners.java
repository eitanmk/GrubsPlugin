package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;

import com.selfequalsthis.grubsplugin.event.AbstractGrubsEventListeners;

public class WirelessRedstoneEventListeners extends AbstractGrubsEventListeners {

	private GrubsWirelessRedstone controllerRef;

	public WirelessRedstoneEventListeners(GrubsWirelessRedstone controller) {
		this.controllerRef = controller;
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				this.controllerRef.removeNode(signObj);
			}
		}
		else if (event.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
			this.controllerRef.removeReceiverOnAnyChannel(event.getBlock());
		}
	}

	@Listener
	public void onBlockRedstoneChange(ChangeBlockEvent.Modify event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				if (GrubsWirelessRedstone.isTransmitter(signObj.getLine(0))) {
					boolean poweredOn = (event.getBlock().isBlockPowered() || event.getBlock().isBlockIndirectlyPowered());
					this.controllerRef.powerChanged(signObj, poweredOn);
				}
			}
		}
	}

	@Listener
	public void onSignChange(ChangeBlockEvent.Modify event) {
		if (GrubsWirelessRedstone.isValidNode(event.getLines())) {
			this.controllerRef.addNode(event.getBlock(), event.getLines());
		}
	}

	@Listener
	public void onBlockPhysics(ChangeBlockEvent.Post event) {
		if ((event.getBlock().getState() instanceof Sign) || event.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
			this.controllerRef.checkChannelsForPhysicsUpdates(event.getBlock());
		}
	}

	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
		World world = event.getPlayer().getWorld();
		int numPlayers = world.getPlayers().size();

		// before this player logs out, so there is still 1 in world
		if (numPlayers == 1) {
			this.controllerRef.saveChannels();
		}
	}
}
