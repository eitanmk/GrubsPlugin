package com.selfequalsthis.grubsplugin.modules.teleport;

import static org.spongepowered.api.util.command.args.GenericArguments.seq;
import static org.spongepowered.api.util.command.args.GenericArguments.string;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.google.common.base.Optional;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommand;

public class TeleportModuleCommandTpset extends AbstractGrubsCommand {

	private TeleportModule moduleRef;
	private Game game;

	public TeleportModuleCommandTpset(TeleportModule module, Game game) {
		this.moduleRef = module;
		this.game = game;
	}

	@Override
	public String getCommandName() {
		return "tpset";
	}

	@Override
	public void init() {
		this.cmdSpec = CommandSpec.builder()
				.setDescription(Texts.of("Set a new teleport preset"))
				.setArguments(seq(string(Texts.of("name"))))
				.setExecutor(new TpsetCommand())
				.build();
	}

	private class TpsetCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {

			if (src instanceof Player) {
				Player p = (Player) src;

				Optional<String> optName = args.getOne("name");
				if (!optName.isPresent()) {
					src.sendMessage(Texts.of("Please provide a name for this teleport preset."));
					return CommandResult.empty();
				}

				String name = optName.get();
				if (moduleRef.teleportPresets.containsKey(name)) {
					src.sendMessage(Texts.of("A preset called '" + name + "' already exists."));
					return CommandResult.empty();
				}
				else {
					TeleportModuleLocation loc = new TeleportModuleLocation(moduleRef, game);
					loc.fromPlayer(p);
					moduleRef.teleportPresets.put(name, loc);
					src.sendMessage(Texts.of("Creating teleport preset: " + name));
					return CommandResult.success();
				}
			}

			return CommandResult.empty();
		}

	}

}
