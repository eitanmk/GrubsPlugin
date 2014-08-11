package com.selfequalsthis.grubsplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.SimplePluginManager;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class GrubsCommandManager {
	private static GrubsCommandManager instance = null;

	private CommandMap commandMap = null;
	private Map<String,Command> knownCommands = null;

	public static GrubsCommandManager getInstance() {
		if (instance == null) {
			instance = new GrubsCommandManager();
		}

		return instance;
	}

	@SuppressWarnings("unchecked")
	public GrubsCommandManager() {
		try{
			Field map = SimplePluginManager.class.getDeclaredField("commandMap");
			map.setAccessible(true);
			this.commandMap = (CommandMap)map.get(Bukkit.getPluginManager());

			Field known = SimpleCommandMap.class.getDeclaredField("knownCommands");
			known.setAccessible(true);
			this.knownCommands = (Map<String,Command>)known.get(this.commandMap);
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void registerCommand(String name, CommandExecutor exec, String desc, String usage) {
		GrubsCommand cmd = new GrubsCommand(name);
		this.commandMap.register("Grubs", cmd);
		cmd.setExecutor(exec);
		cmd.setDescription(desc);
		cmd.setUsage(usage);
	}

	public void unregisterCommand(String name) {
		Command cmd = this.commandMap.getCommand(name);
		if (cmd != null) {
			cmd.unregister(this.commandMap);
			this.knownCommands.remove(name);
		}
	}

	public void registerCommands(AbstractGrubsCommandHandler executor) {
		HashMap<String,Method> commandData = this.getCommandData(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			Method method = commandData.get(command);
			executor.handlers.put(command.toLowerCase(), method);

			GrubsCommandHandler eh = method.getAnnotation(GrubsCommandHandler.class);
			this.registerCommand(command, executor, eh.desc(), eh.usage());
		}
	}

	public void unregisterCommands(AbstractGrubsCommandHandler executor) {
		HashMap<String,Method> commandData = this.getCommandData(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			this.unregisterCommand(command);
		}
	}

	public HashMap<String,Method> getCommandData(AbstractGrubsCommandHandler executor) {
		HashMap<String,Method> ret = new HashMap<String,Method>();

		Method[] methods;
		try {
			methods = executor.getClass().getDeclaredMethods();
		}
		catch (NoClassDefFoundError e) {
			return null;
		}

		for (int i = 0; i < methods.length; i++) {
			Method method = methods[i];
			GrubsCommandHandler eh = method.getAnnotation(GrubsCommandHandler.class);
			if (eh == null) continue;
			String command = eh.command();
			ret.put(command, method);
		}

		return ret;
	}

}
