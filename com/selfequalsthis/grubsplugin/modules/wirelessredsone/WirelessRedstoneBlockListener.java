package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.util.logging.Logger;

import org.bukkit.block.Sign;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WirelessRedstoneBlockListener extends BlockListener {
	protected final Logger log = Logger.getLogger("Minecraft");
	
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
					this.controllerRef.powerChanged(signObj, poweredOn);
				}
			}
		}
	}
	
	public void onSignChange(SignChangeEvent event) {
		if (this.controllerRef.isValidNode(event.getLines())) {
			this.controllerRef.addNode(event.getBlock(), event.getLines());
		}
	}
}
