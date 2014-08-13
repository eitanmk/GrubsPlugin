package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class GameTweaksModule extends AbstractGrubsModule {

	private int timeLockTaskId = -1;

	private GameTweaksEventListeners eventListeners;
	private GameTweaksCommandHandlers commandHandlers;

	public GameTweaksModule(JavaPlugin plugin) {
		this.pluginRef = plugin;
		this.logPrefix = "[GameTweaksModule]: ";
		this.eventListeners = new GameTweaksEventListeners();
		this.commandHandlers = new GameTweaksCommandHandlers(this);
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
		this.registerEventHandlers(this.eventListeners);
	}

	@Override
	public void disable() {
		this.unlockTime();
		this.unregisterCommands(this.commandHandlers);
		this.unregisterEventHandlers(this.eventListeners);
	}

	public void lockTime(World world, long time) {
		this.unlockTime();
		world.setTime(time);
		this.timeLockTaskId = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(
			this.pluginRef,
			new TimeLock(world, time),
			10L,
			100L
		);
	}

	public void unlockTime() {
		if (this.timeLockTaskId != -1) {
			Bukkit.getServer().getScheduler().cancelTask(this.timeLockTaskId);
			this.timeLockTaskId = -1;
		}
	}
}
