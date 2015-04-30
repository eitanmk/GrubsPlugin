package com.selfequalsthis.grubsplugin.modules.moduleloader;

import static org.spongepowered.api.util.command.args.GenericArguments.choices;
import static org.spongepowered.api.util.command.args.GenericArguments.none;
import static org.spongepowered.api.util.command.args.GenericArguments.seq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.google.common.base.Optional;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommand;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsSubcommand;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class ModuleLoaderCommandGpmodule extends AbstractGrubsCommand {

	private ModuleLoaderModule moduleRef;
	private HashMap<List<String>, CommandSpec> subCommands = new HashMap<List<String>, CommandSpec>();

	public ModuleLoaderCommandGpmodule(ModuleLoaderModule module) {
		this.moduleRef = module;
	}

	@Override
	public String getCommandName() {
		return "gpmodule";
	}

	@Override
	public void init() {

		HashMap<String,String> moduleChoices = new HashMap<String,String>();
		for (String key : this.moduleRef.allModules.keySet()) {
			moduleChoices.put(key, key);
		}

		this.subCommands.put(Arrays.asList("list"), CommandSpec.builder()
				.setDescription(Texts.of("List status of GrubsPlugin modules"))
				.setArguments(none())
				.setExecutor(new ListSubcommand())
				.build());

		this.subCommands.put(Arrays.asList("enable"), CommandSpec.builder()
				.setDescription(Texts.of("Enable specified GrubsPlugin module"))
				.setArguments(seq(choices(Texts.of("moduleName"), moduleChoices)))
				.setExecutor(new EnableSubcommand())
				.build());

		this.subCommands.put(Arrays.asList("disable"), CommandSpec.builder()
				.setDescription(Texts.of("Disable specified GrubsPlugin module"))
				.setArguments(seq(choices(Texts.of("moduleName"), moduleChoices)))
				.setExecutor(new DisableSubcommand())
				.build());

		this.cmdSpec = CommandSpec.builder()
				.setDescription(Texts.of("Manage GrubsPlugin modules"))
				.setExtendedDescription(Texts.of("List, enable/disable GrubsPlugin modules"))
				.setChildren(this.subCommands)
				.build();
	}

	private class ListSubcommand extends AbstractGrubsSubcommand {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			String resp = "";
			String separator = "";
			for (String key : moduleRef.allModules.keySet()) {
				resp = resp + separator + key + (moduleRef.activeModules.containsKey(key) ? "(X)" : "( )");
				separator = " ";
			}
			src.sendMessage(Texts.of("Modules: " + resp + "."));
			return CommandResult.success();
		}

	}

	private class EnableSubcommand extends AbstractGrubsSubcommand {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<String> optModuleName = args.getOne("moduleName");
			if (!optModuleName.isPresent()) {
				throw new CommandException(Texts.of("A module name is required!"));
			}
			else {
				String moduleName = optModuleName.get();

				if (!moduleRef.allModules.containsKey(moduleName)) {
					src.sendMessage(Texts.of("Unknown module [" + moduleName + "]."));
					return CommandResult.success();
				}

				if (moduleRef.activeModules.containsKey(moduleName)) {
					src.sendMessage(Texts.of("Module [" + moduleName + "] already enabled."));
					return CommandResult.success();
				}

				AbstractGrubsModule gm = moduleRef.allModules.get(moduleName);
				moduleRef.activeModules.put(moduleName, gm);
				gm.enable();
				src.sendMessage(Texts.of("Module [" + moduleName + "] enabled."));
				return CommandResult.success();
			}

		}

	}

	private class DisableSubcommand extends AbstractGrubsSubcommand {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<String> optModuleName = args.getOne("moduleName");
			if (!optModuleName.isPresent()) {
				throw new CommandException(Texts.of("A module name is required!"));
			}
			else {
				String moduleName = optModuleName.get();

				if (!moduleRef.allModules.containsKey(moduleName)) {
					src.sendMessage(Texts.of("Unknown module [" + moduleName + "]."));
					return CommandResult.success();
				}

				if (!moduleRef.activeModules.containsKey(moduleName)) {
					src.sendMessage(Texts.of("Module [" + moduleName + "] not enabled."));
					return CommandResult.success();
				}

				AbstractGrubsModule gm = moduleRef.allModules.get(moduleName);
				moduleRef.activeModules.remove(moduleName);
				gm.disable();
				src.sendMessage(Texts.of("Module [" + moduleName + "] disabled."));
				return CommandResult.success();
			}

		}

	}

}