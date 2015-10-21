package com.selfequalsthis.grubsplugin.service.regions;

import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.Serializable;
import java.util.UUID;

public class Region implements Serializable {
	private static final long serialVersionUID = 7741887736891122465L;

	private UUID worldId;
	private String name;
	private Polygon polygon;
	private Rectangle bounds;
	private boolean complete = false;
	// TODO min and max Y to constrict region height - default to full world height

	public Region(UUID worldId, String regionName) {
		this.worldId = worldId;
		this.name = regionName;
		this.polygon = new Polygon();
	}

	public String getName() {
		return this.name;
	}

	public UUID getWorldUID() {
		return this.worldId;
	}

	public boolean containsLocation(UUID worldId, int x, int y, boolean useBoundingBox) {
		// TODO will have to include check to make sure location Y val is within height of region
		if (this.bounds == null) {
			this.bounds = this.polygon.getBounds();
		}

		// region not done, pretend like it's not here
		if (!this.complete) {
			return false;
		}

		// region from a different world, so we're not inside it
		if (!this.worldId.equals(worldId)) {
			return false;
		}

		if (useBoundingBox) {
			return this.bounds.contains(x, y);
		}
		else {
			return this.polygon.contains(x, y);
		}
	}

	public void addVertex(int x, int y) {
		if (!this.complete) {
			this.polygon.addPoint(x, y);
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
