package com.selfequalsthis.grubsplugin.module.teleport;

import static org.spongepowered.api.command.args.GenericArguments.none;
import static org.spongepowered.api.command.args.GenericArguments.seq;
import static org.spongepowered.api.command.args.GenericArguments.string;

import java.util.ArrayList;
import java.util.Optional;
import java.util.Set;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class TeleportModuleCommandHandlers extends AbstractGrubsCommandHandlers {

	private TeleportModule moduleRef;
	private Game game;

	public TeleportModuleCommandHandlers(TeleportModule module, Game game) {
		this.moduleRef = module;
		this.game = game;

		this.commands.put("tpset", CommandSpec.builder()
				.description(Text.of("Set a new teleport preset"))
				.arguments(seq(string(Text.of("name"))))
				.executor(new TpsetCommand())
				.build());

		this.commands.put("tplist", CommandSpec.builder()
				.description(Text.of("List teleport presets"))
				.arguments(none())
				.executor(new TplistCommand())
				.build());

		// TODO dynamic autocomplete
		this.commands.put("tpdel", CommandSpec.builder()
				.description(Text.of("Delete a teleport preset"))
				.arguments(seq(string(Text.of("name"))))
				.executor(new TpdelCommand())
				.build());

		// TODO dynamic autocomplete
		this.commands.put("goto", CommandSpec.builder()
				.description(Text.of("Go to a teleport preset location"))
				.arguments(seq(string(Text.of("name"))))
				.executor(new GotoCommand())
				.build());
	}


	private class TpsetCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

			if (src instanceof Player) {
				Player p = (Player) src;

				Optional<String> optName = args.getOne("name");
				if (!optName.isPresent()) {
					src.sendMessage(Text.of("Please provide a name for this teleport preset."));
					return CommandResult.empty();
				}

				String name = optName.get();
				if (moduleRef.teleportPresets.containsKey(name)) {
					src.sendMessage(Text.of("A preset called '" + name + "' already exists."));
					return CommandResult.empty();
				}
				else {
					TeleportModuleLocation loc = new TeleportModuleLocation(moduleRef, game);
					loc.fromPlayer(p);
					moduleRef.teleportPresets.put(name, loc);
					src.sendMessage(Text.of("Creating teleport preset: " + name));
					moduleRef.saveTeleportPresets();
					return CommandResult.success();
				}
			}

			return CommandResult.empty();
		}

	}

	private class TplistCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

			String msgIdentifier = "[Teleport] ";
			Set<String> keys = moduleRef.teleportPresets.keySet();
			ArrayList<String> origList = new ArrayList<String>(keys);
			ArrayList<String> filteredList = new ArrayList<String>();

			for (int i = 0, len = origList.size(); i < len; ++i) {
				String cur = origList.get(i);
				if (cur.indexOf(TeleportModule.SEPARATOR) == -1) {
					filteredList.add(cur);
				}
			}

			if (filteredList.size() > 0) {
				// TODO use pagination API
				String list = "";
				String sep = "";
				for (String s : filteredList) {
					list = list + sep + s;
					sep = ", ";
				}
				src.sendMessage(Text.of(msgIdentifier + list));
				return CommandResult.success();
			}
			else {
				src.sendMessage(Text.of(msgIdentifier + "No presets in list."));
			}

			return CommandResult.empty();
		}

	}

	private class TpdelCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

			Optional<String> optName = args.getOne("name");
			if (!optName.isPresent()) {
				src.sendMessage(Text.of("Please provide a preset name to delete."));
				return CommandResult.empty();
			}

			String name = optName.get();
			if (moduleRef.teleportPresets.containsKey(name)) {
				moduleRef.teleportPresets.remove(name);
				src.sendMessage(Text.of("Preset '" + name + "' deleted."));
				moduleRef.saveTeleportPresets();
				return CommandResult.success();
			}
			else {
				src.sendMessage(Text.of("Teleport preset '" + name + "' not found."));
			}

			return CommandResult.empty();
		}

	}

	private class GotoCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

			if (src instanceof Player) {
				//Player p = (Player) src;
			}

			return CommandResult.empty();
		}

	}

}
