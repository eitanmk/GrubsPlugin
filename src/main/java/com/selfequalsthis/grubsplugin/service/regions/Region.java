package com.selfequalsthis.grubsplugin.service.regions;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.UUID;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

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

	public boolean containsLocation(Location<World> loc, boolean useBoundingBox) {
		// TODO will have to include check to make sure location Y val is within height of region
		if (this.bounds == null) {
			this.bounds = this.polygon.getBounds();
		}

		// region not done, pretend like it's not here
		if (!this.complete) {
			return false;
		}

		// region from a different world, so we're not inside it
		if (!this.worldId.equals(loc.getExtent().getUniqueId())) {
			return false;
		}

		if (useBoundingBox) {
			return this.bounds.contains(loc.getBlockX(), loc.getBlockZ());
		}
		else {
			return this.polygon.contains(loc.getBlockX(), loc.getBlockZ());
		}
	}

	public void addVertex(Location<World> loc) {
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
