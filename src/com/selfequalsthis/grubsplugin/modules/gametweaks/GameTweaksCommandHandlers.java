package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class GameTweaksCommandHandlers extends AbstractGrubsCommandHandler {

	private GameTweaksModule gtModule;

	public GameTweaksCommandHandlers(GameTweaksModule module) {
		this.moduleRef = module;
		this.gtModule = module;
	}

	@GrubsCommandHandler(
		command = "timelock",
		desc = "Toggle freezing time to current value, or to the specified time value or preset.",
		usage = "/<command> off|on|day|night|<0-24000>"
	)
	public boolean onTimeLockCommand(CommandSender sender, Command command, String alias, String[] args) {

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
							return true;
						}
					}
					catch (Exception e) {
						GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Invalid argument.");
						return true;
					}
				}

				this.gtModule.lockTime(world, timecode);
				GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.INFO, "Time locked at " + timecode);
			}
		}
		else {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Missing command argument.");
		}

		return true;
	}
}
