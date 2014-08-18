package com.selfequalsthis.grubsplugin.modules.weathercontrol;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;

public class WeatherControlCommandHandlers extends AbstractGrubsCommandHandler {

	public WeatherControlCommandHandlers(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	@GrubsCommandHandler(
		command = "strike",
		desc = "Hit targeted block or a player with lightening, but do no damage.",
		usage = "/<command> [<player>]"
	)
	public void onStrikeCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"Player '" + args[0] + "' not found."
				);
			}
			else {
				Location playerLoc = target.getLocation();
				World targetWorld = target.getWorld();
				targetWorld.strikeLightningEffect(playerLoc);
			}
		}
		else {
			// aim for cursor
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;

				Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
				World targetWorld = executingPlayer.getWorld();
				targetWorld.strikeLightningEffect(target);
			}
		}
	}

	@GrubsCommandHandler(
		command = "zap",
		desc = "Hit targeted block or a player with lightening, doing damage.",
		usage = "/<command> [<player>]"
	)
	public void onZapCommand(CommandSender sender, Command command, String alias, String[] args) {

		if (args.length > 0) {
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				GrubsMessager.sendMessage(
					sender,
					GrubsMessager.MessageLevel.ERROR,
					"Player '" + args[0] + "' not found."
				);
			}
			else {
				Location playerLoc = target.getLocation();
				World targetWorld = target.getWorld();
				targetWorld.strikeLightning(playerLoc);
			}
		}
		else {
			// aim for cursor
			if (sender instanceof Player) {
				Player executingPlayer = (Player) sender;

				Location target = executingPlayer.getTargetBlock(null, 256).getLocation();
				World targetWorld = executingPlayer.getWorld();
				targetWorld.strikeLightning(target);
			}
		}
	}

}
