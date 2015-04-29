package com.selfequalsthis.grubsplugin.modules.gameinfo;

import static org.spongepowered.api.util.command.args.GenericArguments.player;

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

public class GameInfoCommandSendcoords extends AbstractGrubsCommand {

	private GameInfoModule moduleRef;
	private Game game;

	public GameInfoCommandSendcoords(GameInfoModule module, Game game) {
		this.moduleRef = module;
		this.game = game;
	}

	@Override
	public String getCommandName() {
		return "sendcoords";
	}

	@Override
	public void init() {
		this.cmdSpec = CommandSpec.builder()
				.setDescription(Texts.of("Send coordinates to another player"))
				.setArguments(player(Texts.of("playerName"), this.game))
				.setExecutor(new SendcoordsCommand())
				.build();
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
