package com.selfequalsthis.grubsplugin.modules.locks;

import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericPopup;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

public class KeypadPopup implements Listener {
	private int keyWidth = 30;
	private int keyHeight = 30;
	private int keyGap = 10;
	
	protected final Logger logger = Logger.getLogger("Minecraft");
	
	JavaPlugin pluginRef;
	GenericPopup keypad;

	public KeypadPopup(JavaPlugin plugin) {
		this.pluginRef = plugin;
		createKeypadPopup();
	}
	
	private void createKeypadPopup() {
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
	
	public void showKeypadToPlayer(Player player) {
		SpoutPlayer sPlayer = (SpoutPlayer) player;
		sPlayer.getMainScreen().attachPopupScreen(this.keypad);
	}
	
	@EventHandler
	public void onButtonClick(ButtonClickEvent event) {
		if (event.getScreen().getId() == this.keypad.getId()) {
			logger.info(event.getPlayer().getName());
			logger.info(event.getButton().getText());
			logger.info(event.getScreen().getScreenType().toString());
		}
	}
}
