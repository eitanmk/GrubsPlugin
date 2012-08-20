package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class WirelessRedstoneModule extends AbstractGrubsModule {
	
	private GrubsWirelessRedstone wrController;
	private WirelessRedstoneBlockListener blockListener;
	private WirelessRedstonePlayerListener playerListener;
	
	public WirelessRedstoneModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[WirelessRedstoneModule]: ";
		this.dataFileName = "wireless_redstone.dat";
		
		this.wrController = new GrubsWirelessRedstone(this);
		this.blockListener = new WirelessRedstoneBlockListener(this.wrController);
		this.playerListener = new WirelessRedstonePlayerListener(this.wrController);
	}
	
	public void enable() {
		this.registerCommand("wrchannelclean");
		this.registerEvent(this.blockListener);
		this.registerEvent(this.blockListener);
		this.registerEvent(this.blockListener);
		this.registerEvent(this.blockListener);
		this.registerEvent(this.playerListener);
		
		this.wrController.init();
	}
	
	public void disable() {
		this.wrController.shutdown();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
				
		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));
		
		if (cmdName.equalsIgnoreCase("wrchannelclean")) {
			this.repairChannels(executingPlayer);
		}
		
		return true;
	}
	
	private void repairChannels(Player player) {
		this.wrController.cleanupChannels(player.getWorld());
	}
	
}
