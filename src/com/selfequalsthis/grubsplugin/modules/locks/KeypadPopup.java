package com.selfequalsthis.grubsplugin.modules.locks;

import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class KeypadPopup {
		
	private int keyWidth = 30;
	private int keyHeight = 30;
	private int keyGap = 10;
	
	private SpoutPlayer player;
	private Inventory inventory;
	private String combination;
	private String guess;
		
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
		
		for (i = 1; i <= 9; ++i) {
			GenericButton button = new GenericButton("" + i);
			button.setAnchor(WidgetAnchor.CENTER_CENTER);
			button.setWidth(this.keyWidth).setHeight(this.keyHeight);
			button.shiftXPos(-1 * (this.keyWidth / 2)).shiftYPos(-1 * (this.keyHeight / 2));
			
			if (i % 3 == 1) {
				button.shiftXPos(-1 * (this.keyWidth + this.keyGap));
			}
			if (i % 3 == 0) {
				button.shiftXPos(this.keyWidth + this.keyGap);
			}
			
			if (i <= 3) {
				button.shiftYPos(-1 * (this.keyHeight + this.keyGap));
			}
			if (i >= 7) {
				button.shiftYPos(this.keyHeight + this.keyGap);
			}
			
			this.keypad.attachWidget(this.pluginRef, button);
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
