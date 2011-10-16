package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class GrubsWirelessRedstone {
	protected final Logger log = Logger.getLogger("Minecraft");
	
	public static String TRANSMITTER_TEXT = "[WRt]";
	public static String RECEIVER_TEXT = "[WRr]";
	public static String RECEIVER_INVERTED_TEXT = "[WRri]";

	private HashMap<String,Channel> channels = new HashMap<String,Channel>();
		
	public boolean isTransmitter(String text) {
		return text.equalsIgnoreCase(TRANSMITTER_TEXT);
	}
	
	public boolean isReceiver(String text) {
		return (text.equalsIgnoreCase(RECEIVER_TEXT) || text.equalsIgnoreCase(RECEIVER_INVERTED_TEXT));
	}
	
	public boolean isReceiverInverted(String text) {
		return text.equalsIgnoreCase(RECEIVER_INVERTED_TEXT);
	}
	
	public boolean hasValidChannel(String text) {
		return (text.length() > 0);
	}
	
	public boolean isValidNode(String[] lines) {	
		if (!hasValidChannel(lines[1])) {
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
				
		if (isTransmitter(lines[0])) {
			log.info("Adding as transmitter");
			channelObj.addTransmitter(block);
		}
		else if (isReceiver(lines[0])) {
			log.info("Adding as receiver");
			channelObj.addReceiver(block, isReceiverInverted(lines[0]));
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
			
			if (curChannel.isEmpty()) {
				log.info("Removing empty channel " + channelName);
				channels.remove(channelName);
			}
		}
	}
	
	public void powerChanged(Sign sign, boolean poweredOn) {
		Channel curChannel = channels.get(sign.getLine(1));
		if (curChannel != null) {
			if (poweredOn) {
				curChannel.handlePowerChangedOn(sign.getBlock());
			}
			else {
				curChannel.handlePowerChangedOff(sign.getBlock());
			}	
		}
	}
}
