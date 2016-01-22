package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.event.AbstractGrubsEventListeners;

public class WirelessRedstoneEventListeners extends AbstractGrubsEventListeners {

	//private GrubsWirelessRedstone controllerRef;
	private Logger logger;

	public WirelessRedstoneEventListeners(GrubsWirelessRedstone controller, Logger logger) {
		//this.controllerRef = controller;
		this.logger = logger;
	}
	
	@Listener
	public void onBlockChange(ChangeBlockEvent.Break event) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot original = transaction.getOriginal();
			BlockState block = original.getState();

			if (original.supports(Keys.SIGN_LINES)) {
				if ( GrubsWirelessRedstone.isValidNode( transaction.getOriginal().get(Keys.SIGN_LINES).get() ) ) {
					this.logger.error("TODO: remove node from channel");
					//this.controllerRef.removeNode(signObj);
				}
			}
			else if (block.getType() == BlockTypes.REDSTONE_TORCH) {
				this.logger.error("TODO: remove receiver on any channel by location");
				//this.controllerRef.removeReceiverOnAnyChannel(event.getBlock());
			}
		}
	}

	@Listener
	public void onBlockRedstoneChange(ChangeBlockEvent event) {
		/*
		if (! event.getCause().containsType(Player.class)) {
			return;
		}
		event.getTransactions().stream()
			.forEach(transaction -> {
				this.logger.error(transaction.getOriginal().getState().toString());
				this.logger.error(transaction.getOriginal().getApplicableProperties().toString());
				this.logger.error(transaction.getOriginal().getManipulators().toString());
			});
		/*
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				if (GrubsWirelessRedstone.isTransmitter(signObj.getLine(0))) {
					boolean poweredOn = (event.getBlock().isBlockPowered() || event.getBlock().isBlockIndirectlyPowered());
					this.controllerRef.powerChanged(signObj, poweredOn);
				}
			}
		}
		*/
	}
/*
	@Listener
	public void onSignChange(ChangeBlockEvent.Modify event) {
		if (GrubsWirelessRedstone.isValidNode(event.getLines())) {
			this.controllerRef.addNode(event.getBlock(), event.getLines());
		}
	}
*/
	@Listener
	public void onBlockPhysics(ChangeBlockEvent.Place event) {
		event.getTransactions().stream()
			.filter(transaction -> {
				BlockSnapshot original = transaction.getOriginal();
				return original.supports(Keys.SIGN_LINES) ||
						original.getState().getType() == BlockTypes.REDSTONE_TORCH;
			})
			.forEach(transaction -> {
				BlockSnapshot original = transaction.getOriginal();
				BlockSnapshot outcome = transaction.getFinal();
				if (original.getState().getType() != outcome.getState().getType()) {
					this.logger.error("TODO: check channels for physics updates");
					//this.controllerRef.checkChannelsForPhysicsUpdates(event.getBlock());
				}
			});
	}

	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
		World world = event.getTargetEntity().getWorld();
		int numPlayers = world.getEntities(entity -> entity instanceof Player).size();

		// before this player logs out, so there is still 1 in world
		if (numPlayers == 1) {
			this.logger.error("TODO: save WR channels when last player logs out");
			//this.controllerRef.saveChannels();
		}
	}

}
