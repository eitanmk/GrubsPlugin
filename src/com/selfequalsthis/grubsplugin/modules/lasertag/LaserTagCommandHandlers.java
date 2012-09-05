package com.selfequalsthis.grubsplugin.modules.lasertag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class LaserTagCommandHandlers extends AbstractGrubsCommandHandler {

	public LaserTagCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}
	
	@GrubsCommandHandler(
		command = "lasertag",
		desc = "Used to setup new games of laser tag.",
		usage = "/<command> create|players|time|restart|start"
	)
	public void onLasertagCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			if (args.length == 0) {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			}
			
			String subcommand = args[0];
			
			if (subcommand.equalsIgnoreCase("create")) {
				this.handleSubCommandCreate(executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("players")) {
				this.handleSubCommandPlayers(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("time")) {
				this.handleSubCommandTime(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("restart")) {
				this.handleSubCommandRestartPoint(executingPlayer);
			}	
			else if (subcommand.equalsIgnoreCase("start")) {
				this.handleSubCommandStart(executingPlayer);
			}
			else {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
			}
		}
	}
	
	private void handleSubCommandCreate(Player executingPlayer) {
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.UNINITIALIZED) {
			GrubsLaserTag.createNewGame();
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"New game ready for setup. Add players next."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR, 
				"Can't create a new game right now."
			);
		}
	}
	
	private void handleSubCommandPlayers(String[] args, Player executingPlayer) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}
		
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_PLAYERS ||
			GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_TIME_LIMIT) {
			
			ArrayList<Player> playersToAdd = new ArrayList<Player>(10);
							
			for (int i = 1, len = args.length; i < len; ++i) {
				List<Player> matches = Bukkit.matchPlayer(args[i]);
				
				if (matches.size() == 0) {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No players matching '" + args[i] + "'."
					);
				}
				else if (matches.size() == 1) {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"Added player " + matches.get(0).getDisplayName() + "."
					);
					playersToAdd.add(matches.get(0));
				}
				else {
					String matchStr = "";
					String separator = "";
					for (int m = 0, matchNum = matches.size(); m < matchNum; ++m) {
						matchStr = separator + matches.get(m);
						if (m == 0) {
							separator = ", ";
						}
					}
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INQUIRY, 
						"Matches for '" + args[i] + "': " + matchStr
					);
				}
			}
			
			if (playersToAdd.size() > 0) {
				int added = GrubsLaserTag.setPlayers(playersToAdd.toArray(new Player[1]));
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO, 
					"Added " + added + " players."
				);
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO, 
					"You can add more players or set time next."
				);
			}
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Current game not accepting players."
			);
		}
	}
	
	private void handleSubCommandTime(String[] args, Player executingPlayer) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}
		
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_TIME_LIMIT) {
			int minutesToSet = Integer.parseInt(args[1]);
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO, 
				"Setting game length to " + minutesToSet + " minutes."
			);
			GrubsLaserTag.setGameLength(minutesToSet);
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO,
				"Move to the restart point and set restart next."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR, 
				"Current game not accepting time limit."
			);
		}
	}
	
	private void handleSubCommandRestartPoint(Player executingPlayer) {
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_ELIMINATION_LOCATION) {
			GrubsLaserTag.setEliminationLocation(executingPlayer.getLocation());
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO, 
				"Game setup complete. Ready to start."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				"Current game not accepting an restart location."
			);
		}
	}
	
	private void handleSubCommandStart(Player executingPlayer) {
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.READY_TO_START) {
			GrubsLaserTag.start();
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Can't start a new game.");
		}
	}
}
