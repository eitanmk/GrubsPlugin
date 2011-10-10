package com.selfequalsthis.grubsplugin.modules.lasertag;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
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
				Arrow projectile = (Arrow)newEvent.getDamager();
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
