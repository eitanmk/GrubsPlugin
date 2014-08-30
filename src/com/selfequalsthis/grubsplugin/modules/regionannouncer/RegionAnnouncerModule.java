package com.selfequalsthis.grubsplugin.modules.regionannouncer;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class RegionAnnouncerModule extends AbstractGrubsModule {

	public RegionAnnouncerModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[RegionAnnouncerModule]: ";

		this.eventListeners = new RegionAnnouncerEventListeners();
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
