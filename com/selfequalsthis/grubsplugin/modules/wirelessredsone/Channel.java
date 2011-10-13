package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;

public class Channel implements Serializable {

	private static final long serialVersionUID = -1413121016289041397L;

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
	}
	
	public void addReceiver(Block block) {
		ChannelNode node = new ChannelNode(block);
		receivers.add(node);
		
		if (isTransmitting()) {
			// have to turn the new one on immediately
			node.toTorch(block.getWorld());
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
			for (ChannelNode receiver : receivers) {
				receiver.toTorch(block.getLocation().getWorld());
			}
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
			for (ChannelNode receiver : receivers) {
				receiver.toSign(block.getLocation().getWorld(), this.name);
			}
		}
	}
 }
