package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.modules.moduleloader.ModuleLoaderModule;
import com.selfequalsthis.grubsplugin.services.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.services.servicemanager.ServiceManagerService;

public class GrubsPlugin extends JavaPlugin {

	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[GrubsPlugin]: ";

	private AbstractGrubsService serviceManager;
	private AbstractGrubsModule moduleLoader;

	@Override
	public void onEnable() {
		log.info(logPrefix + "Enabling plugin.");

		File dataDir = this.getDataFolder();
		if (!dataDir.exists()) {
			log.info(logPrefix + "Creating plugin data directory: " + dataDir.toString());
			dataDir.mkdir();
		}

		log.info(logPrefix + "Initializing services.");
		this.serviceManager = new ServiceManagerService(this);
		this.serviceManager.startup();

		log.info(logPrefix + "Initializing module loader.");
		this.moduleLoader = new ModuleLoaderModule(this);
		this.moduleLoader.enable();

		log.info(logPrefix + "Plugin is enabled.");
	}

	@Override
	public void onDisable() {
		log.info(logPrefix + "Disabling plugin.");

		log.info(logPrefix + "Disabling module loader.");
		this.moduleLoader.disable();

		log.info(logPrefix + "Disabling services.");
		this.serviceManager.shutdown();

		log.info(logPrefix + "Plugin is disabled.");
	}

}
