package com.selfequalsthis.grubsplugin; 

import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.commands.GrubsEjectCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsInfoCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsItemCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsLaserTagCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsObsidianBuildModeCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsTeleportCommand;
import com.selfequalsthis.grubsplugin.commands.GrubsWeatherCommand;
import com.selfequalsthis.grubsplugin.listeners.GrubsBlockListener;
import com.selfequalsthis.grubsplugin.listeners.GrubsEntityListener;
import com.selfequalsthis.grubsplugin.listeners.GrubsPlayerListener;
import com.selfequalsthis.grubsplugin.listeners.GrubsWeatherListener;

public class GrubsPlugin extends JavaPlugin implements CommandExecutor {
	private final Logger log = Logger.getLogger("Minecraft");
	private final GrubsBlockListener blockListener = new GrubsBlockListener();
	private final GrubsEntityListener entityListener = new GrubsEntityListener();
	private final GrubsPlayerListener playerListener = new GrubsPlayerListener();
	private final GrubsWeatherListener weatherListener = new GrubsWeatherListener();
	

	@Override
	public void onDisable() {
		GrubsTeleportCommand.disable();
		GrubsItemCommand.disable();
		
		log.info("GrubsPlugin is now disabled.");
	}

	@Override
	public void onEnable() {
		log.info("[GrubsPlugin]: Initializing listeners.");
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.WEATHER_CHANGE, weatherListener, Priority.Monitor, this);
		
		GrubsTeleportCommand.enable(getServer());
		GrubsItemCommand.enable(getServer());
		
		// done!
		log.info("GrubsPlugin is enabled.");
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command name, String label, String[] args) {
		String cmdName = name.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return true;
		}
		
		Server server = getServer();
		GrubsCommandHandler handler = null;
		
		if (cmdName.equalsIgnoreCase("obm")) {
			handler = GrubsObsidianBuildModeCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("strike") || cmdName.equalsIgnoreCase("zap") ||
			cmdName.equalsIgnoreCase("storm") || cmdName.equalsIgnoreCase("thunder")) {
			handler = GrubsWeatherCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("goto") || cmdName.equalsIgnoreCase("fetch") ||
			cmdName.equalsIgnoreCase("send") || cmdName.equalsIgnoreCase("tpset") ||
			cmdName.equalsIgnoreCase("tpdel") || cmdName.equalsIgnoreCase("tplist")) {
			handler = GrubsTeleportCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("eject")) {
			handler = GrubsEjectCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("dataval") || cmdName.equalsIgnoreCase("dataname") ||
			cmdName.equalsIgnoreCase("gettime") ||
			cmdName.equalsIgnoreCase("getcoords") || cmdName.equalsIgnoreCase("sendcoords")) {
			handler = GrubsInfoCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("kitset") || cmdName.equalsIgnoreCase("kitget") ||
			cmdName.equalsIgnoreCase("kitdel") || cmdName.equalsIgnoreCase("kitlist") ||
			cmdName.equalsIgnoreCase("clearinv")) {
			handler = GrubsItemCommand.getInstance();
		}
		if (cmdName.equalsIgnoreCase("lasertag")) {
			handler = GrubsLaserTagCommand.getInstance();
		}
		
		if (handler != null) {
			return handler.processCommand(server, executingPlayer, cmdName, args);
		}
		else {
			return false;
		}
	}

}
