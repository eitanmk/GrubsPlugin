package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class GameTweaksBlockListener extends BlockListener {
		
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (item.getMaxStackSize() > 1) {
			ItemStack playerItem = event.getPlayer().getInventory().getItemInHand();
			if (playerItem.getAmount() == 1) {
				playerItem.setAmount(item.getMaxStackSize());
			}
		}
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		
		// make sure diamond items don't break
		if (player.isOp()) {
			Material inHandItem = event.getItemInHand().getType();
			if (inHandItem == Material.DIAMOND_AXE
				|| inHandItem == Material.DIAMOND_HOE
				|| inHandItem == Material.DIAMOND_PICKAXE 
				|| inHandItem == Material.DIAMOND_SPADE) {
				
				event.getPlayer().getInventory().getItemInHand().setDurability((short) 0);
			}
		}
		
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
