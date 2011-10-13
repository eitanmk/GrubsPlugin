package com.selfequalsthis.grubsplugin.modules.lasertag;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityListener;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class LaserTagEntityListener extends EntityListener {

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
					Player shooter = null;
					
					// now, see what kind of projectile. we only allow arrows and snowballs
					if (damager instanceof Arrow) {
						Arrow arrowProjectile = (Arrow)damager;
						shooter = (Player)arrowProjectile.getShooter();
					}
					else if (damager instanceof Snowball) {
						Snowball snowballProjectile = (Snowball)damager;
						shooter = (Player)snowballProjectile.getShooter();
					}
					
					if (shooter != null) {
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
}
