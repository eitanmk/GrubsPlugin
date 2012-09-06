package com.selfequalsthis.grubsplugin;

import java.lang.reflect.Method;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public abstract class AbstractGrubsCommandHandler implements CommandExecutor {

	protected AbstractGrubsModule moduleRef = null;
	public HashMap<String,Method> handlers = new HashMap<String,Method>();
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		String cmdName = command.getName().toLowerCase();
		
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

}
