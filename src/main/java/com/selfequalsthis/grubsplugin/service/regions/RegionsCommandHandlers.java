package com.selfequalsthis.grubsplugin.service.regions;

import static org.spongepowered.api.util.command.args.GenericArguments.seq;
import static org.spongepowered.api.util.command.args.GenericArguments.string;

import java.util.ArrayList;
import java.util.Optional;

import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.service.pagination.PaginationService;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.util.command.CommandException;
import org.spongepowered.api.util.command.CommandResult;
import org.spongepowered.api.util.command.CommandSource;
import org.spongepowered.api.util.command.args.CommandContext;
import org.spongepowered.api.util.command.spec.CommandExecutor;
import org.spongepowered.api.util.command.spec.CommandSpec;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandlers;

public class RegionsCommandHandlers extends AbstractGrubsCommandHandlers {
	
	private RegionsService serviceRef;
	private RegionsServiceProvider regionController;
	private PaginationService paginationService;

	public RegionsCommandHandlers(RegionsService service, RegionsServiceProvider provider) {
		this.serviceRef = service;
		this.regionController = provider;
		this.paginationService = this.serviceRef.getGame().getServiceManager().provide(PaginationService.class).get();
		
		this.commands.put("regions", CommandSpec.builder()
				.description(Texts.of("Used to manage regions."))
				.extendedDescription(Texts.of("List, create, and delete regions."))
				.child(CommandSpec.builder()
						.description(Texts.of("Create a new region."))
						.arguments(seq(string(Texts.of("regionName"))))
						.executor(new CreateSubcommand())
						.build(),
					"create")
				.child(CommandSpec.builder()
						.description(Texts.of("Add a vertex to a region."))
						.arguments(seq(string(Texts.of("regionName"))))
						.executor(new VertexSubcommand())
						.build(),
					"vertex")
				.child(CommandSpec.builder()
						.description(Texts.of("Mark a region as complete."))
						.arguments(seq(string(Texts.of("regionName"))))
						.executor(new CompleteSubcommand())
						.build(),
					"complete")
				.child(CommandSpec.builder()
						.description(Texts.of("Delete a region."))
						.arguments(seq(string(Texts.of("regionName"))))
						.executor(new DeleteSubcommand())
						.build(), 
					"delete")
				.executor(new ListSubcommand())
				.build());
	}
	
	
	private class ListSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if ( ! (src instanceof Player) ) {
				return CommandResult.empty();
			}
			
			Player executingPlayer = (Player) src;
			ArrayList<Text> regionNames = regionController.listRegions(executingPlayer.getWorld().getUniqueId());
			if (regionNames.size() > 0) {
				// TODO: delete click actions?
				paginationService.builder()
					.title(Texts.of("GrubsPlugin Modules"))
					.contents(regionNames)
					.sendTo(src);
			}
			else {
				executingPlayer.sendMessage(Texts.of("No regions for this world."));
			}

			return CommandResult.success();
		}
	}
	
	private class CreateSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if ( ! (src instanceof Player) ) {
				return CommandResult.empty();
			}
			
			Player executingPlayer = (Player) src;
			Optional<String> optRegionName = args.getOne("regionName");
			if (!optRegionName.isPresent()) {
				throw new CommandException(Texts.of("A region name is required!"));
			}
			
			String regionName = optRegionName.get();
			boolean success = regionController.createRegion(executingPlayer.getWorld().getUniqueId(), regionName);
			if (success) {
				src.sendMessage(Texts.of("Initialized region [" + regionName + "]."));
			}
			else {
				src.sendMessage(Texts.of("Region [" + regionName + "] already exists for this world."));
			}
			
			return CommandResult.success();
		}
	}

	private class VertexSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if ( ! (src instanceof Player) ) {
				return CommandResult.empty();
			}
			
			Player executingPlayer = (Player) src;
			Optional<String> optRegionName = args.getOne("regionName");
			if (!optRegionName.isPresent()) {
				throw new CommandException(Texts.of("A region name is required!"));
			}
			
			String regionName = optRegionName.get();
			Location<World> curLoc = executingPlayer.getLocation();
			boolean success = regionController.addVertex(curLoc.getExtent().getUniqueId(), regionName, curLoc.getBlockX(), curLoc.getBlockY());
			if (success) {
				src.sendMessage(Texts.of("Vertex (" + curLoc.getBlockX() + "," + curLoc.getBlockZ() + ") added to region [" + regionName + "]."));
			}
			else {
				src.sendMessage(Texts.of("Unable to add vertex to region [" + regionName + "]."));
			}
			
			return CommandResult.success();
		}
	}
	
	private class CompleteSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if ( ! (src instanceof Player) ) {
				return CommandResult.empty();
			}
			
			Player executingPlayer = (Player) src;
			Optional<String> optRegionName = args.getOne("regionName");
			if (!optRegionName.isPresent()) {
				throw new CommandException(Texts.of("A region name is required!"));
			}
			
			String regionName = optRegionName.get();
			boolean success = regionController.completeRegion(executingPlayer.getWorld().getUniqueId(), regionName);
			if (success) {
				src.sendMessage(Texts.of("Completed region [" + regionName + "]."));
			}
			else {
				src.sendMessage(Texts.of("Unable to complete region [" + regionName + "]"));
			}
			
			return CommandResult.success();
		}
	}
	
	private class DeleteSubcommand implements CommandExecutor {

		@Override
		public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
			if ( ! (src instanceof Player) ) {
				return CommandResult.empty();
			}
			
			Player executingPlayer = (Player) src;
			Optional<String> optRegionName = args.getOne("regionName");
			if (!optRegionName.isPresent()) {
				throw new CommandException(Texts.of("A region name is required!"));
			}
			
			String regionName = optRegionName.get();
			boolean success = regionController.deleteRegion(executingPlayer.getWorld().getUniqueId(), regionName);
			if (success) {
				src.sendMessage(Texts.of("Deleted region [" + regionName + "]."));
			}
			else {
				src.sendMessage(Texts.of("Unable to delete region [" + regionName + "]"));
			}
			
			return CommandResult.success();
		}
	}
}
