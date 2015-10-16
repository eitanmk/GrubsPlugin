package com.selfequalsthis.grubsplugin.service.regions;

import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.service.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionsService extends AbstractGrubsService {

	private RegionsServiceProvider serviceProvider;

	public RegionsService(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
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
		//TODO: this.unregisterService(this.serviceProvider);
	}

}
