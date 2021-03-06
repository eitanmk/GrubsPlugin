package com.selfequalsthis.grubsplugin.modules.defendtheshed;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class DefendShedEventListeners implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent event) {
		Player player = event.getPlayer();
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.IN_PROGRESS &&
			GrubsDefendShed.isPlaying(player)) {
			player.setSneaking(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player source = (Player) event.getEntity();

			if (event.getCause() == DamageCause.PROJECTILE &&
				GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.IN_PROGRESS &&
				GrubsDefendShed.isPlaying(source)) {

				// "it" player shouldn't get respawned
				if (GrubsDefendShed.isItPlayer(source)) {
					return;
				}

				EntityDamageByEntityEvent newEvent = (EntityDamageByEntityEvent)event;
				Entity damager = newEvent.getDamager();

				// first, make sure it was a projectile that did the damaging
				if (damager instanceof Projectile) {
					Projectile projectile = (Projectile)damager;
					Player shooter = (Player)projectile.getShooter();

					// only if the shooter is "it" do we respawn
					if (GrubsDefendShed.isItPlayer(shooter)) {
						event.setCancelled(true);
						GrubsDefendShed.respawnPlayer(source);
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player source = (Player) event.getPlayer();

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.IN_PROGRESS &&
			GrubsDefendShed.isPlaying(source)) {

			Block target = event.getClickedBlock();
			Action action = event.getAction();

			if (action == Action.RIGHT_CLICK_BLOCK && GrubsDefendShed.isTargetButton(target) &&
				!GrubsDefendShed.isItPlayer(source)) {
				GrubsDefendShed.playersWin();
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockDamage(BlockDamageEvent event) {
		Player source = event.getPlayer();
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.IN_PROGRESS &&
			GrubsDefendShed.isPlaying(source)) {
			event.setCancelled(true);
			return;
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		Player player = event.getPlayer();
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.IN_PROGRESS &&
			GrubsDefendShed.isPlaying(player)) {
			GrubsDefendShed.respawnPlayer(player);
			return;
		}
	}
}
