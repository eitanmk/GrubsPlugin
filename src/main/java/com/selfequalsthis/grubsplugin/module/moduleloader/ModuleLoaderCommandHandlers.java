package com.selfequalsthis.grubsplugin.module.moduleloader;

import static org.spongepowered.api.command.args.GenericArguments.seq;
import static org.spongepowered.api.command.args.GenericArguments.string;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;
import com.selfequalsthis.grubsplugin.module.AbstractGrubsModule;

public class ModuleLoaderCommandHandlers extends AbstractGrubsCommandHandlers {

	private ModuleLoaderModule moduleRef;
	private PaginationService paginationService;

	public ModuleLoaderCommandHandlers(ModuleLoaderModule module) {
		this.moduleRef = module;
		this.paginationService = this.moduleRef.getGame().getServiceManager().provide(PaginationService.class).get();

		this.commands.put("gpmodule", CommandSpec.builder()
				.description(Text.of("Manage GrubsPlugin modules"))
				.extendedDescription(Text.of("List, enable/disable GrubsPlugin modules"))
				.child(CommandSpec.builder()
						.description(Text.of("Enable specified GrubsPlugin module"))
						.arguments(seq(string(Text.of("moduleName"))))
						.executor(new EnableSubcommand())
						.build(),
					"enable")
				.child(CommandSpec.builder()
						.description(Text.of("Disable specified GrubsPlugin module"))
						.arguments(seq(string(Text.of("moduleName"))))
						.executor(new DisableSubcommand())
						.build(),
					"disable")
				.executor(new ListSubcommand())
				.build());
	}


	private class ListSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			ArrayList<Text> contents = new ArrayList<Text>();
			for (String key : moduleRef.allModules.keySet()) {
				boolean enabled = moduleRef.activeModules.containsKey(key);
				String statusText = enabled ? "[X]" : "[ ]";
				String actionText = enabled ? "Disable" : "Enable";
				String commandText = "/gpmodule " + (enabled ? "disable" : "enable") + " " + key;
				Text pageItem = Text.builder(statusText)
						.append(Text.of(" "))
						.append(Text.of(key))
						.append(Text.of(" | "))
						.append(
							Text.builder(actionText)
								.color(enabled ? TextColors.DARK_RED : TextColors.GREEN)
								.onClick(TextActions.runCommand(commandText))
								.build()
						)
						.build();
				contents.add(pageItem);
			}

			paginationService.builder()
				.title(Text.of("GrubsPlugin Modules"))
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
				throw new CommandException(Text.of("A module name is required!"));
			}
			else {
				String moduleName = optModuleName.get();

				if (!moduleRef.allModules.containsKey(moduleName)) {
					src.sendMessage(Text.of("Unknown module [" + moduleName + "]."));
					return CommandResult.success();
				}

				if (moduleRef.activeModules.containsKey(moduleName)) {
					src.sendMessage(Text.of("Module [" + moduleName + "] already enabled."));
					return CommandResult.success();
				}

				AbstractGrubsModule gm = moduleRef.allModules.get(moduleName);
				moduleRef.activeModules.put(moduleName, gm);
				gm.enable();
				src.sendMessage(Text.of("Module [" + moduleName + "] enabled."));
				return CommandResult.success();
			}

		}

	}

	private class DisableSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<String> optModuleName = args.getOne("moduleName");
			if (!optModuleName.isPresent()) {
				throw new CommandException(Text.of("A module name is required!"));
			}
			else {
				String moduleName = optModuleName.get();

				if (!moduleRef.allModules.containsKey(moduleName)) {
					src.sendMessage(Text.of("Unknown module [" + moduleName + "]."));
					return CommandResult.success();
				}

				if (!moduleRef.activeModules.containsKey(moduleName)) {
					src.sendMessage(Text.of("Module [" + moduleName + "] not enabled."));
					return CommandResult.success();
				}

				AbstractGrubsModule gm = moduleRef.allModules.get(moduleName);
				moduleRef.activeModules.remove(moduleName);
				gm.disable();
				src.sendMessage(Text.of("Module [" + moduleName + "] disabled."));
				return CommandResult.success();
			}

		}

	}

}
