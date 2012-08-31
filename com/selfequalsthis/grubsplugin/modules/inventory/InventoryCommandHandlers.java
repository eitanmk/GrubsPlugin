package com.selfequalsthis.grubsplugin.modules.inventory;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.selfequalsthis.grubsplugin.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class InventoryCommandHandlers extends AbstractGrubsCommandHandler {

	private InventoryModule inventoryModule;
	
	public InventoryCommandHandlers(InventoryModule module) {
		this.moduleRef = module;
		this.inventoryModule = module;
	}
	
	@GrubsCommandHandler(command = "kitget")
	public void onKitGetCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			if (args.length > 0) {
				String argName = args[0];
				if (this.inventoryModule.itemKitPresets.containsKey(argName)) {
					executingPlayer.getInventory().clear();
					executingPlayer.getInventory().setContents(
						this.inventoryModule.itemKitPresets.get(argName)
					);
					executingPlayer.getInventory().setArmorContents(
						this.inventoryModule.itemKitArmorPresets.get(argName)
					);
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INFO,
						"Inventory updated."
					);
				}
				else {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"No kit named '" + argName + "' found."
					);
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
			}
		}
	}
	
	@GrubsCommandHandler(command = "kitset")
	public void onKitSetCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			if (args.length > 0) {
				String argName = args[0];
				if (this.inventoryModule.itemKitPresets.containsKey(argName)) {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.ERROR,
						"Kit '" + argName + "' already exists."
					);
				}
				else {
					this.inventoryModule.itemKitPresets.put(
						argName, executingPlayer.getInventory().getContents()
					);
					this.inventoryModule.itemKitArmorPresets.put(
						argName, executingPlayer.getInventory().getArmorContents()
					);
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INFO,
						"Kit '" + argName + "' saved."
					);
					this.inventoryModule.saveItemKits();
				}
			}
			else {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Missing command argument."
				);
			}
		}
	}
	
	@GrubsCommandHandler(command = "kitlist")
	public void onKitListCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;

		String msgIdentifier = "[Items] ";
		Set<String> keys = this.inventoryModule.itemKitPresets.keySet();

		if (keys.size() > 0) {
			GrubsUtilities.multilinePrint(sender, msgIdentifier, keys.toArray(new String[0]));
		}
		else {
			GrubsMessager.sendMessage(
				sender, 
				GrubsMessager.MessageLevel.ERROR,
				msgIdentifier + "No kits in list."
			);
		}
	}
	
	@GrubsCommandHandler(command = "kitdel")
	public void onKitDeleteCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		String[] args = cmd.args;
		
		if (args.length > 0) {
			String argName = args[0];
			if (this.inventoryModule.itemKitPresets.containsKey(argName)) {
				this.inventoryModule.itemKitPresets.remove(argName);
				this.inventoryModule.itemKitArmorPresets.remove(argName);
				GrubsMessager.sendMessage(
					sender, 
					GrubsMessager.MessageLevel.INFO,
					"Kit '" + argName + "' deleted."
				);
				this.inventoryModule.saveItemKits();
			}
			else {
				GrubsMessager.sendMessage(
					sender, 
					GrubsMessager.MessageLevel.ERROR,
					"No kit named '" + argName + "' found."
				);
			}
		}
		else {
			GrubsMessager.sendMessage(
				sender, 
				GrubsMessager.MessageLevel.ERROR,
				"Missing command argument."
			);
		}
	}
	
	@GrubsCommandHandler(command = "clearinv")
	public void onClearInventoryCommand(GrubsCommandInfo cmd) {
		CommandSender sender = cmd.sender;
		
		if (sender instanceof Player) {
			Player executingPlayer = (Player) sender;
			
			executingPlayer.getInventory().clear();
			executingPlayer.getInventory().setArmorContents(new ItemStack[4]);
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO,
				"Inventory cleared."
			);
		}
	}

}
