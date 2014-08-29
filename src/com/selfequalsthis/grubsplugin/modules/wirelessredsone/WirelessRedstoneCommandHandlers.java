package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;

public class WirelessRedstoneCommandHandlers extends AbstractGrubsCommandHandler {

	private WirelessRedstoneModule wrModule;

	public WirelessRedstoneCommandHandlers(WirelessRedstoneModule module) {
		this.componentRef = module;
		this.wrModule = module;
	}

	@GrubsCommandHandler(
		command = "wrchannelclean",
		desc = "Utility to clean corrupted wireless redstone channels."
	)
	public boolean onChannelCleanCommand(CommandSender sender, Command command, String alias, String[] args) {
		World worldObj = null;

		// TODO take a world name as an optional second parameter
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			worldObj = executingPlayer.getWorld();
		}
		else {
			worldObj = Bukkit.getWorlds().get(0);
		}

		this.wrModule.repairChannels(worldObj);

		return true;
	}
}
