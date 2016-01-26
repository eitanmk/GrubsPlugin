package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.spongepowered.api.text.Text;

public class GrubsWirelessRedstone {

	protected final Logger log = Logger.getLogger("Minecraft");

	private WirelessRedstoneModule moduleRef;

	public static String TRANSMITTER_TEXT = "[WRt]";
	public static String RECEIVER_TEXT = "[WRr]";
	public static String RECEIVER_INVERTED_TEXT = "[WRri]";
	/*
	private HashMap<String,Channel> channels = new HashMap<String,Channel>();

	public GrubsWirelessRedstone(WirelessRedstoneModule module) {
		this.moduleRef = module;
	}

	public void init() {

		File dataFile = this.moduleRef.getDataFile();
		if (dataFile != null) {
			this.moduleRef.log("Loading Wireless Redstone channels.");
			this.loadChannels();
			this.moduleRef.log("Loaded " + channels.size() + " channels.");
		}


	}

	public void shutdown() {
		this.moduleRef.log("Saving Wireless Redstone channels.");
		this.saveChannels();
		this.moduleRef.log("Saved " + channels.size() + " channels.");
	}
*/
	public static boolean isTransmitter(String text) {
		return text.equalsIgnoreCase(TRANSMITTER_TEXT);
	}

	public static boolean isReceiver(String text) {
		return (text.equalsIgnoreCase(RECEIVER_TEXT) || text.equalsIgnoreCase(RECEIVER_INVERTED_TEXT));
	}

	public static boolean isReceiverInverted(String text) {
		return text.equalsIgnoreCase(RECEIVER_INVERTED_TEXT);
	}

	public static boolean hasValidChannel(String text) {
		return (text.length() > 0);
	}

	public static boolean isValidNode(List<Text> lines) {
		if (!hasValidChannel(lines.get(1).toPlain())) {
			return false;
		}

		if ( !( isTransmitter(lines.get(0).toPlain()) || isReceiver(lines.get(0).toPlain()) ) ) {
			return false;
		}

		return true;
	}
/*
	public void addNode(Block block, String[] lines) {
		if (!isValidNode(lines)) {
			return;
		}

		String channelName = lines[1];
		Channel channelObj = null;
		if (channels.containsKey(channelName)) {
			this.moduleRef.log("Found channel " + channelName);
			channelObj = channels.get(channelName);
		}
		else {
			this.moduleRef.log("Creating channel " + channelName);
			channelObj = new Channel(channelName);
			channels.put(channelName, channelObj);
		}

		if (isTransmitter(lines[0])) {
			this.moduleRef.log("Adding as transmitter");
			channelObj.addTransmitter(block);
		}
		else if (isReceiver(lines[0])) {
			this.moduleRef.log("Adding as receiver");
			channelObj.addReceiver(block, isReceiverInverted(lines[0]));
		}

		this.saveChannels();
	}

	public void removeNode(Sign sign) {
		String channelName = sign.getLine(1);

		if (channels.containsKey(channelName)) {
			Channel curChannel = channels.get(channelName);

			if (isTransmitter(sign.getLine(0))) {
				this.moduleRef.log("Removing transmitter on channel " + channelName);
				curChannel.removeTransmitterAt(sign.getBlock().getLocation());
			}
			else if (isReceiver(sign.getLine(0))) {
				this.moduleRef.log("Removing receiver on channel " + channelName);
				curChannel.removeReceiverAt(sign.getBlock().getLocation());
			}

			if (curChannel.isEmpty()) {
				this.moduleRef.log("Removing empty channel " + channelName);
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
		File dataFile = this.moduleRef.getDataFile();
		if (dataFile == null) {
			this.moduleRef.log("Error with data file. Nothing can be loaded!");
			return;
		}

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(dataFile);
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
		catch (EOFException eof) { }
		catch (Exception ex) {
			this.moduleRef.log("Error reading Wireless Redstone channels file!");
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
		File dataFile = this.moduleRef.getDataFile();
		if (dataFile == null) {
			this.moduleRef.log("Error with data file. Nothing will be saved!");
			return;
		}

		this.moduleRef.log("Writing Wireless Redstone save file.");
		try {
			FileOutputStream fos = new FileOutputStream(dataFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);

			for (String name : channels.keySet()) {
				out.writeObject(channels.get(name));
			}

			out.close();
		}
		catch (Exception ex) {
			this.moduleRef.log("Error writing Wireless Redstone channels file!");
			ex.printStackTrace();
		}
	}

	public void removeReceiverOnAnyChannel(Block block) {
		for (Channel channel : channels.values()) {
			channel.removeReceiverAt(block.getLocation());
		}
	}

	public void checkChannelsForPhysicsUpdates(Block block) {
		boolean removedNode = false;
		Channel affectedChannel = null;

		for (Channel channel : channels.values()) {
			removedNode = channel.handlePhysicsChange(block);
			if (removedNode) {
				affectedChannel = channel;
				break;
			}
		}

		if (removedNode) {
			if (affectedChannel.isEmpty()) {
				String name = affectedChannel.getName();
				this.moduleRef.log("Physics removal cleared the channel. removing empty channel '" + name + "'");
				channels.remove(name);
			}
			this.saveChannels();
		}
	}

	public void cleanupChannels(World world) {
		Iterator<Channel> channelIterator = channels.values().iterator();
		while (channelIterator.hasNext()) {
			Channel channel = channelIterator.next();
			log.info(channel.toString());
			channel.cleanup(world);
			if (channel.isEmpty()) {
				log.info("Deleting empty channel");
				channelIterator.remove();
			}
		}

		this.saveChannels();
	}
	*/
}
