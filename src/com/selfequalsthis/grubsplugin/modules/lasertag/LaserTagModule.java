package com.selfequalsthis.grubsplugin.modules.lasertag;

import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class LaserTagModule extends AbstractGrubsModule {

	private LaserTagEventListeners eventListeners;
	private LaserTagCommandHandlers commandHandlers;

	public LaserTagModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[LaserTagModule]: ";
		this.eventListeners = new LaserTagEventListeners();
		this.commandHandlers = new LaserTagCommandHandlers(this);
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
