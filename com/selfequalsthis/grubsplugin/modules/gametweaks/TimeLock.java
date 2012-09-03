package com.selfequalsthis.grubsplugin.modules.gametweaks;

import org.bukkit.World;

public class TimeLock implements Runnable {
	
	private World world;
	private long time;

	public TimeLock(World world, long time) {
		this.world = world;
		this.time = time;
	}
	
	@Override
	public void run() {
		this.world.setTime(time);
	}

}
