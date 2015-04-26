package com.selfequalsthis.grubsplugin.modules.gameinfo;

import static org.spongepowered.api.util.command.args.GenericArguments.none;

import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommand;

public class GameInfoCommandGettime extends AbstractGrubsCommand {

	@Override
	public String getCommandName() {
		return "gettime";
	}

	@Override
	public void init() {
		this.cmdSpec = CommandSpec.builder()
				.setDescription(Texts.of("Get current time"))
				.setExtendedDescription(Texts.of("Get time for current world in ticks"))
				.setArguments(none())
				.setExecutor(new GettimeCommand())
				.build();
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

}
