package com.selfequalsthis.grubsplugin.modules.moduleloader;

import static org.spongepowered.api.util.command.args.GenericArguments.choices;
import static org.spongepowered.api.util.command.args.GenericArguments.none;
import static org.spongepowered.api.util.command.args.GenericArguments.seq;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class ModuleLoaderCommandHandlers extends AbstractGrubsCommandHandlers {

	private ModuleLoaderModule moduleRef;

	public ModuleLoaderCommandHandlers(ModuleLoaderModule module) {
		this.moduleRef = module;

		HashMap<List<String>, CommandSpec> subCommands = new HashMap<List<String>, CommandSpec>();

		HashMap<String,String> moduleChoices = new HashMap<String,String>();
		for (String key : this.moduleRef.allModules.keySet()) {
			moduleChoices.put(key, key);
		}

		subCommands.put(Arrays.asList("list"), CommandSpec.builder()
				.description(Texts.of("List status of GrubsPlugin modules"))
				.arguments(none())
				.executor(new ListSubcommand())
				.build());

		// TODO dynamic autocomplete
		subCommands.put(Arrays.asList("enable"), CommandSpec.builder()
				.description(Texts.of("Enable specified GrubsPlugin module"))
				.arguments(seq(choices(Texts.of("moduleName"), moduleChoices)))
				.executor(new EnableSubcommand())
				.build());

		// TODO dynamic autocomplete
		subCommands.put(Arrays.asList("disable"), CommandSpec.builder()
				.description(Texts.of("Disable specified GrubsPlugin module"))
				.arguments(seq(choices(Texts.of("moduleName"), moduleChoices)))
				.executor(new DisableSubcommand())
				.build());

		this.commands.put("gpmodule", CommandSpec.builder()
				.description(Texts.of("Manage GrubsPlugin modules"))
				.extendedDescription(Texts.of("List, enable/disable GrubsPlugin modules"))
				.children(subCommands)
				.build());
	}


	private class ListSubcommand implements CommandExecutor {

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

	private class EnableSubcommand implements CommandExecutor {

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

	private class DisableSubcommand implements CommandExecutor {

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
