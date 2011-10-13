package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WirelessRedstoneBlockListener extends BlockListener {
	
	private GrubsWirelessRedstone controllerRef;
	
	public WirelessRedstoneBlockListener(GrubsWirelessRedstone gwr) {
		this.controllerRef = gwr;
	}

	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			
			if (this.controllerRef.isValidNode(signObj.getLines())) {
				this.controllerRef.removeNode(signObj);
			}
		}
			
		// was the block destroyed a redstone torch?
			// was that torch at a location of a receiver?
	}
	
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			
			if (this.controllerRef.isValidNode(signObj.getLines())) {
				if (this.controllerRef.isTransmitter(signObj.getLine(0))) {
					boolean poweredOn = (event.getBlock().isBlockPowered() || event.getBlock().isBlockIndirectlyPowered());
					this.controllerRef.updateReceivers(signObj, poweredOn);
				}
			}
		}
	}
	
	public void onSignChange(SignChangeEvent event) {
		Block block = event.getBlock();
		if (this.controllerRef.isValidNode(event.getLines())) {
			this.controllerRef.addNode(block, event.getLines());
		}
	}
}
