package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.SignChangeEvent;

public class WirelessRedstoneBlockListener implements Listener {
	protected final Logger log = Logger.getLogger("Minecraft");
	
	private GrubsWirelessRedstone controllerRef;
	
	public WirelessRedstoneBlockListener(GrubsWirelessRedstone gwr) {
		this.controllerRef = gwr;
	}
	
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();			
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				this.controllerRef.removeNode(signObj);
			}
		}
		else if (event.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
			this.controllerRef.removeReceiverOnAnyChannel(event.getBlock());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		if (event.getBlock().getState() instanceof Sign) {
			Sign signObj = (Sign)event.getBlock().getState();
			if (GrubsWirelessRedstone.isValidNode(signObj.getLines())) {
				if (GrubsWirelessRedstone.isTransmitter(signObj.getLine(0))) {
					boolean poweredOn = (event.getBlock().isBlockPowered() || event.getBlock().isBlockIndirectlyPowered());
					this.controllerRef.powerChanged(signObj, poweredOn);
				}
			}
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSignChange(SignChangeEvent event) {
		if (GrubsWirelessRedstone.isValidNode(event.getLines())) {
			this.controllerRef.addNode(event.getBlock(), event.getLines());
		}
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBlockPhysics(BlockPhysicsEvent event) {	
		if ((event.getBlock().getState() instanceof Sign) || event.getBlock().getType() == Material.REDSTONE_TORCH_ON) {
			this.controllerRef.checkChannelsForPhysicsUpdates(event.getBlock());
		}
	}
}
