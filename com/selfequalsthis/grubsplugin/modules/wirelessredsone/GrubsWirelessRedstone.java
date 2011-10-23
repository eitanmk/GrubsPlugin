package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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
	
	private String redstoneMainDirectory = "plugins/WirelessRedstone";
	private File RedstonePresetFile = new File(redstoneMainDirectory + File.separator + "channels.dat");
	
	public void init() {
		File mainDir = new File(redstoneMainDirectory);
		if (!mainDir.exists()) {
			log.info("Wireless Redstone save directory doesn't exist. Creating.");
			mainDir.mkdir();
		}
		
		if(!RedstonePresetFile.exists()){
			log.info("Wireless Redstone channel file doesn't exist. Creating.");
			try {
				RedstonePresetFile.createNewFile();
			} 
			catch (IOException ex) {
				this.log.info("Error creating Wireless Redstone channel file!");
				ex.printStackTrace();
			}
		}
		
		log.info("Loading Wireless Redstone channels.");
		this.loadChannels();
		log.info("Loaded " + channels.size() + " channels.");
	}
	
	public void shutdown() {
		log.info("Saving Wireless Redstone channels.");
		this.saveChannels();
		log.info("Saved " + channels.size() + " channels.");
	}
	
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
		
		this.saveChannels();
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
			
			this.saveChannels();
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
	
	public void loadChannels() {
		FileInputStream fis = null;
		ObjectInputStream in = null;
		
		try {
			fis = new FileInputStream(RedstonePresetFile);
			in = new ObjectInputStream(fis);
			
			Object obj = in.readObject();
			while (obj != null) {
				if (obj instanceof Channel) {
					Channel loadedChannel = (Channel)obj;
					this.channels.put(loadedChannel.getName(), loadedChannel);
				}
				obj = in.readObject();
			}
		}
		catch (EOFException eof) {
			log.info("End of file reached.");
		}
		catch (Exception ex) {
			log.info("Error reading Wireless Redstone channels file!");
			ex.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void saveChannels() {		
		log.info("Writing Wireless Redstone save file.");
		try {
			FileOutputStream fos = new FileOutputStream(RedstonePresetFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);
			
			for (String name : channels.keySet()) {
				out.writeObject(channels.get(name));
			}
			
			out.close();
		}
		catch (Exception ex) {
			log.info("Error writing Wireless Redstone channels file!");
			ex.printStackTrace();
		}
	}
}
