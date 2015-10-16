package com.selfequalsthis.grubsplugin.service;

import org.spongepowered.api.service.ProviderExistsException;

import com.selfequalsthis.grubsplugin.AbstractGrubsComponent;

public abstract class AbstractGrubsService extends AbstractGrubsComponent {

	public abstract void startup();
	public abstract void shutdown();

	protected <T> void registerService(Class<T> serviceType, T provider) {
		this.log("Registering provider for " + serviceType.getSimpleName() + " service");
		try {
			this.game.getServiceManager().setProvider(this.pluginRef, serviceType, provider);
		} catch (ProviderExistsException e) {
			this.log("Failed to register provider for " + serviceType.getSimpleName() + "service!");
			e.printStackTrace();
		}
	}
	
	// TODO: unregister services?

}
