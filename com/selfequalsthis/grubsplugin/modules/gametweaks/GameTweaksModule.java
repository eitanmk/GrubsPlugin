package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class GameTweaksModule extends AbstractGrubsModule {
	
	private GameTweaksBlockListener blockListener;
	private GameTweaksPlayerListener playerListener;
	private GameTweaksEntityListener entityListener;
	
	private boolean obsidianBuildModeEnabled = false;
	
	public GameTweaksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameTweaksModule]: ";
		this.blockListener = new GameTweaksBlockListener(this);
		this.playerListener = new GameTweaksPlayerListener();
		this.entityListener = new GameTweaksEntityListener();
	}
	
	@Override
	public void enable() {		
		this.registerCommand("obm");
		this.registerEvent(Event.Type.BLOCK_BURN, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.BLOCK_IGNITE, this.blockListener, Priority.Monitor);
		this.registerEvent(Event.Type.BLOCK_PLACE, this.blockListener, Priority.Monitor);
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
		
		if (cmdName.equalsIgnoreCase("obm")) {
			if (args.length > 0) {
				if ( !args[0].equalsIgnoreCase("on") && !args[0].equalsIgnoreCase("off")) {
					executingPlayer.sendMessage(ChatColor.RED + "[Obsidian] Invalid argument.");
					return false;
				}
				else {
					obsidianBuildModeEnabled = args[0].equalsIgnoreCase("on");
					executingPlayer.sendMessage(ChatColor.GREEN + "[Obsidian] Build mode is " + args[0] + ".");
					
					return true;
				}
			}
			else {
				executingPlayer.sendMessage(ChatColor.RED + "[Obsidian] Missing command argument.");
				return false;
			}
		}
		
		return false;
	}
	
	public boolean isObsidianModeEnabled() {
		return obsidianBuildModeEnabled;
	}

}
