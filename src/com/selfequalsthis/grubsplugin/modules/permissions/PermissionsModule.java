package com.selfequalsthis.grubsplugin.modules.permissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class PermissionsModule extends AbstractGrubsModule {

	private PermissionsCommandHandlers commandHandlers;
	private PermissionsEventListeners eventListeners;
	private HashMap<String,PermissionAttachment> playerAttachments = new HashMap<String,PermissionAttachment>();
	private GroupManager groupManager = new GroupManager(this);
	
	public PermissionsModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[PermissionsModule]: ";
		this.dataFileName = "permission_groups.yml";
		this.eventListeners = new PermissionsEventListeners(this);
		this.commandHandlers = new PermissionsCommandHandlers(this, this.groupManager);
	}
	
	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);
		this.loadPermissionGroups();
	}
	
	@Override
	public void disable() {
		this.log("Saving permission groups.");
		this.savePermissionGroups();
	}

	private void loadPermissionGroups() {
		File dataFile = this.getDataFile();
		if (dataFile != null) {
			try {
				FileInputStream in = new FileInputStream(dataFile);
				this.log("Loading permission groups.");
				this.groupManager.loadYaml(in);
				this.log("Loaded " + this.groupManager.getGroupCount() + " groups.");
			}
			catch(IOException ex) {
				this.log("Error reading permission groups file!");
				ex.printStackTrace();
			}
		}
	}

	public void savePermissionGroups() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing will be saved!");
			return;
		}
		
		this.log("Writing permission groups file.");
		try {
			FileOutputStream out = new FileOutputStream(dataFile);
			OutputStreamWriter writer = new OutputStreamWriter(out);
			this.groupManager.dumpYaml(writer);
		}
		catch (IOException ex) {
			this.log("Error writing permission groups file!");
			ex.printStackTrace();
		}
	}

	public PermissionAttachment getAttachment(Player player) {
		String lowerName = player.getName().toLowerCase();
		
		if (this.playerAttachments.containsKey(lowerName)) {
			return this.playerAttachments.get(lowerName);
		}
		
		PermissionAttachment newAttachment = player.addAttachment(this.pluginRef);
		this.playerAttachments.put(lowerName, newAttachment);
		return newAttachment;
	}

	public void setupPlayerPermissions(Player p) {
		// we know player is online since they just joined
		this.logger.info(this.groupManager.getGroupsForPlayer(p.getDisplayName()).toString());
		PermissionAttachment attachment = this.getAttachment(p);
		ArrayList<String> playerGroups = this.groupManager.getGroupsForPlayer(p.getDisplayName());
		for (String groupName : playerGroups) {
			for (String permissionStr : this.groupManager.getGroup(groupName).getPermissions()) {
				attachment.setPermission(permissionStr, true);
			}
		}
	}
}
