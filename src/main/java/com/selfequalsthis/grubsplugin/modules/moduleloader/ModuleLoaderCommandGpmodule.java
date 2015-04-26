package com.selfequalsthis.grubsplugin.modules.moduleloader;

import java.util.List;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;

import com.google.common.base.Optional;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class ModuleLoaderCommandGpmodule extends AbstractGrubsCommandHandler {

	private ModuleLoaderModule moduleRef;
	
	public ModuleLoaderCommandGpmodule(ModuleLoaderModule module) {
		this.moduleRef = module;
	}

	@Override
	public String getCommandName() {
		return "gpmodule";
	}
	
	@Override
	public Optional<CommandResult> process(CommandSource source, String arguments) throws CommandException {
		
		final String[] args = arguments.split(" ");
		
		if (arguments.isEmpty()) {
			return Optional.of(CommandResult.empty());
		}

		String subCommand = args[0];
		if (subCommand.equalsIgnoreCase("list")) {
			String resp = "";
			String separator = "";
			for (String key : this.moduleRef.allModules.keySet()) {
				resp = resp + separator + key + (this.moduleRef.activeModules.containsKey(key) ? "(X)" : "( )");
				separator = " ";
			}
			source.sendMessage(Texts.of("Modules: " + resp + "."));
			return Optional.of(CommandResult.success());
		}

		if (args.length > 1) {
			String moduleName = args[1];
			AbstractGrubsModule gm = null;

			if (!this.moduleRef.allModules.containsKey(moduleName)) {
				source.sendMessage(Texts.of("Unknown module [" + moduleName + "]."));
				return Optional.of(CommandResult.success());
			}

			if (subCommand.equalsIgnoreCase("enable")) {
				if (this.moduleRef.activeModules.containsKey(moduleName)) {
					source.sendMessage(Texts.of("Module [" + moduleName + "] already enabled."));
					return Optional.of(CommandResult.success());
				}

				gm = this.moduleRef.allModules.get(moduleName);
				this.moduleRef.activeModules.put(moduleName, gm);
				gm.enable();
				source.sendMessage(Texts.of("Module [" + moduleName + "] enabled."));
			}
			else if (subCommand.equalsIgnoreCase("disable")) {
				if (!this.moduleRef.activeModules.containsKey(moduleName)) {
					source.sendMessage(Texts.of("Module [" + moduleName + "] not enabled."));
					return Optional.of(CommandResult.success());
				}

				gm = this.moduleRef.allModules.get(moduleName);
				this.moduleRef.activeModules.remove(moduleName);
				gm.disable();
				source.sendMessage(Texts.of("Module [" + moduleName + "] disabled."));
			}
		}
		
		return Optional.of(CommandResult.empty());
	}

	@Override
	public List<String> getSuggestions(CommandSource source, String arguments) throws CommandException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean testPermission(CommandSource source) {
		return source.hasPermission("grubsplugin.command.gpmodule");
	}

	@Override
	public Optional<Text> getShortDescription(CommandSource source) {
		return Optional.of((Text) Texts.of("Manage GrubsPlugin modules"));
	}

	@Override
	public Optional<Text> getHelp(CommandSource source) {
		return Optional.of((Text) Texts.of("List, enable/disable GrubsPlugin modules"));
	}

	@Override
	public Text getUsage(CommandSource source) {
		return Texts.of("/<command> [list|enable|disable] <module-name>");
	}

}
