package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class GameTweaksCommandHandlers extends AbstractGrubsCommandHandler {

	private GameTweaksModule gtModule;

	public GameTweaksCommandHandlers(GameTweaksModule module) {
		this.moduleRef = module;
		this.gtModule = module;
	}

	@GrubsCommandHandler(
		command = "buildmode",
		desc = "Toggle creative \"build\" mode.",
		usage = "/<command> off|on"
	)
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

	@GrubsCommandHandler(
		command = "timelock",
		desc = "Toggle freezing time to current value, or to the specified time value or preset.",
		usage = "/<command> off|on|day|night|<0-24000>"
	)
	public void onTimeLockCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length > 0) {
			String argVal = args[0];
			if (argVal.equalsIgnoreCase("off")) {
				this.gtModule.unlockTime();
				GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.INFO, "Time lock is off.");
			}
			else {
				World world;
				int timecode;

				if (sender instanceof Player) {
					Player executingPlayer = (Player) sender;
					world = executingPlayer.getWorld();
				}
				else {
					world = Bukkit.getServer().getWorlds().get(0);
				}

				if (argVal.equalsIgnoreCase("on")) {
					timecode = (int) world.getTime();
				}
				else if (argVal.equalsIgnoreCase("day")) {
					timecode = 6000;
				}
				else if (argVal.equalsIgnoreCase("night")) {
					timecode = 18000;
				}
				else {
					try {
						timecode = Integer.parseInt(argVal);
						if (timecode < 0 || timecode > 24000) {
							GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Invalid time.");
							return;
						}
					}
					catch (Exception e) {
						GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Invalid argument.");
						return;
					}
				}

				this.gtModule.lockTime(world, timecode);
				GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.INFO, "Time locked at " + timecode);
			}
		}
		else {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Missing command argument.");
		}
	}
}
