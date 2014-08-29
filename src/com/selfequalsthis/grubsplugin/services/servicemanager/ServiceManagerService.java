package com.selfequalsthis.grubsplugin.services.servicemanager;

import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.services.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.services.regions.RegionsService;

public class ServiceManagerService extends AbstractGrubsService {

	public HashMap<String,AbstractGrubsService> allServices = new HashMap<String,AbstractGrubsService>();

	public ServiceManagerService(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[ServiceManagerService]: ";

		this.allServices.put("regions", new RegionsService(this.pluginRef));
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
