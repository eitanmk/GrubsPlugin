package com.selfequalsthis.grubsplugin.modules.gameinfo;

import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class GameInfoCommandHandlers extends AbstractGrubsCommandHandler {

	private GameInfoModule gameInfoModule;

	public GameInfoCommandHandlers(GameInfoModule module) {
		this.componentRef = module;
		this.gameInfoModule = module;
	}

	@GrubsCommandHandler(
		command = "dataval",
		desc = "Get the id value of the targeted block, or the id of the requested material name.",
		usage = "/<command> [<name>]"
	)
	public boolean onDataValCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			// lookup the text typed
			HashMap<String,Integer> matches = this.gameInfoModule.matchMaterialName(args[0]);
			if (matches.size() > 0) {
				for (String key : matches.keySet()) {
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						key + " = " + matches.get(key)
					);
				}
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"No matches for '" + args[0] + "'."
				);
			}
		}
		else {
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;
				Material target = executingPlayer.getTargetBlock(null, 256).getType();
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.INFO,
					target.toString().toLowerCase() + " = " + target.getId()
				);
			}
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "dataname",
		desc = "Get the name of the targeted block, or the name of requested material id.",
		usage = "/<command> [<id>]"
	)
	public boolean onDataNameCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			try {
				int val = Integer.parseInt(args[0]);
				String res = this.gameInfoModule.matchMaterialId(val);
				if (res != "") {
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						"" + val + " = " + res
					);
				}
				else {
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.ERROR,
						"No material for id: " + val + "."
					);
				}
			}
			catch (NumberFormatException nex) { }
		}
		else {
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;
				Material target = executingPlayer.getTargetBlock(null, 256).getType();
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO,
					"" + target.getId() + " = " + target.toString().toLowerCase()
				);
			}
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "gettime",
		desc = "Get the current game time."
	)
	public boolean onGetTimeCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.INFO,
				"Current time is: " + executingPlayer.getWorld().getTime()
			);
		}
		else {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.INFO,
				"Current time is: " + Bukkit.getServer().getWorlds().get(0).getTime()
			);
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "getcoords",
		desc = "Get own position, or provided player's position.",
		usage = "/<command> [<player name>]"
	)
	public boolean onGetCoordsCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			String argName = args[0];
			Player player = Bukkit.getPlayer(argName);
			if (player == null) {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"Player '" + argName + "' not found."
				);
			}
			else {
				Location loc = player.getLocation();
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.INFO,
					player.getDisplayName() + ": " + this.gameInfoModule.getCoordsStrFromLocation(loc)
				);
			}
		}
		else {
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;
				Location loc = executingPlayer.getLocation();
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO,
					executingPlayer.getDisplayName() + ": " + this.gameInfoModule.getCoordsStrFromLocation(loc)
				);
			}
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "sendcoords",
		desc = "Send current position to another player.",
		usage = "/<command> <player name>"
	)
	public boolean onSendCoordsCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			if (args.length > 0) {
				String argName = args[0];
				Player target = Bukkit.getPlayer(argName);
				if (target == null) {
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.ERROR,
						"Player '" + argName + "' not found."
					);
				}
				else {
					Location loc = executingPlayer.getLocation();
					GrubsMessager.sendMessage(
						target,
						GrubsMessager.MessageLevel.INFO,
						executingPlayer.getDisplayName() + ": " + this.gameInfoModule.getCoordsStrFromLocation(loc)
					);

					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"Coordinates sent to " + target.getDisplayName()
					);
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
			}
		}

		return true;
	}

}
