package com.selfequalsthis.grubsplugin.listeners;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Rails;

//import com.selfequalsthis.grubsplugin.commands.GrubsObsidianBuildModeCommand;

public class GrubsBlockListener extends BlockListener {
	private final Logger log = Logger.getLogger("Minecraft");
	
	public void onBlockPlace(BlockPlaceEvent event) {
		ItemStack item = event.getItemInHand();
		if (item.getMaxStackSize() > 1) {
			ItemStack playerItem = event.getPlayer().getInventory().getItemInHand();
			if (playerItem.getAmount() == 1) {
				playerItem.setAmount(item.getMaxStackSize());
			}
		}
		
		Block placedBlock = event.getBlock();
		if (placedBlock.getType() == Material.RAILS) {
			//Class<? extends MaterialData> materialDataObj = placedBlock.getType().getData();
			Rails railsObj = new Rails(placedBlock.getType(), placedBlock.getData());
			log.info("" + railsObj.getDirection());
			log.info("" + railsObj.isCurve());
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
		/*if (event.getBlock().getType() == Material.OBSIDIAN) {
			// if not an op, don't let them even break it
			if (!player.isOp()) {
				event.setCancelled(true);
				return;
			}
			else {
				if (GrubsObsidianBuildModeCommand.obsidianBuildModeEnabled) {
					ItemStack damageItem = event.getItemInHand();
					
					if (damageItem.getType() == Material.GOLD_PICKAXE) {
						player.getInventory().getItemInHand().setDurability((short) 0);
						event.setInstaBreak(true);
					}
				}
			}
		}*/
	}
	
	
	// disable fires
	public void onBlockIgnite(BlockIgniteEvent event) {
		event.setCancelled(true);
	}
	
	public void onBlockBurn(BlockBurnEvent event)  {
		event.setCancelled(true);
	}
}
