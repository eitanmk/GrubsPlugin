package com.selfequalsthis.grubsplugin.module.regionannouncer;

import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.module.AbstractGrubsModule;

public class RegionAnnouncerModule extends AbstractGrubsModule {
		
	public RegionAnnouncerModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[RegionAnnouncerModule]: ";

		this.eventListeners = new RegionAnnouncerEventListeners(this);
	}

	@Override
	public void enable() {
		this.registerEventHandlers(this.eventListeners);
	}

	@Override
	public void disable() {
		this.unregisterEventHandlers(this.eventListeners);
	}

}
