package com.selfequalsthis.grubsplugin;

import java.io.File;

import org.slf4j.Logger;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.ServerStartingEvent;
import org.spongepowered.api.event.state.ServerStoppingEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.ConfigDir;

import com.google.inject.Inject;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.modules.moduleloader.ModuleLoaderModule;

@Plugin(id = "grubsplugin", name = "GrubsPlugin", version = "1.0")
public class GrubsPlugin {
	
	private final String logPrefix = "[GrubsPlugin]: ";

	@Inject
	private Logger logger;
	
	@Inject
	@ConfigDir(sharedRoot = false)
	private File dataFolder;
	
	private AbstractGrubsModule moduleLoader;
	
	public Logger getLogger() {
	    return this.logger;
	}
	
	@Subscribe
	public void onServerStarting(ServerStartingEvent event) {
		this.logger.info(this.logPrefix + "Enabling plugin...");
		
		File dataDir = this.getDataFolder();
		if (!dataDir.exists()) {
			this.logger.info(this.logPrefix + "Creating plugin data directory: " + dataDir.toString());
			dataDir.mkdir();
		}
		
		this.logger.info(this.logPrefix + "Initializing module loader...");
		this.moduleLoader = new ModuleLoaderModule(this);
		this.moduleLoader.enable();
		
		this.logger.info(this.logPrefix + "Plugin is enabled.");
	}
	
	@Subscribe
	public void onServerStopping(ServerStoppingEvent event) {
		this.logger.info(this.logPrefix + "Disabling plugin...");
		
		this.logger.info(this.logPrefix + "Disabling module loader...");
		this.moduleLoader.disable();
		
		this.logger.info(this.logPrefix + "Plugin is disabled.");
	}
	
	public File getDataFolder() {
		return this.dataFolder;
	}
	
}
