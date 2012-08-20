package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractGrubsModule implements CommandExecutor {
	
	protected final Logger logger = Logger.getLogger("Minecraft");
	
	protected JavaPlugin pluginRef = null;
	protected String logPrefix = "";
	protected String dataFileName = null;
	
	public void enable() { }
	public void disable() {	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return false;
	}
	
	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}
	
	protected void registerCommand(String cmdName) {
		PluginCommand commandObj = this.pluginRef.getCommand(cmdName);
		
		if (commandObj == null) {
			this.log("Could not find command '" + cmdName + "' in plugin.yml");
			return;
		}
		
		this.log("Registering command '" + cmdName + "'");
		commandObj.setExecutor(this);
	}
	
	protected void registerEvent(Listener listener) {
		Bukkit.getPluginManager().registerEvents(listener, this.pluginRef);
	}

	public File getDataFile() {
		if (this.dataFileName == null) {
			this.log("No file name set!");
			return null;
		}
		
		File dataFile = new File(this.pluginRef.getDataFolder(), this.dataFileName);
		
		if (!dataFile.exists()) {
			this.log("Data file '" + dataFile.toString() + "' doesn't exist yet. Creating.");
			
			try {
				dataFile.createNewFile();
			} catch (IOException e) {
				this.log("Error creating '" + dataFile.toString() + "'!");
				e.printStackTrace();
				return null;
			}
		}
		
		return dataFile;
	}
}
