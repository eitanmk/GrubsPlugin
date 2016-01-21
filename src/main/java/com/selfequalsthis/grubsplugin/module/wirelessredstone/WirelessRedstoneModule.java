package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import org.spongepowered.api.Game;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.module.AbstractGrubsModule;

public class WirelessRedstoneModule extends AbstractGrubsModule {

	private GrubsWirelessRedstone wrController;

	public WirelessRedstoneModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[WirelessRedstoneModule]: ";
		this.dataFileName = "wireless_redstone.dat";

		this.wrController = new GrubsWirelessRedstone(this);
		this.eventListeners = new WirelessRedstoneEventListeners(this.wrController);
		this.commandHandlers = new WirelessRedstoneCommandHandlers(this);
	}

	@Override
	public void enable() {
		this.registerEventHandlers(this.eventListeners);
		this.registerCommands(this.commandHandlers);

		this.wrController.init();
	}

	@Override
	public void disable() {
		this.wrController.shutdown();

		this.unregisterEventHandlers(this.eventListeners);
		this.unregisterCommands(this.commandHandlers);
	}

	public void repairChannels(World world) {
		this.wrController.cleanupChannels(world);
	}

}
