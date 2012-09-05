package com.selfequalsthis.grubsplugin.modules.lasertag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.GrubsMessager;

public class GrubsLaserTag {
	
	private static ArrayList<Player> playerList = new ArrayList<Player>(10);
	private static int gameLengthMinutes = 0;
	private static Location eliminationLocation = null;
	
	private static Object lockObj = new Object();
	private static HashMap<String,Integer> gameScores = new HashMap<String,Integer>();
	private static HashMap<String,Integer> hitCounts = new HashMap<String,Integer>();
	
	private static Timer gameTimer;
	private static TimerTask countInTask;
	private static TimerTask gameDurationTask;
	private static TimerTask scoreUpdateTask;
	
	public static enum GAME_STATES {
		UNINITIALIZED,
		ACCEPT_PLAYERS,
		ACCEPT_ELIMINATION_LOCATION,
		ACCEPT_TIME_LIMIT,
		READY_TO_START,
		IN_PROGRESS
	}
	private static GAME_STATES currentState = GAME_STATES.UNINITIALIZED; 
	
	public static GAME_STATES getGameState() {
		return currentState;
	}
	
	public static void createNewGame() {
		currentState = GAME_STATES.ACCEPT_PLAYERS;
	}
	
	public static boolean isPlaying(Player p) {
		return playerList.contains(p);
	}
	
	public static int setPlayers(Player[] players) {
		for (Player p : players) {
			playerList.add(p);
		}
		
		currentState = GAME_STATES.ACCEPT_TIME_LIMIT;
		
		return playerList.size();
	}
	
	public static void setGameLength(int mins) {
		gameLengthMinutes = mins;
		
		currentState = GAME_STATES.ACCEPT_ELIMINATION_LOCATION;
	}

	public static void setEliminationLocation(Location loc) {
		eliminationLocation = loc;
		
		currentState = GAME_STATES.READY_TO_START;
	}
	
	public static void start() {		
		resetTimerTasks();	
		
		for (Player p : playerList) {
			gameScores.put(p.getDisplayName(), 0);
			hitCounts.put(p.getDisplayName(), 0);
			teleportToRestartPoint(p);
		}
		
		gameTimer.schedule(countInTask, 3000L, 1000L);
	}
	
	private static void teleportToRestartPoint(Player p) {
		p.teleport(eliminationLocation);
	}
	
	private static void startGameTimers() {
		currentState = GAME_STATES.IN_PROGRESS;
		gameTimer.schedule(gameDurationTask, (gameLengthMinutes * 60 * 1000));
		gameTimer.schedule(scoreUpdateTask, 15000, 15000);
	}
	
	private static void showScoreUpdate() {

		String leaderString = getLeaderString();
		
		synchronized(lockObj) {
			for (Player p : playerList) {
				String updateStr = "Score: " + gameScores.get(p.getDisplayName()) + ", " + leaderString;
				GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.PLAIN, updateStr);
			}
		}
	}
	
	private static String getLeaderString() {
		Entry<String,Integer> maxEntry = null;
		String retString = "";
		
		synchronized(lockObj) {
			for (Entry<String,Integer> entry : gameScores.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
			
			retString = "Leader: " + maxEntry.getKey() + " (" + maxEntry.getValue() + ")";
		}
		
		return retString;
	}
	
	private static void completeGame() {
		for (Player p : playerList) {
			GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.MONITOR, "Game over.");
			teleportToRestartPoint(p);
		}
		
		printScoreTable();
		
		currentState = GAME_STATES.UNINITIALIZED;
		resetGameVariables();
	}
	
	public static void updateScore(Player shooter, Player receiver) {
		int shooterScore, receiverScore, receiverHitCount;
		
		String shooterName = shooter.getDisplayName();
		String receiverName = receiver.getDisplayName();
		
		synchronized(lockObj) {
			shooterScore = gameScores.get(shooterName);
			receiverScore = gameScores.get(receiverName);
			gameScores.put(shooterName, shooterScore + 5);
			gameScores.put(receiverName, receiverScore - 2);
		}
		
		receiverHitCount = hitCounts.get(receiverName);
		if ( (receiverHitCount + 1) == 10 ) {
			hitCounts.put(receiverName, 0);
			teleportToRestartPoint(receiver);
		}
		else {
			hitCounts.put(receiverName, receiverHitCount + 1);
		}
	}
	
	private static void resetGameVariables() {
		playerList.clear();
		gameLengthMinutes = 0;
		eliminationLocation = null;
		gameScores.clear();
		hitCounts.clear();
	}
	
	private static void resetTimerTasks() {
		gameTimer = new Timer();
		
		countInTask = new TimerTask() {
			private int count = 15;
			
			public void run() {
				if (count > 0) {
					for (Player p : GrubsLaserTag.playerList) {
						GrubsMessager.sendMessage(
							p,
							GrubsMessager.MessageLevel.MONITOR,
							"Game starting in " + count + " seconds."
						);
					}
					
					count--;
				}			
				else {
					for (Player p : GrubsLaserTag.playerList) {
						GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.MONITOR, "GO!");
					}
					
					this.cancel();
					
					GrubsLaserTag.startGameTimers();
				}
			}
		};
		
		gameDurationTask = new TimerTask() {
			public void run() {
				GrubsLaserTag.gameTimer.cancel();			
				GrubsLaserTag.completeGame();
			}
		};
		
		scoreUpdateTask = new TimerTask() {
			public void run() {
				GrubsLaserTag.showScoreUpdate();
			}
		};
	}
	
	private static void printScoreTable() {
		ArrayList<String> highScoreList = new ArrayList<String>();
		for (int i = 0, iterations = gameScores.size(); i < iterations; ++i) {
			Entry<String,Integer> maxEntry = null;
			for (Entry<String,Integer> entry : gameScores.entrySet()) {
				if (maxEntry == null || entry.getValue().compareTo(maxEntry.getValue()) > 0) {
					maxEntry = entry;
				}
			}
			gameScores.remove(maxEntry.getKey());
			highScoreList.add(maxEntry.getKey() + ": " + maxEntry.getValue()); 
		}
		
		// print scores
		for (Player p : playerList) {
			GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.PLAIN, "Scores:");
			for (int h = 0, num = highScoreList.size(); h < num; ++h) {
				GrubsMessager.sendMessage(p, GrubsMessager.MessageLevel.PLAIN, "" + (h+1) + ". " + highScoreList.get(h));
			}
		}
	}
}
