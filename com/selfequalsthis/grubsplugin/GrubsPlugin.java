package com.selfequalsthis.grubsplugin; 

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.gamefixes.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.gameinfo.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.gametweaks.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.inventory.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.lasertag.LaserTagModule;
import com.selfequalsthis.grubsplugin.modules.teleport.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.weathercontrol.WeatherControlModule;
import com.selfequalsthis.grubsplugin.modules.wirelessredsone.WirelessRedstoneModule;

public class GrubsPlugin extends JavaPlugin {
	
	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[GrubsPlugin]: ";
	private ArrayList<AbstractGrubsModule> modules = new ArrayList<AbstractGrubsModule>();

	@Override
	public void onDisable() {
		log.info(logPrefix + "Disabling plugin.");
		
		for (AbstractGrubsModule gm : modules) {
			gm.disable();
		}
		
		log.info(logPrefix + "Plugin is disabled.");
	}

	@Override
	public void onEnable() {
		log.info(logPrefix + "Enabling plugin.");
		
		File dataDir = this.getDataFolder();
		if (!dataDir.exists()) {
			log.info(logPrefix + "Creating plugin data directory: " + dataDir.toString());
			dataDir.mkdir();
		}
		
		log.info(logPrefix + "Initializing modules.");

		modules.add(new GameInfoModule(this));
		modules.add(new GameFixesModule(this));
		modules.add(new GameTweaksModule(this));
		modules.add(new InventoryModule(this));
		modules.add(new LaserTagModule(this));
		modules.add(new TeleportModule(this));
		modules.add(new WeatherControlModule(this));
		modules.add(new WirelessRedstoneModule(this));
		
		for (AbstractGrubsModule gm : modules) {
			gm.enable();
		}
		
		log.info(logPrefix + "Plugin is enabled.");
	}

}
