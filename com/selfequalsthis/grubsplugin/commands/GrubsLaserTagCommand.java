package com.selfequalsthis.grubsplugin.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityListener;

import com.selfequalsthis.grubsplugin.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsLaserTag;

public class GrubsLaserTagCommand extends EntityListener implements GrubsCommandHandler {

	private static GrubsLaserTagCommand instance = null;
	private GrubsLaserTagCommand() { }
	public static GrubsCommandHandler getInstance() {
		if (instance == null) {
			instance = new GrubsLaserTagCommand();
		}

		return instance;
	}
	
	@Override
	public boolean processCommand(Server server, Player executingPlayer, String cmdName, String[] args) {

		if (args.length == 0) {
			executingPlayer.sendMessage(ChatColor.RED + "Not enough arguments.");
			return true;
		}
		
		String subcommand = args[0];
		
		if (subcommand.equalsIgnoreCase("create")) {
			if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.UNINITIALIZED) {
				GrubsLaserTag.createNewGame();
				executingPlayer.sendMessage(ChatColor.GREEN + "New game ready for setup. Add players next.");
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "Can't create a new game right now.");
			}
			return true;
		}
		
		if (subcommand.equalsIgnoreCase("players")) {
			if (args.length == 1) {
				executingPlayer.sendMessage(ChatColor.RED + "Not enough arguments.");
				return true;
			}
			
			if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_PLAYERS ||
				GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_TIME_LIMIT) {
				
				ArrayList<Player> playersToAdd = new ArrayList<Player>(10);
								
				for (int i = 1, len = args.length; i < len; ++i) {
					List<Player> matches = server.matchPlayer(args[i]);
					
					if (matches.size() == 0) {
						executingPlayer.sendMessage(ChatColor.RED + "No players matching '" + args[i] + "'.");
					}
					else if (matches.size() == 1) {
						executingPlayer.sendMessage(ChatColor.GREEN + "Added player " + matches.get(0).getDisplayName() + ".");
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
						executingPlayer.sendMessage(ChatColor.YELLOW + "Matches for '" + args[i] + "': " + matchStr);
					}
				}
				
				if (playersToAdd.size() > 0) {
					int added = GrubsLaserTag.setPlayers(playersToAdd.toArray(new Player[1]));
					executingPlayer.sendMessage(ChatColor.GREEN + "Added " + added + " players.");
					executingPlayer.sendMessage(ChatColor.GREEN + "You can add more players or set time next.");
				}
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "Current game not accepting players.");
			}
			
			return true;
		}
		
		if (subcommand.equalsIgnoreCase("time")) {
			if (args.length == 1) {
				executingPlayer.sendMessage(ChatColor.RED + "Not enough arguments.");
				return true;
			}
			
			if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_TIME_LIMIT) {
				int minutesToSet = Integer.parseInt(args[1]);
				executingPlayer.sendMessage(ChatColor.GREEN + "Setting game length to " + minutesToSet + " minutes.");
				GrubsLaserTag.setGameLength(minutesToSet);
				executingPlayer.sendMessage(ChatColor.GREEN + "Move to the restart point and set restart next.");
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "Current game not accepting time limit.");
			}
			
			return true;
		}
		
		if (subcommand.equalsIgnoreCase("restart")) {
			if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_ELIMINATION_LOCATION) {
				GrubsLaserTag.setEliminationLocation(executingPlayer.getLocation());
				executingPlayer.sendMessage(ChatColor.GREEN + "Game setup complete. Ready to start.");
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "Current game not accepting an restart location.");
			}
			
			return true;
		}	
		
		if (subcommand.equalsIgnoreCase("start")) {
			if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.READY_TO_START) {
				GrubsLaserTag.start();
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "Can't start a new game.");
			}
			
			return true;
		}
		
		return false;
	}

}
