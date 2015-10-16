package com.selfequalsthis.grubsplugin.service;

import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public interface RegionService {

	public String getRegion(Location<World> location, boolean useBoundingBox);

}