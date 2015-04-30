package com.selfequalsthis.grubsplugin.modules.timelock;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.SynchronousScheduler;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.World;

import com.google.common.base.Optional;
import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class TimeLockModule extends AbstractGrubsModule {

	private Task timeLockTask = null;

	public TimeLockModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[GameInfoModule]: ";

		this.commandHandlers.add(new TimeLockCommandTimelock(this, this.game));
	}

	@Override
	public void enable() {
		this.registerCommands(this.commandHandlers);
	}

	@Override
	public void disable() {
		this.unlockTime();
		this.unregisterCommands(this.commandHandlers);
	}

	public void lockTime(World world, long time) {
		this.unlockTime();
		world.getProperties().setWorldTime(time);
		Optional<SynchronousScheduler> scheduler = this.game.getServiceManager().provide(SynchronousScheduler.class);
		if (scheduler.isPresent()) {
			Optional<Task> scheduledTask = scheduler.get().runRepeatingTaskAfter(this.pluginRef, new TimeLock(world, time), 100L, 10L);
			if (scheduledTask.isPresent()) {
				this.timeLockTask = scheduledTask.get();
			}
			else {
				this.log("Failed to start repeating task.");
			}
		}
	}

	public void unlockTime() {
		if (this.timeLockTask != null) {
			this.timeLockTask.cancel();
			this.timeLockTask = null;
		}
	}
}
