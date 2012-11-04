package com.selfequalsthis.grubsplugin.modules.permissions;

import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;

public class PermissionsModule extends AbstractGrubsModule {

	private PermissionsCommandHandlers commandHandlers;
	private HashMap<String,PermissionAttachment> playerAttachments = new HashMap<String,PermissionAttachment>();
	private GroupManager groupManager = new GroupManager(this);
	
	public PermissionsModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[PermissionsModule]: ";
		this.dataFileName = "permissions.dat";
		this.commandHandlers = new PermissionsCommandHandlers(this, this.groupManager);
	}
	
	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}
	
	@Override
	public void disable() {
		
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
}
