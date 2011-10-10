package com.selfequalsthis.grubsplugin.modules.lasertag;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.IGrubsModule;

public class LaserTagModule implements CommandExecutor, IGrubsModule {

	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[LaserTagModule]: ";
	private JavaPlugin pluginRef;
	
	private LaserTagEntityListener entityListener;
	
	public LaserTagModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.entityListener = new LaserTagEntityListener();
	}
	
	@Override
	public void enable() {
		log.info(logPrefix + "Initializing event listeners.");
		PluginManager pm = this.pluginRef.getServer().getPluginManager();
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Priority.Monitor, this.pluginRef);

		log.info(logPrefix + "Initializing command handlers.");
		this.pluginRef.getCommand("lasertag").setExecutor(this);
	}

	@Override
	public void disable() {	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (!cmdName.equalsIgnoreCase("lasertag")) {
			return false;
		}
		
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
					List<Player> matches = this.pluginRef.getServer().matchPlayer(args[i]);
					
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
