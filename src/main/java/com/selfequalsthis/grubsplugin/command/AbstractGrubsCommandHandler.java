package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public abstract class AbstractGrubsCommandHandler implements CommandExecutor {

	protected AbstractGrubsModule moduleRef = null;
	public HashMap<String,Method> handlers = new HashMap<String,Method>();

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) {
		/*
		String cmdName = command.getName().toLowerCase();

		if (!sender.isOp()) {
			return false;
		}

		this.moduleRef.log(sender.getName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		Method handler = this.handlers.get(cmdName);
		if (handler != null) {
			GrubsCommandInfo info = new GrubsCommandInfo(sender, command, label, args);
			try {
				handler.invoke(this, info);
			}
			catch (Exception e) {
				this.moduleRef.log("Unable to execute command handler for '" + cmdName + "'");
			}
		}

		return true;
		*/
		
		return CommandResult.empty();
	}

}
