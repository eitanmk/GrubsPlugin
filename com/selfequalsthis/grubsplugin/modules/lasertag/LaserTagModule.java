package com.selfequalsthis.grubsplugin.modules.lasertag;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class LaserTagModule extends AbstractGrubsModule {
	
	private LaserTagEntityListener entityListener;
	
	public LaserTagModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[LaserTagModule]: ";
		this.entityListener = new LaserTagEntityListener();
	}
	
	@Override
	public void enable() {
		this.registerCommand("lasertag");
		this.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Priority.Monitor);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("lasertag")) {
			this.handleLasertagCommand(args, executingPlayer);
		}
		
		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));
		
		return true;
	}

	private void handleLasertagCommand(String[] args, Player executingPlayer) {
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
