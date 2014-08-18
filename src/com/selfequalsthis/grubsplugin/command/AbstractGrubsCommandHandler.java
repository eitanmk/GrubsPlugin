package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

public abstract class AbstractGrubsCommandHandler implements TabExecutor {

	protected AbstractGrubsModule moduleRef = null;
	private HashMap<String,Method> commandMap = new HashMap<String,Method>();
	private HashMap<String,HashMap<String,Method>> subcommandMap = new HashMap<String,HashMap<String,Method>>();

	private Method getSubcommandHandler(String commandName, String subcommand) {
		Method handler = null;
		if (this.subcommandMap.containsKey(commandName)) {
			HashMap<String,Method> subcommands = this.subcommandMap.get(commandName);
			if (subcommands.containsKey(subcommand)) {
				handler = subcommands.get(subcommand);
			}
		}
		return handler;
	}

	protected boolean invokeSubcommandHandler(Command command, Player executingPlayer, String[] args) {
		boolean handled = false;

		if (args.length == 0) {
			return handled;
		}

		String commandName = command.getName().toLowerCase();
		String subcommand = args[0];
		Method subcommandHandler = this.getSubcommandHandler(commandName, subcommand);
		if (subcommandHandler != null) {
			try {
				this.moduleRef.log("Invoking subcommand '" + subcommand + "' of command '" + commandName + "'.");
				subcommandHandler.invoke(this, executingPlayer, args);
				handled = true;
			}
			catch (Exception e) {
				this.moduleRef.log("Unable to execute subcommand handler for '" + commandName + "'");
				e.printStackTrace();
			}
		}

		return handled;
	}


	public void addCommandMapping(String command, Method method) {
		this.commandMap.put(command, method);
	}

	public void addSubcommandMappings(String command, HashMap<String,Method> subcommandMap) {
		this.subcommandMap.put(command, subcommandMap);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdName = command.getName().toLowerCase();

		if (!sender.isOp()) {
			return false;
		}

		this.moduleRef.log(sender.getName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		Method handler = this.commandMap.get(cmdName);
		if (handler != null) {
			try {
				handler.invoke(this, sender, command, label, args);
			}
			catch (Exception e) {
				this.moduleRef.log("Unable to execute command handler for '" + cmdName + "'");
				e.printStackTrace();
			}
		}

		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

}
