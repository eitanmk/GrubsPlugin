package com.selfequalsthis.grubsplugin.modules.module_loader;

import org.bukkit.command.CommandSender;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class ModuleLoaderCommandHandlers extends AbstractGrubsCommandHandler {

	private ModuleLoaderModule mlModule;

	public ModuleLoaderCommandHandlers(ModuleLoaderModule module) {
		this.moduleRef = module;
		this.mlModule = module;
	}

	@GrubsCommandHandler(
		command = "gpmodule",
		desc = "Change modules enabled state.",
		usage = "/<command> [list|enable|disable] <module-name>"
	)
	public boolean onGpmoduleCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;

		if (args.length < 1) {
			return false;
		}

		String subCommand = args[0];
		if (subCommand.equalsIgnoreCase("list")) {
			String resp = "";
			String separator = "";
			for (String key : this.mlModule.allModules.keySet()) {
				resp = resp + separator + key + (this.mlModule.activeModules.containsKey(key) ? "(X)" : "( )");
				separator = " ";
			}
			sender.sendMessage("Modules: " + resp + ".");
			return true;
		}

		if (args.length > 1) {
			String moduleName = args[1];
			AbstractGrubsModule gm = null;

			if (!this.mlModule.allModules.containsKey(moduleName)) {
				sender.sendMessage("Unknown module [" + moduleName + "].");
				return true;
			}

			if (subCommand.equalsIgnoreCase("enable")) {
				if (this.mlModule.activeModules.containsKey(moduleName)) {
					sender.sendMessage("Module [" + moduleName + "] already enabled.");
					return true;
				}

				gm = this.mlModule.allModules.get(moduleName);
				this.mlModule.activeModules.put(moduleName, gm);
				gm.enable();
				sender.sendMessage("Module [" + moduleName + "] enabled.");
			}
			else if (subCommand.equalsIgnoreCase("disable")) {
				if (!this.mlModule.activeModules.containsKey(moduleName)) {
					sender.sendMessage("Module [" + moduleName + "] not enabled.");
					return true;
				}

				gm = this.mlModule.allModules.get(moduleName);
				this.mlModule.activeModules.remove(moduleName);
				gm.disable();
				sender.sendMessage("Module [" + moduleName + "] disabled.");
			}
		}

		return false;
	}
}
