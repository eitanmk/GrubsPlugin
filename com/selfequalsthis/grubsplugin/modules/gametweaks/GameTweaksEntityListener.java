package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class GameTweaksEntityListener implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {

		if (event.getEntity() instanceof Player) {
			Player source = (Player) event.getEntity();

			if (event.getCause() == DamageCause.DROWNING) {
				if (source.getInventory().getHelmet().getType() == Material.GOLD_HELMET) {
					event.setCancelled(true);
				}
			}
			
			if (event.getCause() == DamageCause.FALL) {
				event.setCancelled(true);
			}
			
			if (event.getCause() == DamageCause.SUFFOCATION) {
				event.setCancelled(true);
			}
		}
	}
	
}
