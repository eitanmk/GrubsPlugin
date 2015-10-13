package com.selfequalsthis.grubsplugin.modules.moduleloader;

import static org.spongepowered.api.util.command.args.GenericArguments.seq;
import static org.spongepowered.api.util.command.args.GenericArguments.string;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
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
	private PaginationService paginationService;

	public ModuleLoaderCommandHandlers(ModuleLoaderModule module) {
		this.moduleRef = module;
		this.paginationService = this.moduleRef.getGame().getServiceManager().provide(PaginationService.class).get();

		HashMap<List<String>, CommandSpec> subCommands = new HashMap<List<String>, CommandSpec>();

		subCommands.put(Arrays.asList("enable"), CommandSpec.builder()
				.description(Texts.of("Enable specified GrubsPlugin module"))
				.arguments(seq(string(Texts.of("moduleName"))))
				.executor(new EnableSubcommand())
				.build());

		subCommands.put(Arrays.asList("disable"), CommandSpec.builder()
				.description(Texts.of("Disable specified GrubsPlugin module"))
				.arguments(seq(string(Texts.of("moduleName"))))
				.executor(new DisableSubcommand())
				.build());

		this.commands.put("gpmodule", CommandSpec.builder()
				.description(Texts.of("Manage GrubsPlugin modules"))
				.extendedDescription(Texts.of("List, enable/disable GrubsPlugin modules"))
				.children(subCommands)
				.executor(new ListSubcommand())
				.build());
	}


	private class ListSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			List<Text> contents = new ArrayList<>();
			for (String key : moduleRef.allModules.keySet()) {
				boolean enabled = moduleRef.activeModules.containsKey(key);
				String statusText = enabled ? "[X]" : "[ ]";
				String actionText = enabled ? "Disable" : "Enable";
				String commandText = "/gpmodule " + (enabled ? "disable" : "enable") + " " + key;
				Text pageItem = Texts.builder(statusText)
						.append(Texts.of(" "))
						.append(Texts.of(key))
						.append(Texts.of(" | "))
						.append(
							Texts.builder(actionText)
								.color(enabled ? TextColors.DARK_RED : TextColors.GREEN)
								.onClick(TextActions.runCommand(commandText))
								.build()
						)
						.build();
				contents.add(pageItem);
			}

			paginationService.builder()
				.title(Texts.of("GrubsPlugin Modules"))
				.contents(contents)
				.sendTo(src);

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
