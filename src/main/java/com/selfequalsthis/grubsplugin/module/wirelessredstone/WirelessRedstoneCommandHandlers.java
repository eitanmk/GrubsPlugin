package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import static org.spongepowered.api.command.args.GenericArguments.none;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class WirelessRedstoneCommandHandlers extends AbstractGrubsCommandHandlers {

	private WirelessRedstoneModule moduleRef;

	public WirelessRedstoneCommandHandlers(WirelessRedstoneModule module) {
		this.moduleRef = module;

		this.commands.put("wrchannelclean", CommandSpec.builder()
				.description(Text.of("Clean corrupt WR channel data"))
				.extendedDescription(Text.of("Utility to clean corrupted wireless redstone channels."))
				.arguments(none())
				.executor(new WrchannelcleanCommand())
				.build());
	}

	private class WrchannelcleanCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if (src instanceof Player) {
				Player p = (Player) src;
				World world = p.getWorld();
				moduleRef.repairChannels(world);
				return CommandResult.success();
			}

			src.sendMessage(Text.of("Run this command as a player."));
			return CommandResult.empty();
		}
	}
}
