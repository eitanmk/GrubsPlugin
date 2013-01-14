package com.selfequalsthis.grubsplugin.modules.locks;

import java.util.logging.Logger;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.metadata.Metadatable;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.event.screen.ScreenCloseEvent;
import org.getspout.spoutapi.gui.ScreenType;

public class LocksEventListeners implements Listener {
	protected final Logger logger = Logger.getLogger("Minecraft");
	
	private LocksModule module;

	public LocksEventListeners(LocksModule locksModule) {
		module = locksModule;
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent event) {
		Player player = (Player) event.getPlayer();
		
		if (this.module.playerAuthorized(player)) {
			logger.info("Player authorized!");
			this.module.removePlayer(player);
			return;
		}
		
		Inventory inventory = event.getInventory();
		InventoryHolder holder = inventory.getHolder();
		
		logger.info("metadatable: " + (holder instanceof Metadatable));
		
		if (holder instanceof Chest || holder instanceof DoubleChest) {
			logger.info("Challenging player");
			this.module.showPinScreen(player, inventory);
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onScreenClose(ScreenCloseEvent event) {
		if (event.getScreenType() == ScreenType.CUSTOM_SCREEN) {
			this.module.handleScreenClose(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onButtonClick(ButtonClickEvent event) {
		this.module.handleButtonPress(event.getPlayer(), event.getButton().getText());
	}
}
