package com.selfequalsthis.grubsplugin.modules.locks;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class LocksModule extends AbstractGrubsModule {

	KeypadPopup popupObj;
	LocksEventListeners eventListeners;
	
	public LocksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[LocksModule]: ";
		this.popupObj = new KeypadPopup(this.pluginRef);
		this.eventListeners = new LocksEventListeners(this);
	}
	
	@Override
	public void enable() {
		this.registerEventHandlers(this.popupObj);
		this.registerEventHandlers(this.eventListeners);
	}

	public void showPinScreen(Player player) {
		this.popupObj.showKeypadToPlayer(player);
	}

}
