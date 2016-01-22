package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.block.tileentity.TileEntity;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.manipulator.mutable.tileentity.SignData;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.event.AbstractGrubsEventListeners;

public class WirelessRedstoneEventListeners extends AbstractGrubsEventListeners {

	private GrubsWirelessRedstone controllerRef;
	private Logger logger;

	public WirelessRedstoneEventListeners(GrubsWirelessRedstone controller, Logger logger) {
		this.controllerRef = controller;
		this.logger = logger;
	}
	
	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break event) {
		if (event.getTransactions().isEmpty()) {
			return;
		}
		
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockState block = transaction.getOriginal().getState();
			this.logger.error(block.toString());
			
			if (block.getType().equals(BlockTypes.STANDING_SIGN) || block.getType().equals(BlockTypes.WALL_SIGN)) {
				this.logger.error(transaction.getOriginal().getLocation().get().getTileEntity().toString());
			}
		}
		/*
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				this.controllerRef.removeNode(signObj);
			}
		}
		else if (event.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
			this.controllerRef.removeReceiverOnAnyChannel(event.getBlock());
		}
		*/
	}
/*
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
*/
}
