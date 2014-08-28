package com.selfequalsthis.grubsplugin.modules.regions;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.UUID;

import org.bukkit.Location;

public class Region implements Serializable {

	private static final long serialVersionUID = 7741887736891122465L;

	private UUID worldId;
	private String name;
	private Polygon polygon;
	private Rectangle bounds;
	private boolean complete = false;
	// TODO min and max Y to constrict region height - default to full world height

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

	// TODO need a "trueContains" function to check polygon surface instead of bounding rect
	public boolean containsLocation(Location loc) {
		if (this.bounds == null) {
			this.bounds = this.polygon.getBounds();
		}
		// TODO will have to include check to make sure location Y val is within height of region
		return this.complete && this.worldId.equals(loc.getWorld().getUID()) && this.bounds.contains(loc.getBlockX(), loc.getBlockZ());
	}

	public void addVertex(Location loc) {
		if (!this.complete) {
			this.polygon.addPoint(loc.getBlockX(), loc.getBlockZ());
			this.polygon.invalidate();
		}
	}

	public void complete() {
		this.complete = true;
		this.bounds = this.polygon.getBounds();
	}

	public boolean isComplete() {
		return this.complete;
	}

	public int getNumVerticies() {
		return this.polygon.npoints;
	}

}
