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

	private PermissionsModule permModule;
	
	public PermissionsCommandHandlers(PermissionsModule module) {
		this.moduleRef = module;
		this.permModule = module;
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
		command = "grant",
		desc = "Tests granting permissions.",
		defaultPermission = "true"
	)
	public void onGrantCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			this.permModule.getAttachment(executingPlayer).setPermission("grubs.command.*", true);
		}
	}
}
