package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;
import java.util.ArrayList;

import org.bukkit.Location;

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
	
	public ArrayList<ChannelNode> getReceiversList() {
		return receivers;
	}
	
	public void addTransmitter(ChannelNode node) {
		transmitters.add(node);
	}
	
	public void addReceiver(ChannelNode node) {
		receivers.add(node);
	}
	
	public void removeTransmitterAt(Location loc) {
		for (ChannelNode node : transmitters) {
			if (node.isAtLocation(loc)) {
				transmitters.remove(node);
				return;
			}
		}
	}
	
	public void removeReceiverAt(Location loc) {
		for (ChannelNode node : receivers) {
			if (node.isAtLocation(loc)) {
				receivers.remove(node);
				return;
			}
		}
	}
	
	public boolean isEmptyChannel() {
		return (transmitters.size() == 0 && receivers.size() == 0);
	}
 }
