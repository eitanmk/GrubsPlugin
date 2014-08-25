package com.selfequalsthis.grubsplugin.modules.regions;

import java.awt.Polygon;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;

public class Region implements Serializable {

	private static final long serialVersionUID = 7741887736891122465L;

	private UUID worldId;
	private String name;
	public Polygon polygon;
	private boolean complete = false;

	public Region(String regionName, UUID worldId) {
		this.name = regionName;
		this.worldId = worldId;
		this.polygon = new Polygon();
	}

	public String getName() {
		return this.name;
	}

	public UUID getWorldUID() {
		return this.worldId;
	}

	public boolean containsLocation(Location loc) {
		return this.worldId == loc.getWorld().getUID() && this.polygon.contains(loc.getBlockX(), loc.getBlockZ());
	}

	public void addVertex(Location loc) {
		if (!this.complete) {
			this.polygon.addPoint(loc.getBlockX(), loc.getBlockZ());
			this.polygon.invalidate();
		}
	}

	public void complete() {
		this.complete = true;
	}

	public boolean isComplete() {
		return this.complete;
	}

}
