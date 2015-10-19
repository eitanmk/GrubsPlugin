package com.selfequalsthis.grubsplugin.module.worlds;

import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.module.AbstractGrubsModule;

public class WorldsModule extends AbstractGrubsModule {

	public WorldsModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[WorldsModule]: ";

		this.commandHandlers = new WorldsCommandHandlers(this, this.game);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}

	@Override
	public void disable() {
		this.unregisterCommands(this.commandHandlers);
	}

}
