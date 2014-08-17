package com.selfequalsthis.grubsplugin.command;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

public abstract class AbstractGrubsCommandHandler implements TabExecutor {

	protected AbstractGrubsModule moduleRef = null;
	public HashMap<String,Method> handlers = new HashMap<String,Method>();

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
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
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

}
