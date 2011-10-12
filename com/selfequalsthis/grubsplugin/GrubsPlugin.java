package com.selfequalsthis.grubsplugin; 

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.gametweaks.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.lasertag.LaserTagModule;
import com.selfequalsthis.grubsplugin.modules.weathercontrol.WeatherControlModule;

public class GrubsPlugin extends JavaPlugin {
	
	private final Logger log = Logger.getLogger("Minecraft");
	private ArrayList<AbstractGrubsModule> modules = new ArrayList<AbstractGrubsModule>();

	@Override
	public void onDisable() {		
		for (AbstractGrubsModule gm : modules) {
			gm.disable();
		}
		
		log.info("GrubsPlugin is now disabled.");
	}

	@Override
	public void onEnable() {
		log.info("[GrubsPlugin]: Initializing modules.");
		
		modules.add(new WeatherControlModule());
		modules.add(new TeleportModule());
		modules.add(new InventoryModule());
		modules.add(new GameInfoModule());
		modules.add(new GameFixesModule());
		modules.add(new GameTweaksModule());
		modules.add(new LaserTagModule());
		
		for (AbstractGrubsModule gm : modules) {
			gm.enable(this);
		}
		
		log.info("GrubsPlugin is enabled.");
	}

}
