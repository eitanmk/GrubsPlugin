package com.selfequalsthis.grubsplugin.modules.lasertag;

import java.util.ArrayList;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class LaserTagCommandHandlers extends AbstractGrubsCommandHandler {

	public LaserTagCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@GrubsCommandHandler(
		command = "lasertag",
		desc = "Used to setup new games of laser tag.",
		usage = "/<command> create|players|time|restart|start"
	)
	public boolean onLasertagCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length == 0) {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
				return false;
			}

			String subcommand = args[0];

			if (subcommand.equalsIgnoreCase("create")) {
				return this.handleSubCommandCreate(executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("players")) {
				return this.handleSubCommandPlayers(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("time")) {
				return this.handleSubCommandTime(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("restart")) {
				return this.handleSubCommandRestartPoint(executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("start")) {
				return this.handleSubCommandStart(executingPlayer);
			}
			else {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
				return false;
			}
		}

		return true;
	}

	private boolean handleSubCommandCreate(Player executingPlayer) {
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

		return true;
	}

	private boolean handleSubCommandPlayers(String[] args, Player executingPlayer) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}

		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_PLAYERS ||
				GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.ACCEPT_TIME_LIMIT) {

			ArrayList<Player> playersToAdd = new ArrayList<Player>(10);

			for (int i = 1, len = args.length; i < len; ++i) {
				Player curPlayer = Bukkit.getPlayer(args[i]);
				if (curPlayer == null) {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"Player '" + args[i] + "' not found."
					);
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"Added player " + curPlayer.getDisplayName() + "."
					);
					playersToAdd.add(curPlayer);
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

		return true;
	}

	private boolean handleSubCommandTime(String[] args, Player executingPlayer) {
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

		return true;
	}

	private boolean handleSubCommandRestartPoint(Player executingPlayer) {
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

		return true;
	}

	private boolean handleSubCommandStart(Player executingPlayer) {
		if (GrubsLaserTag.getGameState() == GrubsLaserTag.GAME_STATES.READY_TO_START) {
			GrubsLaserTag.start();
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Can't start a new game.");
		}

		return true;
	}
}
