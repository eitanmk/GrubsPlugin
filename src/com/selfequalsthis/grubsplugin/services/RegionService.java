package com.selfequalsthis.grubsplugin.services;

import org.bukkit.Location;

public interface RegionService {

	public String getRegion(Location location, boolean useBoundingBox);

}
