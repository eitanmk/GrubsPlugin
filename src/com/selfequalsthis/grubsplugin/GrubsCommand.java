package com.selfequalsthis.grubsplugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GrubsCommand extends Command {

	private CommandExecutor executor = null;

	public GrubsCommand(String name) {
		super(name);
	}

	public void setExecutor(CommandExecutor exe){
        this.executor = exe;
    }

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args) {
		boolean success = false;

		if (this.executor != null) {
            success = this.executor.onCommand(sender, this, commandLabel, args);
            if (!success && this.usageMessage.length() > 0) {
            	for (String line : usageMessage.replace("<command>", commandLabel).split("\n")) {
            		sender.sendMessage(line);
            	}
            }
        }

        return false;
	}

}
