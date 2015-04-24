package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.command.CommandService;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandMapping;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;


public class GrubsCommandManager {
	private static GrubsCommandManager instance = null;

	private GrubsPlugin pluginRef;
	private CommandService cmdService;
	
	public static GrubsCommandManager getInstance(GrubsPlugin plugin, Game game) {
		if (instance == null) {
			instance = new GrubsCommandManager(plugin, game);
		}

		return instance;
	}

	private GrubsCommandManager(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.cmdService = game.getCommandDispatcher();
	}
	
	private void registerCommand(String name, CommandExecutor exec, String desc, String usage) {
		CommandSpec cmdSpec = CommandSpec.builder()
				.setDescription(Texts.of(desc))
				.setExecutor(exec)
				.build();
		
		this.cmdService.register(this.pluginRef, cmdSpec, name);
	}

	private void unregisterCommand(String name) {
		CommandMapping cmdMap = (CommandMapping) this.cmdService.get(name);
		this.cmdService.removeMapping(cmdMap);
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
