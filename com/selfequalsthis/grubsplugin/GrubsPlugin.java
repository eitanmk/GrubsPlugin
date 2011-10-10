package com.selfequalsthis.grubsplugin; 

import java.util.ArrayList;
import java.util.logging.Logger;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.commands.GrubsLaserTagCommand;
import com.selfequalsthis.grubsplugin.modules.GameFixesModule;
import com.selfequalsthis.grubsplugin.modules.GameInfoModule;
import com.selfequalsthis.grubsplugin.modules.GameTweaksModule;
import com.selfequalsthis.grubsplugin.modules.InventoryModule;
import com.selfequalsthis.grubsplugin.modules.TeleportModule;
import com.selfequalsthis.grubsplugin.modules.WeatherControlModule;

public class GrubsPlugin extends JavaPlugin implements CommandExecutor {
	
	private final Logger log = Logger.getLogger("Minecraft");
	private ArrayList<GrubsModule> modules = new ArrayList<GrubsModule>();

	@Override
	public void onDisable() {		
		for (GrubsModule gm : modules) {
			gm.disable();
		}
		
		log.info("GrubsPlugin is now disabled.");
	}

	@Override
	public void onEnable() {
		log.info("[GrubsPlugin]: Initializing modules.");
		
		modules.add(new WeatherControlModule(this));
		modules.add(new TeleportModule(this));
		modules.add(new InventoryModule(this));
		modules.add(new GameInfoModule(this));
		modules.add(new GameFixesModule(this));
		modules.add(new GameTweaksModule(this));
		/*
		lasertag
		*/
		
		for (GrubsModule gm : modules) {
			gm.enable();
		}
		
		/*
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_BURN, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_IGNITE, blockListener, Priority.Monitor, this);
		pm.registerEvent(Event.Type.BLOCK_PLACE, blockListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.ENTITY_DAMAGE, entityListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Monitor, this);		
		*/
		
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
