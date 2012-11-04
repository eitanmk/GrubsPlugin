package com.selfequalsthis.grubsplugin.modules.permissions;

import java.util.ArrayList;

public class Group {
	
	private ArrayList<String> players = new ArrayList<String>();
	private ArrayList<String> permissions = new ArrayList<String>();

	public ArrayList<String> getPlayers() {
		return copy(this.players);
	}
	
	public ArrayList<String> getPermissions() {
		return copy(this.permissions);
	}
	
	public boolean hasPlayer(String playerName) {
		return this.players.contains(playerName);
	}

	public void addPlayer(String playerName) {
		if (!this.hasPlayer(playerName)) {
			this.players.add(playerName);
		}
	}

	public void removePlayer(String playerName) {
		if (this.hasPlayer(playerName)) {
			this.players.remove(playerName);
		}
	}

	private boolean hasPermission(String permission) {
		return this.permissions.contains(permission);
	}
	
	public void setPermission(String permission) {
		if (!this.hasPermission(permission)) {
			this.permissions.add(permission);
		}
	}

	public void unsetPermission(String permission) {
		if (this.hasPermission(permission)) {
			this.permissions.remove(permission);
		}
	}

	public String dumpState() {
		String retStr = "";
		retStr += "Players: ";
		for (String playerName : this.players) {
			retStr += playerName + " ";
		}
		retStr += "\nPermissions:\n";
		for (String permissionStr : this.permissions) {
			retStr += "  - " + permissionStr + "\n";
		}
		return retStr;
	}
	
	private ArrayList<String> copy(ArrayList<String> list) {
		ArrayList<String> copiedList = new ArrayList<String>();
		for (int i = 0, len = list.size(); i < len; ++i) {
			copiedList.add(list.get(i));
		}
		return copiedList;
	}

}
