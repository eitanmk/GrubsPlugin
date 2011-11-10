package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class WirelessRedstoneModule extends AbstractGrubsModule {
	
	private GrubsWirelessRedstone wrController;
	private WirelessRedstoneBlockListener blockListener;
	private WirelessRedstonePlayerListener playerListener;
	
	public WirelessRedstoneModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[WirelessRedstoneModule]: ";
		this.dataFileName = "wireless_redstone.dat";
		
		this.wrController = new GrubsWirelessRedstone(this);
		this.blockListener = new WirelessRedstoneBlockListener(this.wrController);
		this.playerListener = new WirelessRedstonePlayerListener(this.wrController);
	}
	
	public void enable() {
		this.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.REDSTONE_CHANGE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.SIGN_CHANGE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Priority.Monitor);
		
		this.wrController.init();
	}
	
	public void disable() {
		this.wrController.shutdown();
	}
}
