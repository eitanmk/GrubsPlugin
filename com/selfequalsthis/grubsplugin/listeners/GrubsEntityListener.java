package com.selfequalsthis.grubsplugin.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class GrubsEntityListener extends EntityListener {

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
