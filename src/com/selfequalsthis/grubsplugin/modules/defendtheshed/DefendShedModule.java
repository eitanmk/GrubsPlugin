package com.selfequalsthis.grubsplugin.modules.defendtheshed;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class DefendShedModule extends AbstractGrubsModule {

	private DefendShedEventListeners eventListeners;
	private DefendShedCommandHandlers commandHandlers;

	public DefendShedModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[DefendShedModule]: ";
		this.eventListeners = new DefendShedEventListeners();
		this.commandHandlers = new DefendShedCommandHandlers(this);
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
