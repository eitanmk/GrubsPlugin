package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class WeatherControlModule extends AbstractGrubsModule {

	public WeatherControlModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[WeatherControlModule]: ";
		this.eventListeners = new WeatherControlEventListeners();
		this.commandHandlers = new WeatherControlCommandHandlers(this);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);
	}

	@Override
	public void disable() {
		this.unregisterCommands(this.commandHandlers);
		this.unregisterEventHandlers(this.eventListeners);
	}

}
