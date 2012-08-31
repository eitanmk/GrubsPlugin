package com.selfequalsthis.grubsplugin.modules.teleport;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class TeleportCommandHandlers extends AbstractGrubsCommandHandler {

	private TeleportModule tpModule;
	
	public TeleportCommandHandlers(TeleportModule module) {
		this.moduleRef = module;
		this.tpModule = module;
	}

	@GrubsCommandHandler(command = "goto")
	public void onGotoCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
		
			if (args.length > 0) {
				String argName = args[0];

				// check presets
				if (argName.equalsIgnoreCase("last")) {
					if (this.tpModule.getLastLocation(executingPlayer) != null) {
						// copy the object, b/c we don't want to overwrite the reference
						Location playerLastLocation = this.tpModule.getLastLocation(executingPlayer).clone();
						// save the current location
						this.tpModule.saveLastLocation(executingPlayer);
						executingPlayer.teleport(playerLastLocation);
					}
					else {
						GrubsMessager.sendMessage(
							executingPlayer, 
							GrubsMessager.MessageLevel.ERROR,
							"No previous location set."
						);
					}
				}
				else if (this.tpModule.teleportPresets.containsKey(argName)) {
					this.tpModule.saveLastLocation(executingPlayer);
					executingPlayer.teleport(this.tpModule.teleportPresets.get(argName));
				}
				else {
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
							this.tpModule.saveLastLocation(executingPlayer);
							executingPlayer.teleport(target.getLocation());
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
	}
	
	@GrubsCommandHandler(command = "fetch")
	public void onFetchCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
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
						this.tpModule.saveLastLocation(target);
						target.teleport(executingPlayer.getLocation());
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
	}
	
	@GrubsCommandHandler(command = "send")
	public void onSendCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
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
						this.tpModule.saveLastLocation(target);
						target.teleport(this.tpModule.teleportPresets.get(args[1]));
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
								this.tpModule.saveLastLocation(target);
								target.teleport(dest.getLocation());
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
	}
	
	@GrubsCommandHandler(command = "tpset")
	public void onTpSetCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
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
	}
	
	@GrubsCommandHandler(command = "tpdel")
	public void onTpDelCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
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
	}
	
	@GrubsCommandHandler(command = "tplist")
	public void onTpListCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		
		String msgIdentifier = "[Teleport] ";
		Set<String> keys = this.tpModule.teleportPresets.keySet();

		if (keys.size() > 0) {
			GrubsUtilities.multilinePrint(sender, msgIdentifier, keys.toArray(new String[0]));
		}
		else {
			GrubsMessager.sendMessage(
				sender, 
				GrubsMessager.MessageLevel.ERROR,
				msgIdentifier + "No presets in list."
			);
		}
	}
}
