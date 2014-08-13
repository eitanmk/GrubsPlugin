package com.selfequalsthis.grubsplugin.modules.gameinfo;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class GameInfoModule extends AbstractGrubsModule {

	private GameInfoCommandHandlers commandHandlers;

	public GameInfoModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameInfoModule]: ";
		this.commandHandlers = new GameInfoCommandHandlers(this);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}

	@Override
	public void disable() {
		this.unregisterCommands(this.commandHandlers);
	}

	public HashMap<String,Integer> matchMaterialName(String name) {
		HashMap<String,Integer> results = new HashMap<String,Integer>();

		Material[] materialNames = Material.values();
		for (Material m : materialNames) {
			if (m.toString().indexOf(name.toUpperCase()) != -1) {
				results.put(m.toString().toLowerCase(), m.getId());
			}
		}

		return results;
	}

	public String matchMaterialId(int id) {
		Material material = Material.getMaterial(id);
		return material.toString().toLowerCase();
	}

	public String getCoordsStrFromLocation(Location loc) {
		return "x: " + (int)loc.getX() + ", z: " + (int)loc.getZ() + " Altitude: " + (int)(loc.getY() + 1);
	}
}
