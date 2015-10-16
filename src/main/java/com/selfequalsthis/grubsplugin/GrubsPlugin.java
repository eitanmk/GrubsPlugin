package com.selfequalsthis.grubsplugin;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppingServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.ConfigDir;

import com.google.inject.Inject;
import com.selfequalsthis.grubsplugin.module.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.module.moduleloader.ModuleLoaderModule;
import com.selfequalsthis.grubsplugin.service.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.service.servicemanager.ServiceManagerService;

@Plugin(id = "grubsplugin", name = "GrubsPlugin", version = "1.0")
public class GrubsPlugin {

	private final String logPrefix = "[GrubsPlugin]: ";

	@Inject
	private Logger logger;

	@Inject
	@ConfigDir(sharedRoot = false)
	private File dataFolder;

	@Inject
	private Game game;

	private AbstractGrubsService serviceManager;
	private AbstractGrubsModule moduleLoader;

	public Logger getLogger() {
		return this.logger;
	}

	public File getDataFolder() {
		return this.dataFolder;
	}

	@Listener
	public void onServerStarting(GameStartingServerEvent event) {
		this.logger.info(this.logPrefix + "Enabling plugin...");

		File dataDir = this.getDataFolder();
		if (!dataDir.exists()) {
			this.logger.info(this.logPrefix + "Creating plugin data directory: " + dataDir.toString());
			dataDir.mkdir();
		}
		
		this.logger.info(this.logPrefix + "Initializing services...");
		this.serviceManager = new ServiceManagerService(this, this.game);
		this.serviceManager.startup();

		this.logger.info(this.logPrefix + "Initializing module loader...");
		this.moduleLoader = new ModuleLoaderModule(this, this.game);
		this.moduleLoader.enable();

		this.logger.info(this.logPrefix + "Plugin is enabled.");
	}

	@Listener
	public void onServerStopping(GameStoppingServerEvent event) {
		this.logger.info(this.logPrefix + "Disabling plugin...");

		this.logger.info(this.logPrefix + "Disabling module loader...");
		this.moduleLoader.disable();
		
		this.logger.info(this.logPrefix + "Disabling services...");
		this.serviceManager.shutdown();

		this.logger.info(this.logPrefix + "Plugin is disabled.");
	}

}
