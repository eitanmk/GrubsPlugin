package com.selfequalsthis.grubsplugin.services;

import org.bukkit.Bukkit;
import org.bukkit.plugin.ServicePriority;

import com.selfequalsthis.grubsplugin.AbstractGrubsComponent;

public abstract class AbstractGrubsService extends AbstractGrubsComponent {

	public abstract void startup();
	public abstract void shutdown();

	protected <T> void registerService(Class<T> serviceType, T provider) {
		this.log("Registering provider for " + serviceType.getSimpleName() + " service");
		Bukkit.getServer().getServicesManager().register(serviceType, provider, this.pluginRef, ServicePriority.Normal);
	}

	protected <T> void unregisterService(T provider) {
		this.log("Unregistering service provider");
		Bukkit.getServer().getServicesManager().unregister(provider);
	}

}
