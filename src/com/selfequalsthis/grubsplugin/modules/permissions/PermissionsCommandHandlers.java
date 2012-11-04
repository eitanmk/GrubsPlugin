package com.selfequalsthis.grubsplugin.modules.permissions;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsMessager.MessageLevel;
import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;

public class PermissionsCommandHandlers extends AbstractGrubsCommandHandler {

	private GroupManager groupManager;
	
	public PermissionsCommandHandlers(PermissionsModule module, GroupManager groupManager) {
		this.moduleRef = module;
		this.groupManager = groupManager;
	}

	@GrubsCommandHandler(
		command = "listperms",
		desc = "Lists all permissions for the calling player.",
		defaultPermission = "true"
	)
	public void onListPermsCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			Set<PermissionAttachmentInfo> perms = executingPlayer.getEffectivePermissions();
			
			for (PermissionAttachmentInfo info : perms) {
				GrubsMessager.sendMessage(executingPlayer, MessageLevel.INFO, info.getPermission());
			}
		}
	}
	
	@GrubsCommandHandler(
		command = "perms",
		desc = "Manage groups and permissions.",
		usage = "/<command> [create|delete|add|remove|set|unset] <arguments>",
		defaultPermission = "op"
	)
	public void onPermsCommand(GrubsCommandInfo cmd) {	
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
			
		if (args.length == 0) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Not enough arguments.");
		}
		
		String subCommand = args[0];
		
		if (subCommand.equalsIgnoreCase("create")) {
			this.handleGroupCreate(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("delete")) {
			this.handleGroupDelete(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("add")) {
			this.handlePlayerAddToGroup(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("remove")) {
			this.handlePlayerRemoveFromGroup(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("set")) {
			this.handleSetPermissionOnGroup(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("unset")) {
			this.handleUnsetPermissionFromGroup(args, sender);
		}
		else if (subCommand.equalsIgnoreCase("dump")) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.INFO, this.groupManager.dumpSettings());
		}
		else {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Unknown subcommand.");
		}
	}
	
	private void handleGroupCreate(String[] args, CommandSender sender) {
		if (args.length != 2) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String groupName = args[1];
		this.groupManager.createGroup(groupName);
	}
	
	private void handleGroupDelete(String[] args, CommandSender sender) {
		if (args.length != 2) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String groupName = args[1];
		this.groupManager.deleteGroup(groupName);
	}
	
	private void handlePlayerAddToGroup(String[] args, CommandSender sender) {
		if (args.length != 3) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String playerName = args[1];
		String groupName = args[2];
		this.groupManager.addPlayerToGroup(playerName, groupName);
	}

	private void handlePlayerRemoveFromGroup(String[] args,	CommandSender sender) {
		if (args.length != 3) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String playerName = args[1];
		String groupName = args[2];
		this.groupManager.removePlayerFromGroup(playerName, groupName);
	}

	private void handleSetPermissionOnGroup(String[] args, CommandSender sender) {
		if (args.length != 3) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String permission = args[1];
		String groupName = args[2];
		this.groupManager.setPermissionOnGroup(permission, groupName);
	}
	
	private void handleUnsetPermissionFromGroup(String[] args, CommandSender sender) {
		if (args.length != 3) {
			GrubsMessager.sendMessage(sender, GrubsMessager.MessageLevel.ERROR, "Incorrect number of arguments.");
			return;
		}
		
		String permission = args[1];
		String groupName = args[2];
		this.groupManager.unsetPermissionOnGroup(permission, groupName);
	}

}
