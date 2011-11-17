package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class GameTweaksModule extends AbstractGrubsModule {
	
	private GameTweaksBlockListener blockListener;
	private GameTweaksPlayerListener playerListener;
	private GameTweaksEntityListener entityListener;
		
	public GameTweaksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameTweaksModule]: ";
		this.blockListener = new GameTweaksBlockListener();
		this.playerListener = new GameTweaksPlayerListener();
		this.entityListener = new GameTweaksEntityListener();
	}
	
	@Override
	public void enable() {		
		this.registerCommand("buildmode");
		this.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.ENTITY_DAMAGE, this.entityListener, Priority.Monitor);
		this.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Priority.Monitor);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		if (cmdName.equalsIgnoreCase("buildmode")) {
			this.handleBuildModeToggle(args, executingPlayer);
		}
		
		return true;
	}
	
	private void handleBuildModeToggle(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			if (args[0].equalsIgnoreCase("on")) {
				executingPlayer.setGameMode(GameMode.CREATIVE);
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Creative build mode enabled.");
			}
			else {
				executingPlayer.setGameMode(GameMode.SURVIVAL);
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Build mode disabled.");
			}
		}
		else {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Missing command argument.");
		}
	}
}
