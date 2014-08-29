package com.selfequalsthis.grubsplugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandManager;

public abstract class AbstractGrubsComponent {

	protected final Logger logger = Logger.getLogger("Minecraft");

	protected JavaPlugin pluginRef = null;
	protected String logPrefix = "";
	protected String dataFileName = null;

	protected AbstractGrubsCommandHandler commandHandlers = null;

	// TODO is there a better way to do logging?
	public void log(String msg) {
		this.logger.info(this.logPrefix + msg);
	}

	public void warn(String msg) {
		this.logger.warning(this.logPrefix + msg);
	}

	protected void registerCommands(AbstractGrubsCommandHandler executor) {
		if (executor == null) {
			this.log("Command handler class is null! Don't forget to instantiate it!");
		}

		GrubsCommandManager cmdMgr = GrubsCommandManager.getInstance();
		HashMap<String,Method> commandMapping = cmdMgr.getCommandMethods(executor);

		if (commandMapping == null || commandMapping.size() == 0) {
			return;
		}

		for (String command : commandMapping.keySet()) {
			this.log("Registering command '" + command + "'");

			// validate the command
			Method commandHandler = commandMapping.get(command);
			boolean isCommandPublic = Modifier.isPublic(commandHandler.getModifiers());
			List<Class<?>> cArgTypes = Arrays.asList(commandHandler.getParameterTypes());
			boolean commandHasCorrectArgs = cArgTypes.size() == 4 &&
					cArgTypes.get(0) == CommandSender.class &&
					cArgTypes.get(1) == Command.class &&
					cArgTypes.get(2) == String.class &&
					cArgTypes.get(3) == String[].class;
			boolean commandReturnsBool = commandHandler.getReturnType() == boolean.class;
			if (!isCommandPublic) {
				this.warn("Command handler for '" + command +"' is not public!");
			}
			if (!commandHasCorrectArgs) {
				this.warn("Command handler for '" + command +"' has incorrect argument types!");
			}
			if (!commandReturnsBool) {
				this.warn("Command handler for '" + command +"' doesn't return a boolean!");
			}


			HashMap<String,Method> subcommandMapping = cmdMgr.getSubcommandMethods(executor, command);
			for (String subcommand: subcommandMapping.keySet()) {
				this.log("Registering subcommand '" + subcommand + "' for command '" + command + "'");

				// validate the subcommand
				Method subcommandHandler = subcommandMapping.get(subcommand);
				boolean isSubcommandPublic = Modifier.isPublic(subcommandHandler.getModifiers());
				List<Class<?>> scArgTypes = Arrays.asList(subcommandHandler.getParameterTypes());
				boolean subcommandHasCorrectArgs = scArgTypes.get(0) == Player.class && scArgTypes.get(1) == String[].class;
				boolean subcommandReturnsBool = subcommandHandler.getReturnType() == boolean.class;

				if (!isSubcommandPublic) {
					this.warn("Subcommand handler '" + subcommand + "' for command '" + command +"' is not public!");
				}
				if (!subcommandHasCorrectArgs) {
					this.warn("Subcommand handler '" + subcommand + "' for command '" + command +"' has incorrect argument types!");
				}
				if (!subcommandReturnsBool) {
					this.warn("Subcommand handler '" + subcommand + "' for command '" + command +"' doesn't return a boolean!");
				}
			}
		}

		cmdMgr.registerCommands(executor, this.pluginRef);
	}

	protected void unregisterCommands(AbstractGrubsCommandHandler executor) {
		GrubsCommandManager cmdMgr = GrubsCommandManager.getInstance();
		HashMap<String,Method> commandData = cmdMgr.getCommandMethods(executor);

		if (commandData == null || commandData.size() == 0) {
			return;
		}

		for (String command : commandData.keySet()) {
			this.log("Unregistering command '" + command + "'");
		}

		cmdMgr.unregisterCommands(executor);
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
