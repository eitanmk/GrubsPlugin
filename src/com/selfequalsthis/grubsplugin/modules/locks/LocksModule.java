package com.selfequalsthis.grubsplugin.modules.locks;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.player.SpoutPlayer;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class LocksModule extends AbstractGrubsModule {
	
	private LocksEventListeners eventListeners;
	private HashMap<String,KeypadPopup> popupInstances = new HashMap<String,KeypadPopup>();
	
	public LocksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[LocksModule]: ";
		this.eventListeners = new LocksEventListeners(this);
	}
	
	@Override
	public void enable() {
		this.registerEventHandlers(this.eventListeners);
	}
	
	public boolean playerAuthorized(Player player) {
		if (popupInstances.containsKey(player.getName())) {
			KeypadPopup instance = popupInstances.get(player.getName());
			return instance.codeCorrect();
		}
		
		return false;
	}
	
	public void removePlayer(Player player) {
		if (popupInstances.containsKey(player.getName())) {
			popupInstances.remove(player.getName());
		}
	}

	public void showPinScreen(Player player, Inventory inventory) {
		KeypadPopup popup = new KeypadPopup(this.pluginRef, (SpoutPlayer) player, inventory);
		popup.createKeypadPopup();
		popup.showKeypadToPlayer();
		popupInstances.put(player.getName(), popup);
	}
	
	public void handleScreenClose(Player player) {
		if (popupInstances.containsKey(player.getName())) {
			KeypadPopup instance = popupInstances.get(player.getName());
			if (!instance.codeCorrect()) {
				// if the code isn't correct, we reset and delete
				instance.resetGuess();
				popupInstances.remove(player.getName());
			}
		}
	}
	
	public void handleButtonPress(Player player, String digit) {
		if (popupInstances.containsKey(player.getName())) {
			KeypadPopup instance = popupInstances.get(player.getName());
			instance.addToGuess(digit);
			
			if (instance.codeCorrect()) {
				this.log("Code correct!");
				instance.hideKeypad();
				instance.showInventory();
			}
		}
	}
}
