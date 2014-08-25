package com.selfequalsthis.grubsplugin.modules.regions;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.annotations.GrubsSubcommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

public class RegionsCommandHandlers extends AbstractGrubsCommandHandler {

	private RegionsServiceProvider regionController;

	public RegionsCommandHandlers(AbstractGrubsModule module, RegionsServiceProvider service) {
		this.moduleRef = module;
		this.regionController = service;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return null;
	}

	@GrubsCommandHandler(
		command = "regions",
		desc = "Used to manage regions.",
		subcommands = { "create", "vertex", "complete", "list", "delete" }
	)
	public boolean onRegionsCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (args.length == 0) {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			}

			if (! this.invokeSubcommandHandler(command, executingPlayer, args) ) {
				String subcommand = args[0];
				if (subcommand.equalsIgnoreCase("cancel")) {
					//GrubsDefendShed.gameCancelled();
					//GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Game cancelled.");
				}
				else {
					GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
				}
			}
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "create",
		forCommand = "regions"
	)
	public boolean handleSubCommandCreate(Player executingPlayer, String[] args) {
		if (args.length <= 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			return true;
		}

		String regionName = args[1];

		boolean success = this.regionController.createRegion(regionName, executingPlayer.getWorld().getUID());
		if (success) {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Initialized region '" + regionName + "'."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Region '" + regionName + "' already exists for this world."
			);
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "vertex",
		forCommand = "regions"
	)
	public boolean handleSubCommandVertex(Player executingPlayer, String[] args) {
		if (args.length <= 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			return true;
		}

		String regionName = args[1];
		Location curLoc = executingPlayer.getLocation();

		boolean success = this.regionController.addVertex(regionName, curLoc);
		if (success) {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Vertex (" + curLoc.getBlockX() + "," + curLoc.getBlockZ() + ") added to region '" + regionName + "'."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Unable to add vertex to region '" + regionName + "'."
			);
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "complete",
		forCommand = "regions"
	)
	public boolean handleSubCommandComplete(Player executingPlayer, String[] args) {
		if (args.length <= 1) {
			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
			return true;
		}

		String regionName = args[1];

		boolean success = this.regionController.completeRegion(regionName, executingPlayer.getWorld().getUID());
		if (success) {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Completed region '" + regionName + "'."
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"Unable to complete region '" + regionName + "'."
			);
		}

		return true;
	}

	@GrubsSubcommandHandler(
		name = "list",
		forCommand = "regions"
	)
	public boolean handleSubCommandList(Player executingPlayer, String[] args) {

		String[] regionNames = this.regionController.listRegions(executingPlayer.getWorld().getUID());
		if (regionNames != null) {
			GrubsUtilities.multilinePrint(executingPlayer, "[Regions] ", regionNames);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer,
				GrubsMessager.MessageLevel.INFO,
				"No regions for this world."
			);
		}

		return true;
	}

}
