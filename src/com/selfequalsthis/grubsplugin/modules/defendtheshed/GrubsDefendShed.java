package com.selfequalsthis.grubsplugin.modules.defendtheshed;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class GrubsDefendShed {

	private static Player itPlayer = null;
	private static ArrayList<Player> playerList = new ArrayList<Player>(10);
	private static int gameLengthMinutes = 0;
	private static Block targetButton = null;
	private static Location spawnLocation = null;

	private static Timer gameTimer;
	private static TimerTask countInTask;
	private static TimerTask gameDurationTask;

	public static enum GAME_STATES {
		UNINITIALIZED,
		ACCEPT_PLAYERS,
		ACCEPT_TARGET,
		ACCEPT_SPAWN_LOCATION,
		ACCEPT_TIME_LIMIT,
		READY_TO_START,
		IN_PROGRESS
	}
	private static GAME_STATES currentState = GAME_STATES.UNINITIALIZED;

	public static GAME_STATES getGameState() {
		return currentState;
	}

	public static void createNewGame(Player p) {
		itPlayer = p;
		playerList.add(p);

		currentState = GAME_STATES.ACCEPT_PLAYERS;
	}

	public static boolean isItPlayer(Player p) {
		return itPlayer.equals(p);
	}

	public static boolean isPlaying(Player p) {
		return playerList.contains(p);
	}

	public static int setPlayers(Player[] players) {
		for (Player p : players) {
			playerList.add(p);
		}

		currentState = GAME_STATES.ACCEPT_TARGET;

		return players.length;
	}

	public static void setTargetButton(Block button) {
		targetButton = button;

		currentState = GAME_STATES.ACCEPT_TIME_LIMIT;
	}

	public static boolean isTargetButton(Block button) {
		return button.equals(targetButton);
	}

	public static void setGameLength(int mins) {
		gameLengthMinutes = mins;

		currentState = GAME_STATES.ACCEPT_SPAWN_LOCATION;
	}

	public static void setSpawnLocation(Location loc) {
		spawnLocation = loc;

		currentState = GAME_STATES.READY_TO_START;
	}

	public static void start() {
		resetTimerTasks();

		for (Player p : playerList) {
			teleportToRestartPoint(p);
		}

		gameTimer.schedule(countInTask, 3000L, 1000L);
	}

	public static void teleportToRestartPoint(Player p) {
		p.teleport(spawnLocation);
	}

	private static void startGameTimers() {
		currentState = GAME_STATES.IN_PROGRESS;
		gameTimer.schedule(gameDurationTask, (gameLengthMinutes * 60 * 1000));
	}

	public static void completeGame() {
		gameTimer.cancel();

		for (Player p : playerList) {
			GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.MONITOR, "Game over.");
		}

		currentState = GAME_STATES.UNINITIALIZED;
		resetGameVariables();
	}

	private static void resetGameVariables() {
		itPlayer = null;
		playerList.clear();
		gameLengthMinutes = 0;
		spawnLocation = null;
		targetButton = null;
	}

	private static void resetTimerTasks() {
		gameTimer = new Timer();

		countInTask = new TimerTask() {
			private int count = 15;

			public void run() {
				if (count > 0) {
					for (Player p : GrubsDefendShed.playerList) {
						GrubsMessager.sendMessage(
							p,
							GrubsMessager.MessageLevel.MONITOR,
							"Game starting in " + count + " seconds."
						);
					}

					count--;
				}
				else {
					for (Player p : GrubsDefendShed.playerList) {
						GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.MONITOR, "GO!");
					}

					this.cancel();

					GrubsDefendShed.startGameTimers();
				}
			}
		};

		gameDurationTask = new TimerTask() {
			public void run() {
				GrubsDefendShed.completeGame();
			}
		};

	}
}
