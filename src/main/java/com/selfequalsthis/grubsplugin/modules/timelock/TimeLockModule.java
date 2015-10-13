package com.selfequalsthis.grubsplugin.modules.timelock;

import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Game;
import org.spongepowered.api.service.scheduler.Task;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.GrubsPlugin;
import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;

public class TimeLockModule extends AbstractGrubsModule {

	private Task timeLockTask = null;

	public TimeLockModule(GrubsPlugin plugin, Game game) {
		this.pluginRef = plugin;
		this.game = game;
		this.logger = plugin.getLogger();
		this.logPrefix = "[GameInfoModule]: ";

		this.commandHandlers = new TimeLockCommandHandlers(this, this.game);
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
		this.timeLockTask = this.game.getScheduler().createTaskBuilder()
			.execute(new TimeLock(world, time))
			.interval(5L, TimeUnit.SECONDS)
			.delay(10L, TimeUnit.MILLISECONDS)
			.name("TimeLockModule - Time Lock Task")
			.submit(this.pluginRef);
	}

	public void unlockTime() {
		if (this.timeLockTask != null) {
			this.timeLockTask.cancel();
			this.timeLockTask = null;
		}
	}
}
