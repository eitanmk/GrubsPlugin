package com.selfequalsthis.grubsplugin;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public interface GrubsCommandHandler {
	
	public boolean processCommand(Server server, Player executingPlayer, String cmdName, String[] args);
	
}
