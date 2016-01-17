package com.selfequalsthis.grubsplugin.module.timelock;

import static org.spongepowered.api.command.args.GenericArguments.choices;
import static org.spongepowered.api.command.args.GenericArguments.integer;
import static org.spongepowered.api.command.args.GenericArguments.optionalWeak;
import static org.spongepowered.api.command.args.GenericArguments.seq;

import java.util.HashMap;
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
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class TimeLockCommandHandlers extends AbstractGrubsCommandHandlers {

	private TimeLockModule moduleRef;
	private Game game;

	public TimeLockCommandHandlers(TimeLockModule module, Game game) {
		this.moduleRef = module;
		this.game = game;

		HashMap<String,Long> predefinedSettings = new HashMap<String,Long>();
		predefinedSettings.put("day", 6000L);
		predefinedSettings.put("night", 18000L);
		predefinedSettings.put("on", -1L);
		predefinedSettings.put("off", -2L);

		this.commands.put("timelock", CommandSpec.builder()
				.description(Text.of("Lock/Unlock world time"))
				.extendedDescription(Text.of("Lock or unlock the current time for this world"))
				.arguments(seq(optionalWeak(choices(Text.of("predefSetting"), predefinedSettings)), optionalWeak(integer(Text.of("timeValue")))))
				.executor(new TimelockCommand())
				.build());
	}


	private class TimelockCommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<Long> predefValue = args.getOne("predefSetting");
			Optional<Integer> timeValue = args.getOne("timeValue");

			if (!predefValue.isPresent() && !timeValue.isPresent()) {
				src.sendMessage(Text.of("Not enough arguments!"));
				return CommandResult.empty();
			}

			World world;
			if (src instanceof Player) {
				Player p = (Player) src;
				world = p.getWorld();
			}
			else {
				world = game.getServer().getWorlds().toArray(new World[1])[0];
			}

			long currentWorldTime = world.getProperties().getWorldTime() % 24000;
			long time = currentWorldTime;

			if (predefValue.isPresent()) {
				time = predefValue.get().longValue();

				// "off"
				if (time == -2L) {
					moduleRef.unlockTime();
					src.sendMessage(Text.of("Time lock is now off."));
					return CommandResult.success();
				}

				// "on"
				if (time == -1L) {
					time = currentWorldTime;
				}
			}
			else if (timeValue.isPresent()) {
				time = timeValue.get();
				if (time < 0 || time > 24000) {
					src.sendMessage(Text.of("Invalid time."));
					return CommandResult.empty();
				}
			}

			src.sendMessage(Text.of("Setting timelock: " + time));
			moduleRef.lockTime(world, time);
			return CommandResult.success();
		}

	}

}
