package com.selfequalsthis.grubsplugin.modules.regions;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionsModule extends AbstractGrubsModule {

	private RegionsServiceProvider serviceProvider;

	public RegionsModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[RegionsModule]: ";
		this.dataFileName = "regions.dat";

		this.serviceProvider = new RegionsServiceProvider(this);
		this.eventListeners = new RegionsEventListeners();
		this.commandHandlers = new RegionsCommandHandlers(this, this.serviceProvider);
	}

	@Override
	public void enable() {
		this.registerService(RegionService.class, this.serviceProvider);
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);

		this.serviceProvider.init();
	}

	@Override
	public void disable() {
		this.serviceProvider.shutdown();

		this.unregisterCommands(this.commandHandlers);
		this.unregisterEventHandlers(this.eventListeners);
		this.unregisterService(this.serviceProvider);
	}

}
