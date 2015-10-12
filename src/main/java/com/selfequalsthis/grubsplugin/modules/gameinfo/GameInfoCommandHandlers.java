package com.selfequalsthis.grubsplugin.modules.gameinfo;

import static org.spongepowered.api.util.command.args.GenericArguments.none;
import static org.spongepowered.api.util.command.args.GenericArguments.optional;
import static org.spongepowered.api.util.command.args.GenericArguments.player;

import java.util.Optional;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class GameInfoCommandHandlers extends AbstractGrubsCommandHandlers {

	private GameInfoModule moduleRef;
	private Game game;

	public GameInfoCommandHandlers(GameInfoModule module, Game game) {
		this.moduleRef = module;
		this.game = game;

		this.commands.put("gettime", CommandSpec.builder()
				.description(Texts.of("Get current time"))
				.extendedDescription(Texts.of("Get time for current world in ticks"))
				.arguments(none())
				.executor(new GettimeCommand())
				.build());

		this.commands.put("getcoords", CommandSpec.builder()
				.description(Texts.of("Get coordinates for self or another player"))
				.arguments(optional(player(Texts.of("playerName"), this.game)))
				.executor(new GetcoordsCommand())
				.build());

		this.commands.put("sendcoords", CommandSpec.builder()
				.description(Texts.of("Send coordinates to another player"))
				.arguments(player(Texts.of("playerName"), this.game))
				.executor(new SendcoordsCommand())
				.build());
	}


	private class GettimeCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player p = (Player) src;
				long time = p.getWorld().getProperties().getWorldTime() % 24000;
				src.sendMessage(Texts.of("Current time is: " + time));
				return CommandResult.success();
			}

			return CommandResult.empty();
		}

	}

	private class GetcoordsCommand implements CommandExecutor {

		// TODO: handle different worlds, if we ever do a multi-world setup

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<Player> optPlayerName = args.getOne("playerName");
			if (optPlayerName.isPresent()) {
				Player target = optPlayerName.get();
				src.sendMessage(Texts.of(target.getName() + ": " + moduleRef.getCoordsStrFromLocation(target.getLocation())));
				return CommandResult.success();
			}
			else {
				if (src instanceof Player) {
					Player p = (Player) src;
					src.sendMessage(Texts.of(p.getName() + ": " + moduleRef.getCoordsStrFromLocation(p.getLocation())));
					return CommandResult.success();
				}
			}

			return CommandResult.empty();
		}

	}

	private class SendcoordsCommand implements CommandExecutor {

		// TODO: handle different worlds, if we ever do a multi-world setup

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<Player> optPlayerName = args.getOne("playerName");
			if (src instanceof Player && optPlayerName.isPresent()) {
				Player sender = (Player) src;
				Player target = optPlayerName.get();
				target.sendMessage(Texts.of(sender.getName() + ": " + moduleRef.getCoordsStrFromLocation(sender.getLocation())));
				return CommandResult.success();
			}

			return CommandResult.empty();
		}

	}

}
