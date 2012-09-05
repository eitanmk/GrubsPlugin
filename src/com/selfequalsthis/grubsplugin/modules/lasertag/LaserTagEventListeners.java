package com.selfequalsthis.grubsplugin.modules.lasertag;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class LaserTagEventListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {

		if (event.getEntity() instanceof Player) {
			Player source = (Player) event.getEntity();

			if (event.getCause() == DamageCause.PROJECTILE &&
					GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.IN_PROGRESS &&
					GrubsLaserTag.isPlaying(source)) {

				EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent)event;
				Entity damager = newEvent.getDamager();
				
				// first, make sure it was a projectile that did the damaging
				if (damager instanceof Projectile) {
					Projectile projectile = (Projectile)damager;
					Player shooter = (Player)projectile.getShooter();
					
					// can't shoot yourself
					if (shooter.getDisplayName() != source.getDisplayName()) {
						if (GrubsLaserTag.isPlaying(shooter)) {
							GrubsLaserTag.updateScore(shooter, source);
						}
					}	
				}
			}
		}
	}
}
