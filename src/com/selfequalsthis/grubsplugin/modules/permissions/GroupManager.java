package com.selfequalsthis.grubsplugin.modules.permissions;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

public class GroupManager {
	
	private PermissionsModule moduleRef;
	private HashMap<String,Group> groups = new HashMap<String,Group>();
	
	public GroupManager(PermissionsModule module) {
		this.moduleRef = module;
	}
	
	public boolean hasGroup(String name) {
		return this.groups.containsKey(name);
	}
	
	public void createGroup(String name) {
		if (!this.hasGroup(name)) {
			this.groups.put(name, new Group());
		}
	}
	
	public void deleteGroup(String name) {
		if (this.hasGroup(name)) {
			Group group = this.groups.get(name);
			for (String playerName : group.getPlayers()) {
				Player playerObj = Bukkit.getPlayer(playerName);
				if (playerObj != null) {
					// they are online
					PermissionAttachment attachment = this.moduleRef.getAttachment(playerObj);
					for (String permissionStr : group.getPermissions()) {
						attachment.unsetPermission(permissionStr);
					}
				}
			}
			
			this.groups.remove(name);
		}
	}

	public void addPlayerToGroup(String playerName, String groupName) {
		if (this.hasGroup(groupName)) {
			Group group = this.groups.get(groupName);
			group.addPlayer(playerName);
			
			Player playerObj = Bukkit.getPlayer(playerName);
			if (playerObj != null) {
				PermissionAttachment attachment = this.moduleRef.getAttachment(playerObj);
				for (String permissionStr : group.getPermissions()) {
					attachment.setPermission(permissionStr, true);
				}
			}
		}
	}

	public void removePlayerFromGroup(String playerName, String groupName) {
		if (this.hasGroup(groupName)) {
			Group group = this.groups.get(groupName);
			group.removePlayer(playerName);
			
			Player playerObj = Bukkit.getPlayer(playerName);
			if (playerObj != null) {
				PermissionAttachment attachment = this.moduleRef.getAttachment(playerObj);
				for (String permissionStr : group.getPermissions()) {
					attachment.unsetPermission(permissionStr);
				}
			}
		}
	}

	public void setPermissionOnGroup(String permission, String groupName) {
		if (this.hasGroup(groupName)) {
			Group group = this.groups.get(groupName);
			group.setPermission(permission);
			
			for (String playerName : group.getPlayers()) {
				Player playerObj = Bukkit.getPlayer(playerName);
				if (playerObj != null) {
					// they are online
					PermissionAttachment attachment = this.moduleRef.getAttachment(playerObj);
					attachment.setPermission(permission, true);
				}
			}
		}
	}

	public void unsetPermissionOnGroup(String permission, String groupName) {
		if (this.hasGroup(groupName)) {
			Group group = this.groups.get(groupName);
			group.unsetPermission(permission);
			
			for (String playerName : group.getPlayers()) {
				Player playerObj = Bukkit.getPlayer(playerName);
				if (playerObj != null) {
					// they are online
					PermissionAttachment attachment = this.moduleRef.getAttachment(playerObj);
					attachment.unsetPermission(permission);
				}
			}
		}
	}

	public String dumpSettings() {
		String retStr = "";
		for (String key : this.groups.keySet()) {
			retStr += "Group " + key + ":\n";
			retStr += this.groups.get(key).dumpState();
		}
		return retStr;
	}
}
