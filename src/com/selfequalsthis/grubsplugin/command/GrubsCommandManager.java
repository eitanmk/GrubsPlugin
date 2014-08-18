package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.TabExecutor;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.annotations.GrubsSubcommandHandler;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

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

	public void registerCommand(String name, TabExecutor exec, String desc, String usage, Plugin pluginRef) {
		PluginCommand cmd = GrubsCommand.makeCommand(name, pluginRef);
		this.commandMap.register("Grubs", cmd);
		cmd.setExecutor(exec);
		cmd.setTabCompleter(exec);
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

	public void registerCommands(AbstractGrubsCommandHandler executor, Plugin pluginRef) {
		HashMap<String,Method> commandData = this.getCommandMethods(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			Method method = commandData.get(command);
			executor.addCommandMapping(command.toLowerCase(), method);

			GrubsCommandHandler ch = method.getAnnotation(GrubsCommandHandler.class);
			String desc = ch.desc();
			String usage = ch.usage();
			String[] subcommands = ch.subcommands();
			if (subcommands.length > 0) {
				usage = usage + " " + GrubsUtilities.join(subcommands, "|");
			}

			HashMap<String,Method> subcommandData = this.getSubcommandMethods(executor, command);
			if (subcommandData.size() > 0) {
				executor.addSubcommandMappings(command, subcommandData);
			}

			this.registerCommand(command, executor, desc, usage, pluginRef);
		}
	}

	public void unregisterCommands(AbstractGrubsCommandHandler executor) {
		HashMap<String,Method> commandData = this.getCommandMethods(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			this.unregisterCommand(command);
		}
	}

	public HashMap<String,Method> getCommandMethods(AbstractGrubsCommandHandler executor) {
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
			GrubsCommandHandler ch = method.getAnnotation(GrubsCommandHandler.class);
			if (ch == null) continue;
			String command = ch.command();
			ret.put(command, method);
		}

		return ret;
	}

	public HashMap<String,Method> getSubcommandMethods(AbstractGrubsCommandHandler executor, String commandName) {
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
			GrubsSubcommandHandler sch = method.getAnnotation(GrubsSubcommandHandler.class);
			if (sch == null) continue;
			String subCommandName = sch.name();
			String forCommand = sch.forCommand();
			if (forCommand.equalsIgnoreCase(commandName)) {
				ret.put(subCommandName, method);
			}
		}

		return ret;
	}

}
