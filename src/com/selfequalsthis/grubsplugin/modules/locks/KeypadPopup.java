package com.selfequalsthis.grubsplugin.modules.locks;

import java.util.logging.Logger;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class KeypadPopup {
		
	protected final Logger logger = Logger.getLogger("Minecraft");
	
	private int keyWidth = 30;
	private int keyHeight = 30;
	private int keyGap = 10;
	
	private SpoutPlayer player;
	private Inventory inventory;
	private String combination;
	private String guess;
	
	private GenericLabel message;
		
	JavaPlugin pluginRef;
	GenericPopup keypad;

	public KeypadPopup(JavaPlugin plugin, SpoutPlayer player, Inventory inventory) {
		this.pluginRef = plugin;
		this.player = player;
		this.inventory = inventory;
		this.combination = "1256";
		this.guess = "";
	}
	
	public void createKeypadPopup() {
		int i;
		this.keypad = new GenericPopup();
		
		String labelText = "This inventory is locked.";
		GenericLabel label = new GenericLabel(labelText);
		label.setAnchor(WidgetAnchor.CENTER_CENTER);
		label.setWidth(GenericLabel.getStringWidth(labelText));
		label.setHeight(GenericLabel.getStringHeight(labelText));
		label.shiftXPos(-1 * (label.getWidth() / 2));
		label.shiftYPos(-1 * (2 * this.keyHeight + 3 * this.keyGap));
		this.keypad.attachWidget(this.pluginRef, label);
		this.message = label;
		
		for (i = 1; i <= 10; ++i) {
			int num = i % 10;
			GenericButton button = new GenericButton("" + num);
			button.setAnchor(WidgetAnchor.CENTER_CENTER);
			button.setWidth(this.keyWidth).setHeight(this.keyHeight);
			button.shiftXPos(-1 * (this.keyWidth / 2));
			button.shiftYPos(-1 * (this.keyHeight + this.keyGap / 2));
			this.keypad.attachWidget(this.pluginRef, button);
			
			if (num == 0) {
				button.shiftYPos(2 * this.keyHeight + 2 * this.keyGap);
				continue;
			}
			
			if (num % 3 == 1) {
				button.shiftXPos(-1 * (this.keyWidth + this.keyGap));
			}
			if (num % 3 == 0) {
				button.shiftXPos(this.keyWidth + this.keyGap);
			}
			
			if (num <= 3) {
				button.shiftYPos(-1 * (this.keyHeight + this.keyGap));
			}
			if (num >= 7) {
				button.shiftYPos(this.keyHeight + this.keyGap);
			}
		}
	}
	
	public void showKeypadToPlayer() {
		this.player.getMainScreen().attachPopupScreen(this.keypad);
	}
	
	public void addToGuess(String digit) {
		this.guess += digit;
	}
	
	public void resetGuess() {
		this.guess = "";
		this.message.setText("Incorrect code. Try again.");
	}
	
	public boolean codeCorrect() {
		if (this.combination.compareTo(this.guess) == 0) {
			return true;
		}
		
		if (this.guess.length() == 4) {
			this.resetGuess();
		}
		
		return false;
	}

	public void hideKeypad() {
		this.player.getMainScreen().closePopup();
	}

	public void showInventory() {
		this.player.openInventory(this.inventory);
	}
}
