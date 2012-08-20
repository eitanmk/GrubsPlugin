package com.selfequalsthis.grubsplugin.modules.gamefixes;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class GameFixesModule extends AbstractGrubsModule {
	
	private GameFixesPlayerListener playerListner;
	
	public GameFixesModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameFixesModule]: ";
		this.playerListner = new GameFixesPlayerListener();
	}
	
	@Override
	public void enable() {		
		this.registerCommand("eject");
		this.registerCommand("getchunks");
		this.registerEvent(playerListner);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		if (cmdName.equalsIgnoreCase("eject")) {
			this.handleEject(executingPlayer);
			return true;
		}
		else if (cmdName.equalsIgnoreCase("getchunks")) {
			this.handleGetChunks(executingPlayer);
			return true;
		}

		return false;
	}
	
	private void handleEject(Player executingPlayer) {
		if (executingPlayer.isInsideVehicle()) {
			executingPlayer.leaveVehicle();
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "You are not in a vehicle.");
		}
	}
	
	private void handleGetChunks(Player executingPlayer) {
		World world = executingPlayer.getWorld();
		Chunk playerChunk = world.getChunkAt(executingPlayer.getLocation());

		int playerChunkX = playerChunk.getX();
		int playerChunkZ = playerChunk.getZ();

		int startX = playerChunkX - 1;
		int startZ = playerChunkZ - 1;
		int endX  = playerChunkX + 1;
		int endZ  = playerChunkZ + 1;

		for (int x = startX; x <= endX; ++x) {
		    for (int z = startZ; z <= endZ; ++z) {
		        world.refreshChunk(x, z);
		    }
		}
		
		GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Chunks re-sent.");
	}

}
