package com.selfequalsthis.grubsplugin.modules.gameinfo;

import org.spongepowered.api.Game;
import org.spongepowered.api.world.Location;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class GameInfoModule extends AbstractGrubsModule {

	public GameInfoModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[GameInfoModule]: ";

		this.commandHandlers = new GameInfoCommandHandlers(this, this.game);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}

	@Override
	public void disable() {
		this.unregisterCommands(this.commandHandlers);
	}

	public String getCoordsStrFromLocation(Location loc) {
		return "x: " + loc.getBlockX() + ", z: " + loc.getBlockZ() + " Altitude: " + loc.getBlockY();
	}
}
