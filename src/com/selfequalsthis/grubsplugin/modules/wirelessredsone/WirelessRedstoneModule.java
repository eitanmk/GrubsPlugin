package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class WirelessRedstoneModule extends AbstractGrubsModule {

	private GrubsWirelessRedstone wrController;
	private WirelessRedstoneEventListeners eventListeners;
	private WirelessRedstoneCommandHandlers commandHandlers;

	public WirelessRedstoneModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[WirelessRedstoneModule]: ";
		this.dataFileName = "wireless_redstone.dat";

		this.wrController = new GrubsWirelessRedstone(this);
		this.eventListeners = new WirelessRedstoneEventListeners(this.wrController);
		this.commandHandlers = new WirelessRedstoneCommandHandlers(this);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);

		this.wrController.init();
	}

	@Override
	public void disable() {
		this.wrController.shutdown();

		this.unregisterCommands(this.commandHandlers);
		this.unregisterEventHandlers(this.eventListeners);
	}

	public void repairChannels(World world) {
		this.wrController.cleanupChannels(world);
	}

}
