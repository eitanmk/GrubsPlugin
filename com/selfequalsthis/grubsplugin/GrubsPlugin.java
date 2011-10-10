package com.selfequalsthis.grubsplugin; 

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.WeatherControlModule;
import com.selfequalsthis.grubsplugin.modules.gametweaks.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.lasertag.LaserTagModule;

public class GrubsPlugin extends JavaPlugin {
	
	private final Logger log = Logger.getLogger("Minecraft");
	private ArrayList<IGrubsModule> modules = new ArrayList<IGrubsModule>();

	@Override
	public void onDisable() {		
		for (IGrubsModule gm : modules) {
			gm.disable();
		}
		
		log.info("GrubsPlugin is now disabled.");
	}

	@Override
	public void onEnable() {
		log.info("[GrubsPlugin]: Initializing modules.");
		
		modules.add(new WeatherControlModule(this));
		modules.add(new TeleportModule(this));
		modules.add(new InventoryModule(this));
		modules.add(new GameInfoModule(this));
		modules.add(new GameFixesModule(this));
		modules.add(new GameTweaksModule(this));
		modules.add(new LaserTagModule(this));
		
		for (IGrubsModule gm : modules) {
			gm.enable();
		}
		
		log.info("GrubsPlugin is enabled.");
	}

}
