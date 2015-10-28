package com.selfequalsthis.grubsplugin.service.regions;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

public class RegionList {

	private ArrayList<Region> regionList = new ArrayList<Region>();

	public void addRegion(Region region) {
		this.regionList.add(region);
	}

	public void removeRegion(String name) {
		Optional<Region> optRegion = this.getRegion(name);
		if (optRegion.isPresent()) {
			this.regionList.remove(optRegion.get());
		}
	}

	public boolean containsRegion(String name) {
		boolean retVal = false;
		for (Region reg : this.regionList) {
			if (reg.getName().equals(name)) {
				retVal = true;
				break;
			}
		}

		return retVal;
	}

	public int size() {
		return this.regionList.size();
	}

	public Optional<Region> getRegion(String name) {
		for (Region region : this.regionList) {
			if (region.getName().equals(name)) {
				return Optional.of(region);
			}
		}

		return Optional.empty();
	}

	public ArrayList<String> getRegionNames() {
		ArrayList<String> retVal = new ArrayList<String>(this.regionList.size());
		for (Region region : this.regionList) {
			retVal.add(region.getName());
		}
		return retVal;
	}

	public Optional<Region> containsLocation(UUID worldId, int x, int y, boolean useBoundingBox) {
		for (Region region : this.regionList) {
			if (region.containsLocation(worldId, x, y, useBoundingBox)) {
				return Optional.of(region);
			}
		}

		return Optional.empty();
	}
}
