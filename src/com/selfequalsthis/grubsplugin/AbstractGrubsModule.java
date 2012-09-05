package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractGrubsModule {
	
	protected final Logger logger = Logger.getLogger("Minecraft");
	
	protected JavaPlugin pluginRef = null;
	protected String logPrefix = "";
	protected String dataFileName = null;
	
	public void enable() { }
	public void disable() {	}
	
	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}
	
	protected void registerCommands(AbstractGrubsCommandHandler executor) {
		Method[] methods;
		try {
			methods = executor.getClass().getDeclaredMethods();
		}
		catch (NoClassDefFoundError e) {
			this.log("Could not find command executor class: " + executor.getClass());
			return;
		}
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			GrubsCommandHandler eh = method.getAnnotation(GrubsCommandHandler.class);
			if (eh == null) continue;
			String command = eh.command();
			
			executor.handlers.put(command.toLowerCase(), method);	
			
			PluginCommand commandObj = this.pluginRef.getCommand(command);
			if (commandObj == null) {
				this.log("Could not find command '" + command + "' in plugin.yml");
				return;
			}
			this.log("Registering command '" + command + "'");
			commandObj.setExecutor(executor);
		}
	}
	
	protected void registerEventHandlers(Listener listener) {
		Method[] methods;
		try {
			methods = listener.getClass().getDeclaredMethods();
		}
		catch (NoClassDefFoundError e) {
			this.log("Could not find listener class: " + listener.getClass());
			return;
		}
		
		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null) continue;
			Class<?> checkClass = method.getParameterTypes()[0];
			String eventClassName = checkClass.getName();
			String eventType = eventClassName.substring(eventClassName.lastIndexOf(".") + 1);
			String eventPriority = eh.priority().toString();
			this.log("Listening to " + eventType + " (" + eventPriority + ")");
		}
		
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
