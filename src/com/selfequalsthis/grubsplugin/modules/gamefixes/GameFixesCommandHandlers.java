package com.selfequalsthis.grubsplugin.modules.gamefixes;

import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class GameFixesCommandHandlers extends AbstractGrubsCommandHandler {

	public GameFixesCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@GrubsCommandHandler(
		command = "eject",
		desc = "Eject from a vehicle."
	)
	public void onEjectCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			if (executingPlayer.isInsideVehicle()) {
				executingPlayer.leaveVehicle();
			}
			else {
				GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.ERROR, "You are not in a vehicle.");
			}
		}
	}

	@GrubsCommandHandler(
		command = "getchunks",
		desc = "Reload chunks from the server if some are missing."
	)
	public void onGetChunksCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;

			World world = executingPlayer.getWorld();
			Chunk playerChunk = world.getChunkAt(executingPlayer.getLocation());

			int playerChunkX = playerChunk.getX();
			int playerChunkZ = playerChunk.getZ();

			int startX = playerChunkX - 1;
			int startZ = playerChunkZ - 1;
			int endX  = playerChunkX + 1;
			int endZ  = playerChunkZ + 1;

			for (int x = startX; x <= endX; ++x) {
				for (int z = startZ; z <= endZ; ++z) {
					world.refreshChunk(x, z);
				}
			}

			GrubsMessager.sendMessage(executingPlayer, GrubsMessager.MessageLevel.INFO, "Chunks re-sent.");
		}
	}

}
