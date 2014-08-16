package com.selfequalsthis.grubsplugin.modules.defendtheshed;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class DefendShedCommandHandlers extends AbstractGrubsCommandHandler {

	private final Logger log = Logger.getLogger("Minecraft");

	public DefendShedCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@GrubsCommandHandler(
		command = "defendshed",
		desc = "Used to setup new games of Defend the Shed.",
		usage = "/<command> create|players|target|time|spawn|start"
	)
	public void onDefendshedCommand(GrubsCommandInfo cmd) {
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
			else if (subcommand.equalsIgnoreCase("it")) {
				this.handleSubCommandItPlayer(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("target")) {
				this.handleSubCommandTarget(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("time")) {
				this.handleSubCommandTime(args, executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("spawn")) {
				this.handleSubCommandSpawnPoint(executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("start")) {
				this.handleSubCommandStart(executingPlayer);
			}
			else if (subcommand.equalsIgnoreCase("cancel")) {
				GrubsDefendShed.gameCancelled();
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Game cancelled.");
			}
			else {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
			}
		}
	}

	private void handleSubCommandCreate(Player executingPlayer) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.UNINITIALIZED) {
			GrubsDefendShed.createNewGame();
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"New game ready for setup. Add players next: /defendshed players <name1> [...<nameN>]"
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

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_PLAYERS ||
				GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_IT_PLAYER) {

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
				int added = GrubsDefendShed.setPlayers(playersToAdd.toArray(new Player[1]));
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO,
					"Added " + added + " players."
				);
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO,
					"You can add more players or set who is 'it' next: /defendshed it <player>"
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

	private void handleSubCommandItPlayer(String[] args, Player executingPlayer) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}

		String playerName = args[1];

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_IT_PLAYER) {
			List<Player> matches = Bukkit.matchPlayer(playerName);

			if (matches.size() == 0) {
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.ERROR,
					"No players matching '" + playerName + "'."
				);
			}
			else if (matches.size() == 1) {
				Player p = matches.get(0);
				if (GrubsDefendShed.isPlaying(p)) {
					GrubsDefendShed.setItPlayer(p);
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"" + p.getDisplayName() + " is 'it'. Put target button in crosshairs: /defendshed target"
					);
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"Player " + p.getDisplayName() + " isn't currently added to this game and can't be set as 'it'."
					);
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.ERROR,
					"Ambiguous player name. Use exact, full player name."
				);
			}
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.ERROR,
				"Current game in incorrect state for setting 'it' player."
			);
		}
	}

	private void handleSubCommandTarget(String[] args, Player executingPlayer) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_TARGET) {
			Block block = executingPlayer.getTargetBlock(null, 256);
			Material target = block.getType();
			log.info(target.toString());
			if (target == Material.STONE_BUTTON || target == Material.WOOD_BUTTON) {
				GrubsDefendShed.setTargetButton(block);
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.INFO,
					"Target set. Set game time limit next: /defendshed time <minutes>"
				);
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.ERROR,
					"Target must be a wood or stone button."
				);
			}
		}
	}


	private void handleSubCommandTime(String[] args, Player executingPlayer) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_TIME_LIMIT) {
			int minutesToSet = Integer.parseInt(args[1]);
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Setting game length to " + minutesToSet + " minutes."
			);
			GrubsDefendShed.setGameLength(minutesToSet);
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Move to the spawn point and set spawn next: /defendshed spawn"
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

	private void handleSubCommandSpawnPoint(Player executingPlayer) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_SPAWN_LOCATION) {
			GrubsDefendShed.setSpawnLocation(executingPlayer.getLocation());
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Game setup complete. Ready to start: /defendshed start"
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.ERROR,
				"Current game not accepting a spawn location."
			);
		}
	}

	private void handleSubCommandStart(Player executingPlayer) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.READY_TO_START) {
			GrubsDefendShed.start();
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Can't start a new game.");
		}
	}

}
