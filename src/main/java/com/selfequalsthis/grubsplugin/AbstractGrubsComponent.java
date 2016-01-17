package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public abstract class AbstractGrubsComponent {

	protected Logger logger = null;
	protected GrubsPlugin pluginRef = null;
	protected Game game = null;
	protected String logPrefix = "";
	protected String dataFileName = null;
	
	protected AbstractGrubsCommandHandlers commandHandlers = null;
	
	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}

	public Game getGame() {
		return this.game;
	}

	protected void registerCommands(AbstractGrubsCommandHandlers commandHandlers) {

		HashMap<String,CommandSpec> commands = commandHandlers.getCommands();

		if (commands.isEmpty()) {
			this.log("No commands to register.");
			return;
		}

		for (String commandName : commands.keySet()) {
			this.log("Registering command '" + commandName + "'");
			this.game.getCommandManager().register(this.pluginRef, commands.get(commandName), commandName);
		}
	}

	protected void unregisterCommands(AbstractGrubsCommandHandlers commandHandlers) {

		HashMap<String,CommandSpec> commands = commandHandlers.getCommands();

		if (commands.isEmpty()) {
			this.log("No commands to unregister.");
			return;
		}

		for (String commandName : commands.keySet()) {
			Optional<? extends CommandMapping> optCmdMap = this.game.getCommandManager().get(commandName);
			if (optCmdMap.isPresent()) {
				this.log("Unregistering command '" + commandName + "'");
				this.game.getCommandManager().removeMapping(optCmdMap.get());
			}
		}
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
