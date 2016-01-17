package com.selfequalsthis.grubsplugin.module.worlds;

import static org.spongepowered.api.command.args.GenericArguments.seq;
import static org.spongepowered.api.command.args.GenericArguments.string;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.gamemode.GameModes;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.GeneratorTypes;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.WorldCreationSettings;
import org.spongepowered.api.world.storage.WorldProperties;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class WorldsCommandHandlers extends AbstractGrubsCommandHandlers {

	private Game game;
	private PaginationService paginationService;

	public WorldsCommandHandlers(WorldsModule module, Game game) {
		this.game = game;
		this.paginationService = game.getServiceManager().provide(PaginationService.class).get();

		this.commands.put("worlds", CommandSpec.builder()
				.description(Text.of("Manages worlds on this server."))
				.child(CommandSpec.builder()
						.description(Text.of("Create a test world"))
						.arguments(seq(string(Text.of("name"))))
						.executor(new TestSubcommand())
						.build(),
					"test")
				.child(CommandSpec.builder()
						.description(Text.of("Goto named world"))
						.arguments(seq(string(Text.of("name"))))
						.executor(new GotoSubcommand())
						.build(),
					"goto")
				.executor(new ListSubcommand())
				.build());
	}

	private class ListSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			ArrayList<Text> contents = new ArrayList<Text>();
			for (WorldProperties props : game.getServer().getAllWorldProperties()) {
				contents.add(Text.of(props.getWorldName() + " " + props.getDimensionType().getName()));
			}

			paginationService.builder()
				.title(Text.of("Worlds"))
				.contents(contents)
				.sendTo(src);

			return CommandResult.success();
		}
	}
	
	private class TestSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<String> optName = args.getOne("name");
			String newName = optName.isPresent() ? optName.get() : "testworld";
			game.getRegistry().createBuilder(WorldCreationSettings.Builder.class)
				.dimension(DimensionTypes.OVERWORLD)
				.gameMode(GameModes.CREATIVE)
				.name(newName)
				.generator(GeneratorTypes.FLAT)
				.keepsSpawnLoaded(true)
				.loadsOnStartup(true)
				.build();

			return CommandResult.success();
		}
	}
	
private class GotoSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			Optional<String> optName = args.getOne("name");
			String newName = optName.isPresent() ? optName.get() : "hillel";
			Optional<World> optWorld = game.getServer().loadWorld(newName);
			if (optWorld.isPresent() && src instanceof Player) {
				Player p = (Player) src;
				World w = optWorld.get();
				p.setLocation(new Location<World>(w, w.getProperties().getSpawnPosition()));
			}

			return CommandResult.success();
		}
	}

}
