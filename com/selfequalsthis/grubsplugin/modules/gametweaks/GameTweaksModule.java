package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class GameTweaksModule extends AbstractGrubsModule {
	
	private GameTweaksEventListeners eventListeners;
	private GameTweaksCommandHandlers commandHandlers;
		
	public GameTweaksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameTweaksModule]: ";
		this.eventListeners = new GameTweaksEventListeners();
		this.commandHandlers = new GameTweaksCommandHandlers(this);
	}
	
	@Override
	public void enable() {		
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);
	}
	
}
