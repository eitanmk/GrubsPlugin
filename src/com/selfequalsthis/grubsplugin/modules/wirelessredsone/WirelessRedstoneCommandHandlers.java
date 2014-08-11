package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandInfo;

public class WirelessRedstoneCommandHandlers extends AbstractGrubsCommandHandler {

	private WirelessRedstoneModule wrModule;

	public WirelessRedstoneCommandHandlers(WirelessRedstoneModule module) {
		this.moduleRef = module;
		this.wrModule = module;
	}

	@GrubsCommandHandler(
		command = "wrchannelclean",
		desc = "Utility to clean corrupted wireless redstone channels."
	)
	public void onChannelCleanCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		World worldObj = null;

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			worldObj = executingPlayer.getWorld();
		}
		else {
			worldObj = Bukkit.getWorlds().get(0);
		}

		this.wrModule.repairChannels(worldObj);
	}
}
