package com.selfequalsthis.grubsplugin.service;

import java.util.UUID;

public interface RegionService {

	public String getRegion(UUID worldId, int x, int y, boolean useBoundingBox);

}