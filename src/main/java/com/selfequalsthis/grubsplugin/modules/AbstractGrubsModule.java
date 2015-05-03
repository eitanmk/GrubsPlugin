package com.selfequalsthis.grubsplugin.modules;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.util.command.CommandMapping;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.google.common.base.Optional;
import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public abstract class AbstractGrubsModule {

	protected Logger logger = null;
	protected GrubsPlugin pluginRef = null;
	protected Game game = null;
	protected String logPrefix = "";
	protected String dataFileName = null;
	protected AbstractGrubsCommandHandlers commandHandlers = null;

	public abstract void enable();

	public abstract void disable();

	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}

	protected void registerCommands(AbstractGrubsCommandHandlers commandHandlers) {

		HashMap<String,CommandSpec> commands = commandHandlers.getCommands();

		if (commands.isEmpty()) {
			this.log("No commands to register.");
			return;
		}

		for (String commandName : commands.keySet()) {
			this.log("Registering command '" + commandName + "'");
			this.game.getCommandDispatcher().register(this.pluginRef, commands.get(commandName), commandName);
		}
	}

	protected void unregisterCommands(AbstractGrubsCommandHandlers commandHandlers) {

		HashMap<String,CommandSpec> commands = commandHandlers.getCommands();

		if (commands.isEmpty()) {
			this.log("No commands to unregister.");
			return;
		}

		for (String commandName : commands.keySet()) {
			Optional<? extends CommandMapping> optCmdMap = this.game.getCommandDispatcher().get(commandName);
			if (optCmdMap.isPresent()) {
				this.log("Unregistering command '" + commandName + "'");
				this.game.getCommandDispatcher().removeMapping(optCmdMap.get());
			}
		}
	}

	/*
	 * protected void registerEventHandlers(Listener listener) { if (listener ==
	 * null) {
	 * this.log("Event listener class is null! Don't forget to instantiate it!"
	 * ); }
	 *
	 * ArrayList<String> listenerData = this.getEventListenerData(listener);
	 *
	 * if (listenerData == null || listenerData.size() == 0) { return; }
	 *
	 * for (String data : listenerData) { this.log("Listening to " + data); }
	 *
	 * Bukkit.getPluginManager().registerEvents(listener, this.pluginRef); }
	 *
	 * protected void unregisterEventHandlers(Listener listener) {
	 * ArrayList<String> listenerData = this.getEventListenerData(listener);
	 *
	 * if (listenerData == null || listenerData.size() == 0) { return; }
	 *
	 * for (String data : listenerData) { this.log("Stopped listening to " +
	 * data); }
	 *
	 * HandlerList.unregisterAll(listener); }
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
	 * private ArrayList<String> getEventListenerData(Listener listener) {
	 * ArrayList<String> ret = new ArrayList<String>();
	 *
	 * Method[] methods; try { methods =
	 * listener.getClass().getDeclaredMethods(); } catch (NoClassDefFoundError
	 * e) { this.log("Could not find listener class: " + listener.getClass());
	 * return null; }
	 *
	 * for (int i = 0; i < methods.length; i++) { Method method = methods[i];
	 * EventHandler eh = method.getAnnotation(EventHandler.class); if (eh ==
	 * null) continue; Class<?> checkClass = method.getParameterTypes()[0];
	 * String eventClassName = checkClass.getName(); String eventType =
	 * eventClassName.substring(eventClassName.lastIndexOf(".") + 1); String
	 * eventPriority = eh.priority().toString(); ret.add("" + eventType + " (" +
	 * eventPriority + ")"); }
	 *
	 * return ret; }
	 */

}
