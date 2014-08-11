package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class WeatherControlCommandHandlers extends AbstractGrubsCommandHandler {

	public WeatherControlCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@GrubsCommandHandler(
		command = "strike",
		desc = "Hit targeted block or a player with lightening, but do no damage.",
		usage = "/<command> [<player>]"
	)
	public void onStrikeCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length > 0) {
			// there is a player as an arg
			// find the player's object
			List<Player> matches = Bukkit.matchPlayer(args[0]);
			if (matches.size() > 0) {
				if (matches.size() > 1) {
					String matchStr = "";
					for (Player player : matches) {
						matchStr = matchStr + player.getName() + " ";
					}
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						"Multiple matches: " + matchStr
					);
				}
				else {
					// unambiguous. get 'em!
					Player target = matches.get(0);
					Location playerLoc = target.getLocation();
					World targetWorld = target.getWorld();
					targetWorld.strikeLightningEffect(playerLoc);
				}
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + args[0] + "'."
				);
			}
		}
		else {
			// aim for cursor
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;

				Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
				World targetWorld = executingPlayer.getWorld();
				targetWorld.strikeLightningEffect(target);
			}
		}
	}

	@GrubsCommandHandler(
		command = "zap",
		desc = "Hit targeted block or a player with lightening, doing damage.",
		usage = "/<command> [<player>]"
	)
	public void onZapCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length > 0) {
			// there is a player as an arg
			// find the player's object
			List<Player> matches = Bukkit.matchPlayer(args[0]);
			if (matches.size() > 0) {
				if (matches.size() > 1) {
					String matchStr = "";
					for (Player player : matches) {
						matchStr = matchStr + player.getName() + " ";
					}
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						"Multiple matches: " + matchStr
					);
				}
				else {
					// unambiguous. get 'em!
					Player target = matches.get(0);
					Location playerLoc = target.getLocation();
					World targetWorld = target.getWorld();
					targetWorld.strikeLightning(playerLoc);
				}
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + args[0] + "'."
				);
			}
		}
		else {
			// aim for cursor
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;

				Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
				World targetWorld = executingPlayer.getWorld();
				targetWorld.strikeLightning(target);
			}
		}
	}

	@GrubsCommandHandler(
		command = "storm",
		desc = "Toggle rain storm.",
		usage = "/<command> off|on"
	)
	public void onStormCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length == 0) {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Argument missing."
			);
		}

		if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Invalid argument."
			);
		}

		boolean onFlag = args[0].equalsIgnoreCase("on");
		World worldObj = null;

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			worldObj = executingPlayer.getWorld();
		}
		else {
			worldObj = Bukkit.getServer().getWorlds().get(0);
		}

		worldObj.setStorm(onFlag);
	}

	@GrubsCommandHandler(
		command = "thunder",
		desc = "Toggle thundering.",
		usage = "/<command> off|on"
	)
	public void onThunderCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length == 0) {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Argument missing."
			);
		}

		if (!args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Invalid argument."
			);
		}

		boolean onFlag = args[0].equalsIgnoreCase("on");
		World worldObj = null;

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			worldObj = executingPlayer.getWorld();
		}
		else {
			worldObj = Bukkit.getServer().getWorlds().get(0);
		}

		worldObj.setThundering(onFlag);
	}
}
