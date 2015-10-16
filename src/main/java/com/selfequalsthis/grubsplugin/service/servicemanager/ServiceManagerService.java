package com.selfequalsthis.grubsplugin.service.servicemanager;

import java.util.HashMap;

import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.service.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.service.regions.RegionsService;

public class ServiceManagerService extends AbstractGrubsService {

	public HashMap<String,AbstractGrubsService> allServices = new HashMap<String,AbstractGrubsService>();

	public ServiceManagerService(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[ServiceManagerService]: ";

		this.allServices.put("regions", new RegionsService(this.pluginRef, this.game));
	}

	@Override
	public void startup() {
		for (String serviceKey : this.allServices.keySet()) {
			AbstractGrubsService gs = this.allServices.get(serviceKey);
			if (gs != null) {
				this.log("Enabling service [" + serviceKey + "].");
				gs.startup();
			}
		}
	}

	@Override
	public void shutdown() {
		for (String serviceKey : this.allServices.keySet()) {
			AbstractGrubsService gs = this.allServices.get(serviceKey);
			if (gs != null) {
				this.log("Disabling service [" + serviceKey + "].");
				gs.shutdown();
			}
		}
	}
}
