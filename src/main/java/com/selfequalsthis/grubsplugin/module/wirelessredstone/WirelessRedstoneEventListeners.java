package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import java.util.List;

import org.slf4j.Logger;
import org.spongepowered.api.block.BlockSnapshot;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.Transaction;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.property.block.PoweredProperty;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.block.ChangeBlockEvent;
import org.spongepowered.api.event.block.NotifyNeighborBlockEvent;
import org.spongepowered.api.event.block.tileentity.ChangeSignEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
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
	public void onSignPlace(ChangeSignEvent event) {
		if (! event.getCause().containsType(Player.class)) {
			return;
		}

		if ( GrubsWirelessRedstone.isValidNode( event.getText().lines().get() ) ) {
			this.controllerRef.addNode(event.getTargetTile().getLocation(), event.getText().lines().get());
		}
	}

	@Listener
	public void onBlockBreak(ChangeBlockEvent.Break event) {
		for (Transaction<BlockSnapshot> transaction : event.getTransactions()) {
			BlockSnapshot original = transaction.getOriginal();
			BlockState block = original.getState();
			Location<World> location = original.getLocation().get();

			if (original.supports(Keys.SIGN_LINES)) {
				List<Text> lines = original.get(Keys.SIGN_LINES).get();
				if ( GrubsWirelessRedstone.isValidNode(lines) ) {
					this.controllerRef.removeNode(location, lines);
				}
			}
			else if (block.getType() == BlockTypes.REDSTONE_TORCH) {
				this.controllerRef.removeReceiverOnAnyChannel(location);
			}
		}
	}

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
					this.controllerRef.checkChannelsForPhysicsUpdates(original.getLocation().get());
				}
			});
	}

	@Listener
	public void onNeighborNotify(NotifyNeighborBlockEvent event) {
		if (! event.getCause().containsType(BlockSnapshot.class)) {
			return;
		}

		BlockSnapshot source = event.getCause().first(BlockSnapshot.class).get();
		if (source.supports(Keys.SIGN_LINES)) {
			Location<World> location = source.getLocation().get();
			List<Text> lines = source.get(Keys.SIGN_LINES).get();
			if ( GrubsWirelessRedstone.isValidNode( source.get(Keys.SIGN_LINES).get() ) ) {
				PoweredProperty powered = location.getProperty(PoweredProperty.class).orElse(null);
				if (powered != null) {
					this.controllerRef.powerChanged(location, lines, powered.getValue());
				}
			}
		}
	}

	@Listener
	public void onPlayerQuit(ClientConnectionEvent.Disconnect event) {
		World world = event.getTargetEntity().getWorld();
		int numPlayers = world.getEntities(entity -> entity instanceof Player).size();

		// before this player logs out, so there is still 1 in world
		if (numPlayers == 1) {
			this.controllerRef.saveChannels();
		}
	}

}
