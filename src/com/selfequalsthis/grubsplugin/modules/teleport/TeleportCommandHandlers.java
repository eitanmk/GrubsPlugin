package com.selfequalsthis.grubsplugin.modules.teleport;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

public class TeleportCommandHandlers extends AbstractGrubsCommandHandler {

	private TeleportModule tpModule;

	public TeleportCommandHandlers(TeleportModule module) {
		this.moduleRef = module;
		this.tpModule = module;
	}

	@GrubsCommandHandler(
		command = "goto",
		desc = "Teleport to the named preset location or player.",
		usage = "/<command> <player>|<preset location>|last|grave|quit"
	)
	public boolean onGotoCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length > 0) {
				String argName = args[0];

				// check presets
				if (this.tpModule.playerSpecialLocations.containsKey(argName)) {
					if (this.tpModule.getPlayerSpecialLocation(executingPlayer, argName) != null) {
						Location targetLocation = this.tpModule.getPlayerSpecialLocation(executingPlayer, argName).clone();
						this.tpModule.teleportPlayer(executingPlayer, targetLocation);
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer,
							GrubsMessager.MessageLevel.ERROR,
							this.tpModule.playerSpecialLocations.get(argName)
						);
					}
				}
				else if (this.tpModule.teleportPresets.containsKey(argName)) {
					this.tpModule.teleportPlayer(executingPlayer, this.tpModule.teleportPresets.get(argName));
				}
				else {
					// match players
					OfflinePlayer[] offlinePlayers = Bukkit.getOfflinePlayers();
					ArrayList<OfflinePlayer> matches = new ArrayList<OfflinePlayer>();

					String lowerArg = argName.toLowerCase();

					for (int i = 0, len = offlinePlayers.length; i < len; ++i) {
						OfflinePlayer current = offlinePlayers[i];
						if (current.getName().toLowerCase().startsWith(lowerArg)) {
							matches.add(current);
						}
					}

					if (matches.size() > 0) {
						if (matches.size() > 1) {
							String matchStr = "";
							for (OfflinePlayer player : matches) {
								matchStr = matchStr + player.getName() + " ";
							}
							GrubsMessager.sendMessage(
								executingPlayer,
								GrubsMessager.MessageLevel.INFO,
								"Multiple matches: " + matchStr
							);
						}
						else {
							OfflinePlayer target = matches.get(0);
							if (target.isOnline()) {
								Location tpLoc = Bukkit.getPlayerExact(target.getName()).getLocation();
								this.tpModule.teleportPlayer(executingPlayer, tpLoc);
							}
							else {
								this.tpModule.teleportPlayer(
									executingPlayer,
									this.tpModule.getPlayerSpecialLocation(target, "quit")
								);
							}
						}
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer,
							GrubsMessager.MessageLevel.ERROR,
							"No presets or players matching '" + argName + "'."
						);
					}
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

	@GrubsCommandHandler(
		command = "fetch",
		desc = "Teleports a named player to the current position.",
		usage = "/<command> <player>"
	)
	public boolean onFetchCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length > 0) {
				List<Player> matches = Bukkit.matchPlayer(args[0]);
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
						this.tpModule.teleportPlayer(target, executingPlayer.getLocation());
					}
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + args[0] + "'."
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

	@GrubsCommandHandler(
		command = "send",
		desc = "Teleport named player to another user or a saved preset. ",
		usage = "/<command> <player> <player>|<preset name>"
	)
	public boolean onSendCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length == 2) {
			List<Player> targetMatches = Bukkit.matchPlayer(args[0]);
			if (targetMatches.size() > 0) {
				if (targetMatches.size() > 1) {
					String matchStr = "";
					for (Player player : targetMatches) {
						matchStr = matchStr + player.getName() + " ";
					}
					GrubsMessager.sendMessage(
						sender,
						GrubsMessager.MessageLevel.INFO,
						"Multiple matches: " + matchStr
					);
				}
				else {
					Player target = targetMatches.get(0);

					if (this.tpModule.teleportPresets.containsKey(args[1])) {
						this.tpModule.teleportPlayer(target, this.tpModule.teleportPresets.get(args[1]));
					}
					else {
						List<Player> destMatches = Bukkit.matchPlayer(args[1]);
						if (destMatches.size() > 0) {
							if (destMatches.size() > 1) {
								String matchStr = "";
								for (Player player : destMatches) {
									matchStr = matchStr + player.getName() + " ";
								}
								GrubsMessager.sendMessage(
									sender,
									GrubsMessager.MessageLevel.INFO,
									"Multiple matches: " + matchStr
								);
							}
							else {
								Player dest = destMatches.get(0);
								this.tpModule.teleportPlayer(target, dest.getLocation());
							}
						}
						else {
							GrubsMessager.sendMessage(
								sender,
								GrubsMessager.MessageLevel.ERROR,
								"No players matching '" + args[1] + "'."
							);
						}
					}
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
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Missing command argument."
			);
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "tpset",
		desc = "Create a new preset with the given name for the current position.",
		usage = "/<command> <preset name>"
	)
	public boolean onTpSetCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length > 0) {
				String argName = args[0];
				if (this.tpModule.teleportPresets.containsKey(argName)) {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"Preset '" + argName + "' already exists."
					);
				}
				else {
					this.tpModule.teleportPresets.put(argName, executingPlayer.getLocation());
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"Preset '" + argName + "' saved."
					);
					this.tpModule.saveTeleportPresets();
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

	@GrubsCommandHandler(
		command = "tpdel",
		desc = "Delete a saved preset teleport location.",
		usage = "/<command> <preset name>"
	)
	public boolean onTpDelCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			String argName = args[0];
			if (this.tpModule.teleportPresets.containsKey(argName)) {
				this.tpModule.teleportPresets.remove(argName);
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.INFO,
					"Preset '" + argName + "' deleted."
				);
				this.tpModule.saveTeleportPresets();
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"Preset '" + argName + "' not found."
				);
			}
		}
		else {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Missing command argument."
			);
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "tplist",
		desc = "List saved teleport preset names."
	)
	public boolean onTpListCommand(CommandSender sender, Command command, String alias, String[] args) {

		String msgIdentifier = "[Teleport] ";
		Set<String> keys = this.tpModule.teleportPresets.keySet();
		ArrayList<String> origList = new ArrayList<String>(keys);
		ArrayList<String> filteredList = new ArrayList<String>();

		for (int i = 0, len = origList.size(); i < len; ++i) {
			String cur = origList.get(i);
			if (cur.indexOf(TeleportModule.SEPARATOR) == -1) {
				filteredList.add(cur);
			}
		}

		if (filteredList.size() > 0) {
			GrubsUtilities.multilinePrint(sender, msgIdentifier, filteredList.toArray(new String[0]));
		}
		else {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				msgIdentifier + "No presets in list."
			);
		}

		return true;
	}

	@GrubsCommandHandler(
		command = "tpinfo",
		desc = "Get the coordinates of a preset teleport location.",
		usage = "/<command> <preset name>"
	)
	public boolean onTpInfoCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			String argName = args[0];
			if (this.tpModule.teleportPresets.containsKey(argName)) {
				Location infoLoc = this.tpModule.teleportPresets.get(argName);
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.INQUIRY,
					argName +
					": x=" + Math.floor(infoLoc.getX()) +
					", z=" + Math.floor(infoLoc.getZ()) +
					", y=" + Math.floor(infoLoc.getY())
				);
			}
			else {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"Preset '" + argName + "' not found."
				);
			}
		}
		else {
			GrubsMessager.sendMessage(
				sender,
				GrubsMessager.MessageLevel.ERROR,
				"Missing command argument."
			);
		}

		return true;
	}
}
