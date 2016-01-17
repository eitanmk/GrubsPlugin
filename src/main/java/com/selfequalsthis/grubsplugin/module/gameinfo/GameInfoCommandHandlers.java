package com.selfequalsthis.grubsplugin.module.gameinfo;

import static org.spongepowered.api.command.args.GenericArguments.none;
import static org.spongepowered.api.command.args.GenericArguments.optional;
import static org.spongepowered.api.command.args.GenericArguments.player;

import java.util.Optional;

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

public class GameInfoCommandHandlers extends AbstractGrubsCommandHandlers {

	private GameInfoModule moduleRef;

	public GameInfoCommandHandlers(GameInfoModule module, Game game) {
		this.moduleRef = module;

		this.commands.put("gettime", CommandSpec.builder()
				.description(Text.of("Get current time"))
				.extendedDescription(Text.of("Get time for current world in ticks"))
				.arguments(none())
				.executor(new GettimeCommand())
				.build());

		this.commands.put("getcoords", CommandSpec.builder()
				.description(Text.of("Get coordinates for self or another player"))
				.arguments(optional(player(Text.of("playerName"))))
				.executor(new GetcoordsCommand())
				.build());

		this.commands.put("sendcoords", CommandSpec.builder()
				.description(Text.of("Send coordinates to another player"))
				.arguments(player(Text.of("playerName")))
				.executor(new SendcoordsCommand())
				.build());
	}


	private class GettimeCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player p = (Player) src;
				long time = p.getWorld().getProperties().getWorldTime() % 24000;
				src.sendMessage(Text.of("Current time is: " + time));
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
				src.sendMessage(Text.of(target.getName() + ": " + moduleRef.getCoordsStrFromLocation(target.getLocation())));
				return CommandResult.success();
			}
			else {
				if (src instanceof Player) {
					Player p = (Player) src;
					src.sendMessage(Text.of(p.getName() + ": " + moduleRef.getCoordsStrFromLocation(p.getLocation())));
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
				target.sendMessage(Text.of(sender.getName() + ": " + moduleRef.getCoordsStrFromLocation(sender.getLocation())));
				return CommandResult.success();
			}

			return CommandResult.empty();
		}

	}

}
