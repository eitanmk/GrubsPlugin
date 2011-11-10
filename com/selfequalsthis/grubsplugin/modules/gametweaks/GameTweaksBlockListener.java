package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;

public class GameTweaksBlockListener extends BlockListener {
		
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		
		// obsidian is protected
		if (event.getBlock().getType() == Material.OBSIDIAN) {
			// if not an op, don't let them even break it
			if (!player.isOp()) {
				event.setCancelled(true);
				return;
			}
		}
	}
	
	// disable fires
	public void onBlockIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	public void onBlockBurn(BlockBurnEvent event)  {
		event.setCancelled(true);
	}
}
