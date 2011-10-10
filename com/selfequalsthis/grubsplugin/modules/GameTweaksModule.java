package com.selfequalsthis.grubsplugin.modules;

import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.GrubsModule;

public class GameTweaksModule implements CommandExecutor, GrubsModule {

	private final Logger log = Logger.getLogger("Minecraft");
	private final String logPrefix = "[GameTweaksModule]: ";
	private JavaPlugin pluginRef;
	
	private boolean obsidianBuildModeEnabled = false;
	
	public GameTweaksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
	}
	
	@Override
	public void enable() {
		log.info(logPrefix + "Initializing command handlers.");
		this.pluginRef.getCommand("obm").setExecutor(this);
	}

	@Override
	public void disable() {	}

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
					if (obsidianBuildModeEnabled) {
						executingPlayer.sendMessage(ChatColor.GREEN + "[Obsidian] Build mode is on.");
					}
					else {
						executingPlayer.sendMessage(ChatColor.GREEN + "[Obsidian] Build mode is off.");
					}
					
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

}
