package com.selfequalsthis.grubsplugin.modules.gameinfo;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class GameInfoCommandHandlers extends AbstractGrubsCommandHandler {

	private GameInfoModule gameInfoModule;

	public GameInfoCommandHandlers(GameInfoModule module) {
		this.moduleRef = module;
		this.gameInfoModule = module;
	}

	@GrubsCommandHandler(
		command = "dataval",
		desc = "Get the id value of the targeted block, or the id of the requested material name.",
		usage = "/<command> [<name>]"
	)
	public void onDataValCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

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
	}

	@GrubsCommandHandler(
		command = "dataname",
		desc = "Get the name of the targeted block, or the name of requested material id.",
		usage = "/<command> [<id>]"
	)
	public void onDataNameCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

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
	}

	@GrubsCommandHandler(
		command = "gettime",
		desc = "Get the current game time."
	)
	public void onGetTimeCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;

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
	}

	@GrubsCommandHandler(
		command = "getcoords",
		desc = "Get own position, or provided player's position.",
		usage = "/<command> [<player name>]"
	)
	public void onGetCoordsCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length > 0) {
			String argName = args[0];
			// match players
			List<Player> matches = Bukkit.matchPlayer(argName);
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
					Player target = matches.get(0);
					Location loc = target.getLocation();
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						target.getName() + ": " + this.gameInfoModule.getCoordsStrFromLocation(loc)
					);
				}
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + argName + "'."
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
					executingPlayer.getName() + ": " + this.gameInfoModule.getCoordsStrFromLocation(loc)
				);
			}
		}
	}

	@GrubsCommandHandler(
		command = "sendcoords",
		desc = "Send current position to another player.",
		usage = "/<command> <player name>"
	)
	public void onSendCoordsCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			if (args.length > 0) {
				String argName = args[0];
				// match players
				List<Player> matches = Bukkit.matchPlayer(argName);
				if (matches.size() > 0) {
					if (matches.size() > 1) {
						String matchStr = "";
						for (Player player : matches) {
							matchStr = matchStr + player.getName() + " ";
						}
						GrubsMessager.sendMessage(
							executingPlayer,
							GrubsMessager.MessageLevel.INFO,
							"Multiple matches: " + matchStr
						);
					}
					else {
						Player target = matches.get(0);
						Location loc = executingPlayer.getLocation();
						GrubsMessager.sendMessage(
							target,
							GrubsMessager.MessageLevel.INFO,
							executingPlayer.getName() + ": " +
								this.gameInfoModule.getCoordsStrFromLocation(loc)
						);
						GrubsMessager.sendMessage(
							executingPlayer,
							GrubsMessager.MessageLevel.INFO,
							"Coordinates sent to " + target.getName()
						);
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + argName + "'."
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
	}

}
