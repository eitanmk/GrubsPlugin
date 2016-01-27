package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class Channel implements Serializable {

	private static final long serialVersionUID = -1413121016289041397L;

	private String name;
	private ArrayList<ChannelNode> transmitters = new ArrayList<ChannelNode>();
	private ArrayList<ChannelNode> receivers = new ArrayList<ChannelNode>();
	
	private Logger log = Sponge.getPluginManager().getPlugin("grubsplugin").get().getLogger();

	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addTransmitter(Location<World> location) {
		ChannelNode node = new ChannelNode(location);
		transmitters.add(node);

		if (transmitters.size() == 1 && !receivers.isEmpty()) {
			// if we just added the first transmitter and there were receivers,
			//  put them back into their default state
			updateReceivers(location.getExtent(), isTransmitting());
		}
	}

	public void addReceiver(Location<World> location, boolean isInverted) {
		ChannelNode node = new ChannelNode(location);
		node.setIsInverted(isInverted);
		receivers.add(node);

		// might have to turn the new one on immediately
		if (!transmitters.isEmpty()) {
			if (isTransmitting()) {
				node.handleChannelStartTransmitting(location.getExtent(), this.name);
			}
			else {
				node.handleChannelEndTransmitting(location.getExtent(), this.name);
			}
		}
	}

	public Optional<ChannelNode> getTransmitterAt(Location<World> location) {
		Optional<ChannelNode> optional = Optional.empty();
		for (ChannelNode node : transmitters) {
			if (node.isAtLocation(location)) {
				optional = Optional.of(node);
			}
		}

		return optional;
	}

	public Optional<ChannelNode> getReceiverAt(Location<World> location) {
		Optional<ChannelNode> optional = Optional.empty();
		for (ChannelNode node : receivers) {
			if (node.isAtLocation(location)) {
				optional = Optional.of(node);
			}
		}
		return optional;
	}

	public void removeTransmitterAt(Location<World> location) {
		Optional<ChannelNode> node = getTransmitterAt(location);
		if (node.isPresent()) {
			transmitters.remove(node.get());

			if (transmitters.isEmpty() && !receivers.isEmpty()) {
				allReceiversToSigns(location.getExtent());
			}
		}
	}

	public void removeReceiverAt(Location<World> location) {
		Optional<ChannelNode> node = getReceiverAt(location);
		if (node.isPresent()) {
			receivers.remove(node.get());
		}
	}

	public boolean isTransmitting() {
		boolean transmitting = false;
		for (ChannelNode node : transmitters) {
			transmitting = transmitting || node.isPowered();
		}
		return transmitting;
	}

	public boolean isEmpty() {
		return (transmitters.size() == 0 && receivers.size() == 0);
	}

	public void handlePowerChangedOn(Location<World> location) {
		// only replace signs if we aren't already transmitting
		if (!isTransmitting()) {
			updateReceivers(location.getExtent(), true);
		}

		// update the transmitter power state
		Optional<ChannelNode> node = getTransmitterAt(location);
		if (node.isPresent()) {
			node.get().setIsPowered(true);
		}
	}

	public void handlePowerChangedOff(Location<World> location) {
		// update the transmitter power state
		Optional<ChannelNode> node = getTransmitterAt(location);
		if (node.isPresent()) {
			node.get().setIsPowered(false);
		}

		// if that was the last transmitter off, replace the torches
		if (!isTransmitting()) {
			updateReceivers(location.getExtent(), false);
		}
	}

	private void updateReceivers(World world, boolean powerOn) {
		for (ChannelNode receiver : receivers) {
			if (powerOn) {
				receiver.handleChannelStartTransmitting(world, this.name);
			}
			else {
				log.error("handle channel end transmitting");
				receiver.handleChannelEndTransmitting(world, this.name);
			}
		}
	}

	private void allReceiversToSigns(World world) {
		for (ChannelNode receiver : receivers) {
			receiver.toSign(world, this.name);
		}
	}

	public boolean handlePhysicsChange(Location<World> location) {
		log.error("physics change " + location.toString());
		log.error(this.toString());
		// does this location affect us?
		Optional<ChannelNode> target = getTransmitterAt(location);
		if (! target.isPresent()) {
			target = getReceiverAt(location);
		}

		if (! target.isPresent()) {
			return false;
		}

		log.error("" + location.getBlock().supports(Keys.SIGN_LINES));
		if ( ! (location.getBlock().supports(Keys.SIGN_LINES) || location.getBlock().getType() == BlockTypes.REDSTONE_TORCH) ) {
			log.error(location.getBlock().toString());
			this.removeTransmitterAt(location);
			this.removeReceiverAt(location);
			log.error(this.toString());
			return true;
		}

		return false;
	}

	public void cleanup(World world) {
		/*
		ArrayList<Location> locations = new ArrayList<Location>();

		// for transmitters, ensure there is a sign at that location
		Iterator<ChannelNode> transmitterIterator = transmitters.iterator();
		while (transmitterIterator.hasNext()) {
			ChannelNode transmitter = transmitterIterator.next();
			Location loc = transmitter.getLocation();
			if (locations.contains(loc)) {
				log.info("Transmitter at previously inspected location. Deleting.");
				log.info(transmitter.toString());
				transmitterIterator.remove();
			}
			else {
				locations.add(loc);
				Block block = world.getBlockAt(loc);
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign)block.getState();
					if (!GrubsWirelessRedstone.isTransmitter(sign.getLine(0)) ) {
						log.info("Found a sign. Isn't a transmitter. Deleting.");
						log.info(transmitter.toString());
						transmitterIterator.remove();
					}
					else if (!sign.getLine(1).equals(this.name)) {
						log.info("Found a transmitter. Isn't on this channel. Deleting.");
						log.info(transmitter.toString());
						transmitterIterator.remove();
					}
				}
				else {
					log.info("No transmitter sign found for this location. Deleting.");
					log.info(transmitter.toString());
					transmitterIterator.remove();
				}
			}
		}

		// for receivers
		Iterator<ChannelNode> receiverIterator = receivers.iterator();
		while (receiverIterator.hasNext()) {
			ChannelNode receiver = receiverIterator.next();
			Location loc = receiver.getLocation();
			if (locations.contains(loc)) {
				log.info("Receiver at previously inspected location. Deleting.");
				log.info(receiver.toString());
				receiverIterator.remove();
			}
			else {
				locations.add(loc);
				Block block = world.getBlockAt(loc);
				if (block.getState() instanceof Sign) {
					Sign sign = (Sign)block.getState();
					if (!GrubsWirelessRedstone.isReceiver(sign.getLine(0)) ) {
						log.info("Found a sign. Isn't a receiver. Deleting.");
						log.info(receiver.toString());
						receiverIterator.remove();
					}
					else if (!sign.getLine(1).equals(this.name)) {
						log.info("Found a receiver. Isn't on this channel. Deleting.");
						log.info(receiver.toString());
						receiverIterator.remove();
					}
				}
				else if (block.getType() == Material.REDSTONE_TORCH_ON) {
					if (!this.isTransmitting() && !receiver.isInverted()) {
						log.info("Location doesn't contain a receiver item. Deleting.");
						log.info(receiver.toString());
						receiverIterator.remove();
					}
				}
				else {
					log.info("No receiver sign found for this location. Deleting.");
					log.info(receiver.toString());
					receiverIterator.remove();
				}
			}
		}
		*/
	}

	public String toString() {
		String retVal = "";

		retVal += "Channel name: " + this.name + "\n";
		retVal += "# Transmitters: " + transmitters.size() + "\n";
		retVal += "# Receivers: " + receivers.size() + "\n";
		retVal += "Transmitters:\n";
		for (ChannelNode transmitter : transmitters) {
			retVal += transmitter.toString();
		}
		retVal += "Receivers:\n";
		for (ChannelNode receiver : receivers) {
			retVal += receiver.toString();
		}
		return retVal;
	}

}

