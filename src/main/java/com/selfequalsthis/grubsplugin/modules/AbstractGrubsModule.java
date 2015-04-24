package com.selfequalsthis.grubsplugin.modules;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;

import org.slf4j.Logger;
import org.spongepowered.api.Game;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandManager;

public abstract class AbstractGrubsModule {

	protected Logger logger = null;
	protected GrubsPlugin pluginRef = null;
	protected Game game = null;
	protected String logPrefix = "";
	protected String dataFileName = null;

	public void enable() { }
	public void disable() {	}

	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}

	protected void registerCommands(AbstractGrubsCommandHandler executor) {
		if (executor == null) {
			this.log("Command handler class is null! Don't forget to instantiate it!");
		}

		GrubsCommandManager cmdMgr = GrubsCommandManager.getInstance(this.pluginRef, this.game);
		HashMap<String,Method> commandData = cmdMgr.getCommandData(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			this.log("Registering command '" + command + "'");
		}

		cmdMgr.registerCommands(executor);
	}

	protected void unregisterCommands(AbstractGrubsCommandHandler executor) {
		GrubsCommandManager cmdMgr = GrubsCommandManager.getInstance(this.pluginRef, this.game);
		HashMap<String,Method> commandData = cmdMgr.getCommandData(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			this.log("Unregistering command '" + command + "'");
		}

		cmdMgr.unregisterCommands(executor);
	}
/*
	protected void registerEventHandlers(Listener listener) {
		if (listener == null) {
			this.log("Event listener class is null! Don't forget to instantiate it!");
		}

		ArrayList<String> listenerData = this.getEventListenerData(listener);

		if (listenerData == null || listenerData.size() == 0) {
			return;
		}

		for (String data : listenerData) {
			this.log("Listening to " + data);
		}

		Bukkit.getPluginManager().registerEvents(listener, this.pluginRef);
	}

	protected void unregisterEventHandlers(Listener listener) {
		ArrayList<String> listenerData = this.getEventListenerData(listener);

		if (listenerData == null || listenerData.size() == 0) {
			return;
		}

		for (String data : listenerData) {
			this.log("Stopped listening to " + data);
		}

		HandlerList.unregisterAll(listener);
	}
	*/

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

	/*
	private ArrayList<String> getEventListenerData(Listener listener) {
		ArrayList<String> ret = new ArrayList<String>();

		Method[] methods;
		try {
			methods = listener.getClass().getDeclaredMethods();
		}
		catch (NoClassDefFoundError e) {
			this.log("Could not find listener class: " + listener.getClass());
			return null;
		}

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			EventHandler eh = method.getAnnotation(EventHandler.class);
			if (eh == null) continue;
			Class<?> checkClass = method.getParameterTypes()[0];
			String eventClassName = checkClass.getName();
			String eventType = eventClassName.substring(eventClassName.lastIndexOf(".") + 1);
			String eventPriority = eh.priority().toString();
			ret.add("" + eventType + " (" + eventPriority + ")");
		}

		return ret;
	}
	*/

}
