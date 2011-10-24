package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

public class Channel implements Serializable {

	private static final long serialVersionUID = -1413121016289041397L;

	protected static final Logger log = Logger.getLogger("Minecraft");
	
	private String name;
	private ArrayList<ChannelNode> transmitters = new ArrayList<ChannelNode>();
	private ArrayList<ChannelNode> receivers = new ArrayList<ChannelNode>();
	
	public Channel(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public void addTransmitter(Block block) {
		ChannelNode node = new ChannelNode(block);
		transmitters.add(node);

		if (transmitters.size() == 1 && !receivers.isEmpty()) {
			// if we just added the first transmitter and there were receivers, 
			//  put them back into their default state
			log.info("First transmitters added to channel. Initializing receivers to default states.");
			updateReceivers(block.getWorld(), isTransmitting());
		}
	}
	
	public void addReceiver(Block block, boolean isInverted) {
		ChannelNode node = new ChannelNode(block);
		node.setIsInverted(isInverted);
		receivers.add(node);
		
		// might have to turn the new one on immediately
		try {
			if (!transmitters.isEmpty()) {
				if (isTransmitting()) {
					node.handleChannelStartTransmitting(block.getLocation().getWorld(), this.name);
				}
				else {
					node.handleChannelEndTransmitting(block.getLocation().getWorld(), this.name);
				}
			}
		} catch (ReceiverDestroyedException e) {
			log.info("Receiver missing." + block.getLocation().getBlock().getType());
			this.removeReceiverAt(block.getLocation());
		}
	}
	
	public ChannelNode getTransmitterAt(Location loc) {
		for (ChannelNode node : transmitters) {
			if (node.isAtLocation(loc)) {
				return node;
			}
		}
		
		return null;
	}
	
	public ChannelNode getReceiverAt(Location loc) {
		for (ChannelNode node : receivers) {
			if (node.isAtLocation(loc)) {
				return node;
			}
		}
		
		return null;
	}
	
	public void removeTransmitterAt(Location loc) {
		ChannelNode node = getTransmitterAt(loc);
		if (node != null) {
			log.info("Removing transmitter.");
			transmitters.remove(node);
			
			if (transmitters.isEmpty() && !receivers.isEmpty()) {
				log.info("No more transmitters on channel. Converting all receivers to signs.");
				allReceiversToSigns(loc.getWorld());
			}
		}
	}
	
	public void removeReceiverAt(Location loc) {
		ChannelNode node = getReceiverAt(loc);
		if (node != null) {
			log.info("Removing receiver.");
			receivers.remove(node);
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
	
	public void handlePowerChangedOn(Block block) {		
		// only replace signs if we aren't already transmitting
		if (!isTransmitting()) {			
			updateReceivers(block.getWorld(), true);
		}
		
		// update the transmitter power state
		ChannelNode node = getTransmitterAt(block.getLocation());
		if (node != null) {
			node.setIsPowered(true);
		}		
	}
	
	public void handlePowerChangedOff(Block block) {
		// update the transmitter power state
		ChannelNode node = getTransmitterAt(block.getLocation());
		if (node != null) {
			node.setIsPowered(false);
		}
		
		// if that was the last transmitter off, replace the torches
		if (!isTransmitting()) {
			updateReceivers(block.getWorld(), false);
		}
	}
	
	private void updateReceivers(World world, boolean powerOn) {
		ArrayList<Location> removeLocations = new ArrayList<Location>();
		for (ChannelNode receiver : receivers) {
			try {
				if (powerOn) {
					receiver.handleChannelStartTransmitting(world, this.name);
				}
				else {
					receiver.handleChannelEndTransmitting(world, this.name);
				}
			} catch (ReceiverDestroyedException e) {
				Location loc = new Location(world, receiver.getX(), receiver.getY(), receiver.getZ());
				removeLocations.add(loc);
			}
		}
		
		if (removeLocations.size() > 0) {
			for (Location loc : removeLocations) {
				log.info("Missing receiver at "  + loc.getX() + "," + loc.getY() + "," + loc.getZ());
				this.removeReceiverAt(loc);
			}
		}
	}
	
	private void allReceiversToSigns(World world) {
		ArrayList<Location> removeLocations = new ArrayList<Location>();
		for (ChannelNode receiver : receivers) {
			try {
				receiver.toSign(world, this.name);
			} catch (ReceiverDestroyedException e) {
				Location loc = new Location(world, receiver.getX(), receiver.getY(), receiver.getZ());
				removeLocations.add(loc);
			}
		}
		
		if (removeLocations.size() > 0) {
			for (Location loc : removeLocations) {
				log.info("Missing receiver at "  + loc.getX() + "," + loc.getY() + "," + loc.getZ());
				this.removeReceiverAt(loc);
			}
		}
	}
 }
