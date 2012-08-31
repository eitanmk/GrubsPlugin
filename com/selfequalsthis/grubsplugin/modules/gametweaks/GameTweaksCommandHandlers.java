package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;

public class GameTweaksCommandHandlers extends AbstractGrubsCommandHandler {

	public GameTweaksCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}
	
	@GrubsCommandHandler(command = "buildmode")
	public void onBuildModeCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			if (args.length > 0) {
				if (args[0].equalsIgnoreCase("on")) {
					executingPlayer.setGameMode(GameMode.CREATIVE);
					GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Creative build mode enabled.");
				}
				else {
					executingPlayer.setGameMode(GameMode.SURVIVAL);
					GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Build mode disabled.");
				}
			}
			else {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Missing command argument.");
			}
		}
	}
}
