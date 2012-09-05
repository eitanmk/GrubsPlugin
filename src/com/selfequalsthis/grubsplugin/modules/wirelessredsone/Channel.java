package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

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
			updateReceivers(block.getWorld(), isTransmitting());
		}
	}
	
	public void addReceiver(Block block, boolean isInverted) {
		ChannelNode node = new ChannelNode(block);
		node.setIsInverted(isInverted);
		receivers.add(node);
		
		// might have to turn the new one on immediately
		if (!transmitters.isEmpty()) {
			if (isTransmitting()) {
				node.handleChannelStartTransmitting(block.getLocation().getWorld(), this.name);
			}
			else {
				node.handleChannelEndTransmitting(block.getLocation().getWorld(), this.name);
			}
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
			transmitters.remove(node);
			
			if (transmitters.isEmpty() && !receivers.isEmpty()) {
				allReceiversToSigns(loc.getWorld());
			}
		}
	}
	
	public void removeReceiverAt(Location loc) {
		ChannelNode node = getReceiverAt(loc);
		if (node != null) {
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
		for (ChannelNode receiver : receivers) {
			if (powerOn) {
				receiver.handleChannelStartTransmitting(world, this.name);
			}
			else {
				receiver.handleChannelEndTransmitting(world, this.name);
			}
		}
	}
	
	private void allReceiversToSigns(World world) {
		for (ChannelNode receiver : receivers) {
			receiver.toSign(world, this.name);
		}
	}
	
	public boolean handlePhysicsChange(Block block) {
		// does this location affect us?
		ChannelNode target = this.getTransmitterAt(block.getLocation());
		if (target == null) {
			target = this.getReceiverAt(block.getLocation());
		}
		
		if (target == null) {
			return false;
		}
		else {
			boolean removeNode = target.physicsWillCauseDestruction(block);
			if (removeNode) {
				this.removeTransmitterAt(block.getLocation());
				this.removeReceiverAt(block.getLocation());
			}
			
			return removeNode;
		}
	}
	
	public void cleanup(World world) {
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
