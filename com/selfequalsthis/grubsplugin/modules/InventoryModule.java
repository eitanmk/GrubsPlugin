package com.selfequalsthis.grubsplugin.modules;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Set;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import com.selfequalsthis.grubsplugin.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.GrubsMessager;
import com.selfequalsthis.grubsplugin.GrubsUtilities;

public class InventoryModule extends AbstractGrubsModule {
	
	private HashMap<String,ItemStack[]> itemKitPresets = new HashMap<String,ItemStack[]>();
	private HashMap<String,ItemStack[]> itemKitArmorPresets = new HashMap<String,ItemStack[]>();
	private Properties itemKitProperties = new Properties(); 
	
	private String itemKitMainDirectory = "plugins/ItemKitPresets";
	private File ItemKitPresetFile = new File(itemKitMainDirectory + File.separator + "presets.dat");
	
	public InventoryModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[InventoryModule]: ";
		this.dataFileName = "itemkits.dat";
	}
	
	@Override
	public void enable() {		
		this.registerCommand("kitget");
		this.registerCommand("kitset");
		this.registerCommand("kitlist");
		this.registerCommand("kitdel");
		this.registerCommand("clearinv");
		
		File dataFile = this.getDataFile();
		if (dataFile != null) {
			
			if (ItemKitPresetFile.exists()){
				this.log("Old preset file exists. Moving to new location.");
				boolean succeeded = ItemKitPresetFile.renameTo(dataFile);
				if (!succeeded) {
					this.log("Failed to move preset file to new location!");
					return;
				}
			}
			else {
				this.log("Can remove the old data file code. It's been migrated already.");
			}
			
			this.log("Loading Item Kit presets.");
			loadItemKits();
			this.log("Loaded " + itemKitPresets.size() + " presets.");
		}
	}

	@Override
	public void disable() {
		this.log("Saving Item Kit presets.");
		saveItemKits();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		String cmdName = command.getName();
		Player executingPlayer = (Player) sender;
		
		if (!executingPlayer.isOp()) {
			return false;
		}
		
		if (cmdName.equalsIgnoreCase("kitget")) {
			this.handleKitGet(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("kitset")) {
			this.handleKitSet(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("kitdel")) {
			this.handleKitDelete(args, executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("kitlist")) {
			this.handleKitList(executingPlayer);
		}
		else if (cmdName.equalsIgnoreCase("clearinv")) {
			this.handleClearInventory(executingPlayer);
		}

		this.log(executingPlayer.getDisplayName() + ": " + cmdName + " " + GrubsUtilities.join(args, " "));

		return true;
	}
	
	private void handleKitGet(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			String argName = args[0];
			if (itemKitPresets.containsKey(argName)) {
				executingPlayer.getInventory().clear();
				executingPlayer.getInventory().setContents(itemKitPresets.get(argName));
				executingPlayer.getInventory().setArmorContents(itemKitArmorPresets.get(argName));
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
	
	private void handleKitSet(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			String argName = args[0];
			if (itemKitPresets.containsKey(argName)) {
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.ERROR,
					"Kit '" + argName + "' already exists."
				);
			}
			else {
				itemKitPresets.put(argName, executingPlayer.getInventory().getContents());
				itemKitArmorPresets.put(argName, executingPlayer.getInventory().getArmorContents());
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					"Kit '" + argName + "' saved."
				);
				saveItemKits();
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
	
	private void handleKitDelete(String[] args, Player executingPlayer) {
		if (args.length > 0) {
			String argName = args[0];
			if (itemKitPresets.containsKey(argName)) {
				itemKitPresets.remove(argName);
				itemKitArmorPresets.remove(argName);
				GrubsMessager.sendMessage(
					executingPlayer, 
					GrubsMessager.MessageLevel.INFO,
					"Kit '" + argName + "' deleted."
				);
				saveItemKits();
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
	
	private void handleKitList(Player executingPlayer) {
		boolean useSeparator = false;
		String msgIdentifier = "[Items] ";
		String list = "";
		Set<String> keys = itemKitPresets.keySet();

		if (keys.size() > 0) {
			for (String s : keys) {
				if ( (msgIdentifier.length() + list.length() + 2 + s.length()) > 60) {
					GrubsMessager.sendMessage(
						executingPlayer, 
						GrubsMessager.MessageLevel.INFO,
						msgIdentifier + list
					);
					list = "";
					useSeparator = false;
				}
				
				if (useSeparator) {
					list += ", ";
				}
				else {
					useSeparator = true;
				}
				
				
				list += s;	
			}

			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.INFO,
				msgIdentifier + list
			);
		}
		else {
			GrubsMessager.sendMessage(
				executingPlayer, 
				GrubsMessager.MessageLevel.ERROR,
				msgIdentifier + "No kits in list."
			);
		}
	}
	
	private void handleClearInventory(Player executingPlayer) {
		executingPlayer.getInventory().clear();
		executingPlayer.getInventory().setArmorContents(new ItemStack[4]);
		GrubsMessager.sendMessage(
			executingPlayer, 
			GrubsMessager.MessageLevel.INFO,
			"Inventory cleared."
		);
	}
	
	
	

	private void loadItemKits() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing can be loaded!");
			return;
		}
		
		try {
			FileInputStream in = new FileInputStream(dataFile);
			itemKitProperties.load(in);
			in.close();
			
			for (Object key : itemKitProperties.keySet()) {
				String realKey = (String) key;
				String rawValue = itemKitProperties.getProperty(realKey);
				
				ItemStack[] kitItems = new ItemStack[36];
				ItemStack[] armorItems = new ItemStack[4];

				String[] parts = rawValue.split("\\^");

				// set up inventory part
				if (parts[0].length() > 0) {
					String[] invParts = parts[0].split(",");
					for (int i=0; i < invParts.length; ++i) {
						String[] curInvTuple = invParts[i].split(":");
						
						// if spot was empty, it won't be listed
						int index = Integer.parseInt(curInvTuple[0]);
						int materialId = Integer.parseInt(curInvTuple[1]);
						int amt = Integer.parseInt(curInvTuple[2]);
						
						kitItems[index] = new ItemStack(materialId, amt);
					}
				}
				
				if (parts[1].length() > 0) {					
					String[] armorParts = parts[1].split(",");
					for (int i=0; i < armorParts.length; ++i) {
						String[] curArmorTuple = armorParts[i].split(":");
						
						int index = Integer.parseInt(curArmorTuple[0]);
						int materialId = Integer.parseInt(curArmorTuple[1]);
						int amt = Integer.parseInt(curArmorTuple[2]);
						
						armorItems[index] = new ItemStack(materialId, amt);
					}
				}
				
				itemKitPresets.put(realKey, kitItems);
				itemKitArmorPresets.put(realKey, armorItems);
			}
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void saveItemKits() {
		File dataFile = this.getDataFile();
		if (dataFile == null) {
			this.log("Error with data file. Nothing will be saved!");
			return;
		}
		
		String settingStr = "";
		boolean useSeparator = false;
				
		itemKitProperties.clear();
		
		for (String s : itemKitPresets.keySet()) {
			ItemStack[] curStackList = itemKitPresets.get(s);
			
			settingStr = "";
			useSeparator = false;
			
			for (int i=0; i < curStackList.length; ++i) {
				ItemStack curStackItem = curStackList[i];

				if (curStackItem == null) {
					continue;
				}
				
				if (useSeparator) {
					settingStr += ",";
				}
				else {
					useSeparator = true;
				}
				
				settingStr += "" + i + ":" + curStackItem.getTypeId() + ":" + curStackItem.getAmount();
			}
			
			settingStr += "^";
			useSeparator = false;
			
			ItemStack[] curArmorStackList = itemKitArmorPresets.get(s);
			
			for (int i=0; i < curArmorStackList.length; ++ i) {
				ItemStack curArmorStackItem = curArmorStackList[i];
				
				if (curArmorStackItem == null) {
					continue;
				}
				
				if (useSeparator) {
					settingStr += ",";
				}
				else {
					useSeparator = true;
				}
				
				settingStr += "" + i + ":" + curArmorStackItem.getTypeId() + ":" + curArmorStackItem.getAmount();
			}
			
			itemKitProperties.put(s, settingStr);
		}
		
		this.log("Writing Item Kit presets file.");
		try {
			FileOutputStream out = new FileOutputStream(dataFile);
			itemKitProperties.store(out, "Do NOT edit this file manually!");
			out.flush();
			out.close();
		}
		catch (IOException ex) {
			this.log("Error writing Item Kit presets file!");
			ex.printStackTrace();
		}
	}
}
