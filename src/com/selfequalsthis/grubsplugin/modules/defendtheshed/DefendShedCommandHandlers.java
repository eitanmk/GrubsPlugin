package com.selfequalsthis.grubsplugin.modules.defendtheshed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.annotations.GrubsSubcommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class DefendShedCommandHandlers extends AbstractGrubsCommandHandler {

	public DefendShedCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		ArrayList<String> retVals = null;
		ArrayList<String> argsList = new ArrayList<String>(Arrays.asList(args));

		if (argsList.size() == 1) {
			String subCommand = argsList.get(0);
			retVals = new ArrayList<String>();

			if (subCommand.equalsIgnoreCase("")) {
				switch (GrubsDefendShed.getGameState()) {
					case UNINITIALIZED:
						retVals.add("create");
						break;
					case ACCEPT_PLAYERS:
						retVals.add("players");
						break;
					case ACCEPT_IT_PLAYER:
						retVals.add("players");
						retVals.add("it");
						break;
					case ACCEPT_TARGET:
						retVals.add("target");
						break;
					case ACCEPT_TIME_LIMIT:
						retVals.add("time");
						break;
					case ACCEPT_SPAWN_LOCATION:
						retVals.add("spawn");
						break;
					case READY_TO_START:
						retVals.add("start");
						break;
					default:
						retVals.add("cancel");
				}
			}
			else {
				String[] subCommands = new String[] {
					"cancel", "create",
					"it",
					"players",
					"spawn", "start",
					"target", "time"
				};

				for (int i = 0, len = subCommands.length; i < len; ++i) {
					String cur = subCommands[i];
					if (cur.startsWith(subCommand)) {
						retVals.add(cur);
					}
				}
			}
		}


		return retVals;
	}

	@GrubsCommandHandler(
		command = "defendshed",
		desc = "Used to setup new games of Defend the Shed.",
		subcommands = { "create", "players", "it", "target", "time", "spawn", "start" }
	)
	public boolean onDefendshedCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length == 0) {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			}

			if (! this.invokeSubcommandHandler(command, executingPlayer, args) ) {
				String subcommand = args[0];
				if (subcommand.equalsIgnoreCase("cancel")) {
					GrubsDefendShed.gameCancelled();
					GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Game cancelled.");
				}
				else {
					GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
				}
			}
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "create",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandCreate(Player executingPlayer, String[] args) {
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

		return true;
	}

	@GrubsSubcommandHandler(
		name = "players",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandPlayers(Player executingPlayer, String[] args) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_PLAYERS ||
				GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_IT_PLAYER) {

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

		return true;
	}

	@GrubsSubcommandHandler(
		name = "it",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandItPlayer(Player executingPlayer, String[] args) {
		if (args.length == 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}

		String playerName = args[1];

		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_IT_PLAYER) {
			Player player = Bukkit.getPlayer(playerName);
			if (player == null) {
				GrubsMessager.sendMessage(
					executingPlayer,
					GrubsMessager.MessageLevel.ERROR,
					"Player '" + playerName + "' not found."
				);
			}
			else {
				if (GrubsDefendShed.isPlaying(player)) {
					GrubsDefendShed.setItPlayer(player);
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.INFO,
						"" + player.getDisplayName() + " is 'it'. Put target button in crosshairs: /defendshed target"
					);
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer,
						GrubsMessager.MessageLevel.ERROR,
						"Player " + player.getDisplayName() + " isn't currently added to this game and can't be set as 'it'."
					);
				}
			}
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.ERROR,
				"Current game in incorrect state for setting 'it' player."
			);
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "target",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandTarget(Player executingPlayer, String[] args) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.ACCEPT_TARGET) {
			Block block = executingPlayer.getTargetBlock(null, 256);
			Material target = block.getType();
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

		return true;
	}

	@GrubsSubcommandHandler(
		name = "time",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandTime(Player executingPlayer, String[] args) {
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

		return true;
	}

	@GrubsSubcommandHandler(
		name = "spawn",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandSpawnPoint(Player executingPlayer, String[] args) {
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

		return true;
	}

	@GrubsSubcommandHandler(
		name = "start",
		forCommand = "defendshed"
	)
	public boolean handleSubCommandStart(Player executingPlayer, String[] args) {
		if (GrubsDefendShed.getGameState() == GrubsDefendShed.GAME_STATES.READY_TO_START) {
			GrubsDefendShed.start();
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Can't start a new game.");
		}

		return true;
	}

}
