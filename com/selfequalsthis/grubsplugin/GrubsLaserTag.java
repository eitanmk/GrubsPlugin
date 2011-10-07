package com.selfequalsthis.grubsplugin;

public class GrubsLaserTag {
	
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

	/*
	 * list of involved players
	 * timer for game updates
	 * scores for each player
	 * game status enum
	 * game status var
	 * eliminate location reference
	 * 
	 * createNewGame()
	 * 
	 * setPlayers(Players[])
	 * 
	 * setEliminateDestination(Location)
	 * 
	 * setTimeLimit(minutes)
	 * 
	 * start()
	 * 
	 * setupListeners()
	 * removeListeners()
	 * 
	 * 
	 */
}
