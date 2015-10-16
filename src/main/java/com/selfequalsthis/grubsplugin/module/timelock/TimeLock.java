package com.selfequalsthis.grubsplugin.module.timelock;

import org.spongepowered.api.world.World;

public class TimeLock implements Runnable {

	private World world;
	private long time;

	public TimeLock(World world, long time) {
		this.world = world;
		this.time = time;
	}

	@Override
	public void run() {
		this.world.getProperties().setWorldTime(this.time);
	}

}
