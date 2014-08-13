package com.selfequalsthis.grubsplugin.modules.inventory;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.selfequalsthis.grubsplugin.annotations.GrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.AbstractGrubsCommandHandler;
import com.selfequalsthis.grubsplugin.command.GrubsCommandInfo;
import com.selfequalsthis.grubsplugin.utils.GrubsMessager;
import com.selfequalsthis.grubsplugin.utils.GrubsUtilities;

public class InventoryCommandHandlers extends AbstractGrubsCommandHandler {

	private InventoryModule inventoryModule;

	public InventoryCommandHandlers(InventoryModule module) {
		this.moduleRef = module;
		this.inventoryModule = module;
	}

	@GrubsCommandHandler(
		command = "kitget",
		desc = "Load a kit into your inventory.",
		usage = "/<command> <preset name>"
	)
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

	@GrubsCommandHandler(
		command = "kitset",
		desc = "Create a new item kit preset based on current inventory.",
		usage = "/<command> <preset name>"
	)
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

	@GrubsCommandHandler(
		command = "kitlist",
		desc = "List all saved kit presets."
	)
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

	@GrubsCommandHandler(
		command = "kitdel",
		desc = "Delete a saved kit preset.",
		usage = "/<command> <preset name>"
	)
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

	@GrubsCommandHandler(
		command = "clearinv",
		desc = "Empty out current inventory."
	)
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
