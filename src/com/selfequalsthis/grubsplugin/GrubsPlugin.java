package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.gamefixes.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.gameinfo.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.gametweaks.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.inventory.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.lasertag.LaserTagModule;
import com.selfequalsthis.grubsplugin.modules.teleport.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.weathercontrol.WeatherControlModule;
import com.selfequalsthis.grubsplugin.modules.wirelessredsone.WirelessRedstoneModule;

public class GrubsPlugin extends JavaPlugin implements CommandExecutor {

	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[GrubsPlugin]: ";
	private final String settingFileName = "active-modules.dat";
	private final String moduleSettingKey = "modules";

	private HashMap<String,AbstractGrubsModule> allModules = new HashMap<String,AbstractGrubsModule>();
	private HashMap<String,AbstractGrubsModule> activeModules = new HashMap<String,AbstractGrubsModule>();

	public GrubsPlugin() {
		this.allModules.put("gameinfo", new GameInfoModule(this));
		this.allModules.put("gamefixes", new GameFixesModule(this));
		this.allModules.put("gametweaks",new GameTweaksModule(this));
		this.allModules.put("inventory", new InventoryModule(this));
		this.allModules.put("lasertag", new LaserTagModule(this));
		this.allModules.put("teleport", new TeleportModule(this));
		this.allModules.put("weather", new WeatherControlModule(this));
		this.allModules.put("wirelessredstone", new WirelessRedstoneModule(this));
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
		ArrayList<String> modulesToLoad = getModulesToLoad();
		for (String moduleKey : modulesToLoad) {
			AbstractGrubsModule gm = this.allModules.get(moduleKey);
			if (gm != null) {
				log.info(logPrefix + "Enabling module [" + moduleKey + "].");
				this.activeModules.put(moduleKey, gm);
				gm.enable();
			}
		}

		GrubsCommandManager cmdMgr = GrubsCommandManager.getInstance();
		cmdMgr.registerCommand("gpmodule", this, "Change modules enabled state.",
				"/<command> [list|enable|disable] <module-name>");

		log.info(logPrefix + "Plugin is enabled.");
	}

	@Override
	public void onDisable() {
		log.info(logPrefix + "Disabling plugin.");

		log.info(logPrefix + "Saving loaded modules list.");
		saveActiveModules();

		log.info(logPrefix + "Uninitializing modules.");
		for (String moduleKey : this.activeModules.keySet()) {
			AbstractGrubsModule gm = this.activeModules.get(moduleKey);
			log.info(logPrefix + "Disabling module [" + moduleKey + "].");
			gm.disable();
		}

		log.info(logPrefix + "Plugin is disabled.");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdName = command.getName().toLowerCase();

		if (!sender.isOp()) {
			return false;
		}

		log.info(sender.getName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		if (args.length < 1) {
			return false;
		}

		String subCommand = args[0];
		if (subCommand.equalsIgnoreCase("list")) {
			String resp = "";
			String separator = "";
			for (String key : this.allModules.keySet()) {
				resp = resp + separator + key + (this.activeModules.containsKey(key) ? "(X)" : "( )");
				separator = " ";
			}
			sender.sendMessage("Modules: " + resp + ".");
			return true;
		}

		if (args.length > 1) {
			String moduleName = args[1];
			AbstractGrubsModule gm = null;

			if (!this.allModules.containsKey(moduleName)) {
				sender.sendMessage("Unknown module [" + moduleName + "].");
				return true;
			}

			if (subCommand.equalsIgnoreCase("enable")) {
				if (this.activeModules.containsKey(moduleName)) {
					sender.sendMessage("Module [" + moduleName + "] already enabled.");
					return true;
				}

				gm = this.allModules.get(moduleName);
				this.activeModules.put(moduleName, gm);
				gm.enable();
				sender.sendMessage("Module [" + moduleName + "] enabled.");
			}
			else if (subCommand.equalsIgnoreCase("disable")) {
				if (!this.activeModules.containsKey(moduleName)) {
					sender.sendMessage("Module [" + moduleName + "] not enabled.");
					return true;
				}

				gm = this.allModules.get(moduleName);
				this.activeModules.remove(moduleName);
				gm.disable();
				sender.sendMessage("Module [" + moduleName + "] disabled.");
			}
		}

		return true;
	}


	private ArrayList<String> getModulesToLoad() {
		ArrayList<String> ret = null;

		File dataFile = new File(this.getDataFolder(), this.settingFileName);
		if (dataFile.exists()) {
			try {
				Properties modulesToLoad = new Properties();
				FileInputStream in = new FileInputStream(dataFile);
				modulesToLoad.load(in);
				in.close();

				String moduleSettingsStr = modulesToLoad.getProperty(this.moduleSettingKey);
				log.info(logPrefix + "Modules to load: " + moduleSettingsStr);
				String[] moduleList = moduleSettingsStr.split(",");
				return new ArrayList<String>(Arrays.asList(moduleList));
			}
			catch (Exception e) {
				log.info(logPrefix + "Failed to read properties from modules file.");
			}
		}

		log.info(logPrefix + "Loading default modules.");
		ret = new ArrayList<String>(this.allModules.keySet().size());
		for (String key : this.allModules.keySet()) {
			ret.add(key);
		}

		return ret;
	}

	private void saveActiveModules() {
		String moduleKeySettingsStr = "";
		String separator = "";
		for (String moduleKey : this.activeModules.keySet()) {
			moduleKeySettingsStr = moduleKeySettingsStr + separator + moduleKey;
			separator = ",";
		}

		Properties moduleSettings = new Properties();
		moduleSettings.setProperty(this.moduleSettingKey, moduleKeySettingsStr);
		log.info(logPrefix + "Saving loaded modules: " + moduleKeySettingsStr);

		try {
			File dataFile = new File(this.getDataFolder(), this.settingFileName);
			FileOutputStream out = new FileOutputStream(dataFile);
			moduleSettings.store(out, "List of modules to load on startup");
			out.flush();
			out.close();
		}
		catch (Exception e) {
			log.info(logPrefix + "Failed to write module properties file.");
		}
	}

}
