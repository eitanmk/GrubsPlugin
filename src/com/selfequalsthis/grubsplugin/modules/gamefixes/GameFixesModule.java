package com.selfequalsthis.grubsplugin.modules.gamefixes;

import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class GameFixesModule extends AbstractGrubsModule {
	
	private GameFixesEventListeners eventListeners;
	private GameFixesCommandHandlers commandHandlers;
	
	public GameFixesModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameFixesModule]: ";
		this.eventListeners = new GameFixesEventListeners();
		this.commandHandlers = new GameFixesCommandHandlers(this);
	}
	
	@Override
	public void enable() {		
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);
	}

}
