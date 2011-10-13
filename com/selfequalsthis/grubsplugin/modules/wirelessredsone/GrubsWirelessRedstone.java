package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.java.JavaPlugin;

public class GrubsWirelessRedstone {
	protected final Logger log = Logger.getLogger("Minecraft");

	private JavaPlugin pluginRef;
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();
	
	public GrubsWirelessRedstone(JavaPlugin plugin) {
		this.pluginRef = plugin;
	}
	
	public boolean isTransmitter(String text) {
		return text.equalsIgnoreCase("[transmitter]");
	}
	
	public boolean isReceiver(String text) {
		return text.equalsIgnoreCase("[receiver]");
	}
	
	public boolean isValidChannel(String text) {
		return (text.length() > 0);
	}
	
	public boolean isValidNode(String[] lines) {		
		if (!isValidChannel(lines[1])) {
			return false;
		}
		
		if ( !( isTransmitter(lines[0]) || isReceiver(lines[0]) ) ) {
			return false;
		}
		
		return true;
	}
	
	public void addNode(Block block, String[] lines) {
		if (!isValidNode(lines)) {
			return;
		}
		
		String channelName = lines[1];
		Channel channelObj = null;
		if (channels.containsKey(channelName)) {
			log.info("Found channel");
			channelObj = channels.get(channelName);
		}
		else {
			log.info("Creating channel " + channelName);
			channelObj = new Channel(channelName);
			channels.put(channelName, channelObj);
		}
		
		ChannelNode newNode = new ChannelNode(block);
		
		if (isTransmitter(lines[0])) {
			log.info("Adding as transmitter");
			channelObj.addTransmitter(newNode);
		}
		else if (isReceiver(lines[0])) {
			log.info("Adding as receiver");
			channelObj.addReceiver(newNode);
		}
	}
	
	public void removeNode(Sign sign) {
		String channelName = sign.getLine(1);
		
		if (channels.containsKey(channelName)) {
			Channel curChannel = channels.get(channelName);
			
			if (isTransmitter(sign.getLine(0))) {
				log.info("Removing transmitter on channel " + channelName);
				curChannel.removeTransmitterAt(sign.getBlock().getLocation());
			}
			else if (isReceiver(sign.getLine(0))) {
				log.info("Removing receiver on channel " + channelName);
				curChannel.removeReceiverAt(sign.getBlock().getLocation());
			}
			
			if (curChannel.isEmptyChannel()) {
				log.info("Removing empty channel " + channelName);
				channels.remove(channelName);
			}
		}
	}
	
	public void updateReceivers(Sign sign, boolean poweredOn) {
		Channel curChannel = channels.get(sign.getLine(1));
		
		if (poweredOn) {
			// replace signs with torches
			ArrayList<Location> receiverLocations = getReceiverLocations(curChannel);
			for (Location loc : receiverLocations) {
				this.replaceWithTorch(loc);
			}
		}
		else {
			// replace torches with signs
			for (ChannelNode node : curChannel.getReceiversList()) {
				this.restoreSign(sign, node);
			}
		}
	}
	
	private ArrayList<Location> getReceiverLocations(Channel channel) {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		for (ChannelNode node : channel.getReceiversList()) {
			locations.add(this.getNodeLocation(node));
		}
		
		return locations;
	}
	
	private Location getNodeLocation(ChannelNode node) {
		return new Location(
			this.pluginRef.getServer().getWorld(node.getWorld()), 
			node.getX(),
			node.getY(),
			node.getZ()
		);
	}
	
	private void replaceWithTorch(Location loc) {
		Block blockAtLoc = loc.getBlock();
		
		if (blockAtLoc.getType() == Material.SIGN_POST) {
			blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x5, true);
		}
		else if (blockAtLoc.getType() == Material.WALL_SIGN) {
			// facing east, replace with torch with data 0x4 to face east
			if (blockAtLoc.getData() == 0x2) {
				blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x4, true); 
			}
			// facing west, replace with torch with data 0x3 to face west
			else if (blockAtLoc.getData() == 0x3) {
				blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x3, true); 
			}
			// facing north, replace with torch with data 0x2 to face north
			else if (blockAtLoc.getData() == 0x4) {
				blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x2, true); 
			}
			// facing south, replace with torch with data 0x1 to face south
			else if (blockAtLoc.getData() == 0x5) {
				blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x1, true); 
			}
		}
	}
	
	private void restoreSign(Sign sign, ChannelNode node) {
		Location loc = this.getNodeLocation(node);
		Block blockAtLoc = loc.getBlock();
		
		if (node.isWallSign()) {
			blockAtLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte)node.getDirection(), true);
		}
		else {
			blockAtLoc.setTypeIdAndData(Material.SIGN_POST.getId(), (byte)node.getDirection(), true);
		}
		
		Sign newSignRef = (Sign)blockAtLoc.getState();
		newSignRef.setLine(0, "[receiver]");
		newSignRef.setLine(1, sign.getLine(1));
		newSignRef.update(true);
	}
}
