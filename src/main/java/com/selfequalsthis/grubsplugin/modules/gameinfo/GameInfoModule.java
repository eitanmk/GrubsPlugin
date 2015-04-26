package com.selfequalsthis.grubsplugin.modules.gameinfo;

import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class GameInfoModule extends AbstractGrubsModule {

	public GameInfoModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[GameInfoModule]: ";

		this.commandHandlers.add(new GameInfoCommandGettime());
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}

	@Override
	public void disable() {
		this.unregisterCommands(this.commandHandlers);
	}

	/*
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
	 */
}
