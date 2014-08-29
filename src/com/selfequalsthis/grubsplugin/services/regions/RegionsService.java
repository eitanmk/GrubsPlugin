package com.selfequalsthis.grubsplugin.services.regions;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.services.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.services.RegionService;

public class RegionsService extends AbstractGrubsService {

	private RegionsServiceProvider serviceProvider;

	public RegionsService(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[RegionsService]: ";
		this.dataFileName = "regions.dat";

		this.serviceProvider = new RegionsServiceProvider(this);
		this.commandHandlers = new RegionsCommandHandlers(this, this.serviceProvider);
	}

	@Override
	public void startup() {
		this.registerService(RegionService.class, this.serviceProvider);
		this.registerCommands(this.commandHandlers);
		this.serviceProvider.init();
	}

	@Override
	public void shutdown() {
		this.serviceProvider.shutdown();
		this.unregisterCommands(this.commandHandlers);
		this.unregisterService(this.serviceProvider);
	}

}
