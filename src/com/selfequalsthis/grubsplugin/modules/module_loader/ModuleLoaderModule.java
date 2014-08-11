package com.selfequalsthis.grubsplugin.modules.module_loader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.modules.gamefixes.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.gameinfo.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.gametweaks.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.inventory.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.lasertag.LaserTagModule;
import com.selfequalsthis.grubsplugin.modules.teleport.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.weathercontrol.WeatherControlModule;
import com.selfequalsthis.grubsplugin.modules.wirelessredsone.WirelessRedstoneModule;

public class ModuleLoaderModule extends AbstractGrubsModule {

	private final String moduleSettingKey = "modules";

	public HashMap<String,AbstractGrubsModule> allModules = new HashMap<String,AbstractGrubsModule>();
	public HashMap<String,AbstractGrubsModule> activeModules = new HashMap<String,AbstractGrubsModule>();

	private ModuleLoaderCommandHandlers commandHandlers;

	public ModuleLoaderModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[ModuleLoaderModule]: ";
		this.dataFileName = "active-modules.dat";
		this.commandHandlers = new ModuleLoaderCommandHandlers(this);

		// all new modules need to be listed here
		this.allModules.put("gamefixes", new GameFixesModule(plugin));
		this.allModules.put("gameinfo", new GameInfoModule(plugin));
		this.allModules.put("gametweaks",new GameTweaksModule(plugin));
		this.allModules.put("inventory", new InventoryModule(plugin));
		this.allModules.put("lasertag", new LaserTagModule(plugin));
		this.allModules.put("teleport", new TeleportModule(plugin));
		this.allModules.put("weather", new WeatherControlModule(plugin));
		this.allModules.put("wirelessredstone", new WirelessRedstoneModule(plugin));
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);

		ArrayList<String> modulesToLoad = getModulesToLoad();
		for (String moduleKey : modulesToLoad) {
			AbstractGrubsModule gm = this.allModules.get(moduleKey);
			if (gm != null) {
				this.log("Enabling module [" + moduleKey + "].");
				this.activeModules.put(moduleKey, gm);
				gm.enable();
			}
		}
	}

	@Override
	public void disable() {
		this.log("Saving loaded modules list.");
		saveActiveModules();

		this.log("Uninitializing modules.");
		for (String moduleKey : this.activeModules.keySet()) {
			AbstractGrubsModule gm = this.activeModules.get(moduleKey);
			this.log("Disabling module [" + moduleKey + "].");
			gm.disable();
		}

		this.unregisterCommands(this.commandHandlers);
	}

	private ArrayList<String> getModulesToLoad() {
		// initialize return value to all modules
		ArrayList<String> ret = new ArrayList<String>(this.allModules.keySet().size());
		String allModulesList = "";
		String separator = "";
		for (String key : this.allModules.keySet()) {
			ret.add(key);
			allModulesList = allModulesList + separator + key;
			separator = ",";
		}

		File dataFile = this.getDataFile();
		if (dataFile != null) {
			try {
				Properties modulesToLoad = new Properties();
				FileInputStream in = new FileInputStream(dataFile);
				modulesToLoad.load(in);
				in.close();

				String moduleSettingsStr = modulesToLoad.getProperty(this.moduleSettingKey);
				if (moduleSettingsStr != null) {
					this.log("Modules to load: " + moduleSettingsStr);
					String[] moduleList = moduleSettingsStr.split(",");
					return new ArrayList<String>(Arrays.asList(moduleList));
				}
			}
			catch (Exception e) {
				this.log("Failed to read properties from modules file.");
			}
		}

		this.log("Loading all modules: " + allModulesList);
		return ret;
	}

	private void saveActiveModules() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing will be saved!");
			return;
		}

		String moduleKeySettingsStr = "";
		String separator = "";
		for (String moduleKey : this.activeModules.keySet()) {
			moduleKeySettingsStr = moduleKeySettingsStr + separator + moduleKey;
			separator = ",";
		}

		Properties moduleSettings = new Properties();
		moduleSettings.setProperty(this.moduleSettingKey, moduleKeySettingsStr);
		this.log("Saving loaded modules: " + moduleKeySettingsStr);

		try {
			FileOutputStream out = new FileOutputStream(dataFile);
			moduleSettings.store(out, "List of modules to load on startup");
			out.flush();
			out.close();
		}
		catch (Exception e) {
			this.log("Failed to write module properties file.");
		}
	}
}
