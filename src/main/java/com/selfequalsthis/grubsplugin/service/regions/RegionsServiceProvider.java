package com.selfequalsthis.grubsplugin.service.regions;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.selfequalsthis.grubsplugin.service.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionsServiceProvider implements RegionService {

	private AbstractGrubsService serviceRef;

	private HashMap<UUID,RegionList> regionMap = new HashMap<UUID,RegionList>();

	public RegionsServiceProvider(AbstractGrubsService module) {
		this.serviceRef = module;
	}

	public void init() {
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile != null) {
			this.serviceRef.log("Loading regions...");
			this.loadRegions();
		}
	}

	public void shutdown() {
		this.serviceRef.log("Saving regions...");
		this.saveRegions();
	}

	@Override
	public String getRegion(UUID worldId, int x, int y, boolean useBoundingBox) {
		String retVal = null;

		Optional<RegionList> optWorldRegions = this.getWorldRegions(worldId);
		if (optWorldRegions.isPresent()) {
			Optional<Region> optRegion = optWorldRegions.get().containsLocation(worldId, x, y, useBoundingBox);
			if (optRegion.isPresent()) {
				retVal = optRegion.get().getName();
			}
		}

		return retVal;
	}

	public boolean createRegion(UUID worldId, String name) {
		Region newRegion = new Region(worldId, name);
		Optional<RegionList> optWorldRegions = this.getWorldRegions(worldId);
		if (!optWorldRegions.isPresent()) {
			RegionList worldRegionList = new RegionList();
			worldRegionList.addRegion(newRegion);
			this.regionMap.put(worldId, worldRegionList);
			return true;
		}

		RegionList worldRegions = optWorldRegions.get();
		if (worldRegions.containsRegion(name)) {
			newRegion = null;
			return false;
		}

		worldRegions.addRegion(newRegion);
		return true;
	}

	public boolean addVertex(UUID worldId, String regionName, int x, int y) {
		Optional<Region> optionalRegion = this.getRegionObject(worldId, regionName);
		if (!optionalRegion.isPresent()) {
			return false;
		}

		Region reg = optionalRegion.get();

		if (reg.isComplete()) {
			return false;
		}

		// check to make sure this vertex isn't contained by another region
		String overlap = this.getRegion(worldId, x, y, true);
		if (overlap != null) {
			return false;
		}

		reg.addVertex(x, y);
		return true;
	}

	public boolean completeRegion(UUID worldId, String regionName) {
		Optional<Region> optionalRegion = this.getRegionObject(worldId, regionName);
		if (!optionalRegion.isPresent()) {
			return false;
		}

		Region reg = optionalRegion.get();	

		if (reg.isComplete()) {
			return false;
		}

		if (reg.getNumVerticies() < 3) {
			return false;
		}
		// TODO: can two regions intersect? maybe if the complete timing is off. need to check

		reg.complete();
		this.saveRegions();
		return true;
	}

	public ArrayList<Text> listRegions(UUID worldId) {
		ArrayList<Text> retVal = new ArrayList<Text>();
		Optional<RegionList> optWorldRegions = this.getWorldRegions(worldId);
		if (optWorldRegions.isPresent()) {
			ArrayList<String> regionNames = optWorldRegions.get().getRegionNames();
			for (String name : regionNames) {
				retVal.add(Texts.of(name));
			}
		}

		return retVal;
	}

	public boolean deleteRegion(UUID worldId, String regionName) {
		Optional<RegionList> optWorldRegions = this.getWorldRegions(worldId);
		if (optWorldRegions.isPresent()) {
			RegionList worldRegions = optWorldRegions.get();
			worldRegions.removeRegion(regionName);

			// remove world from mapping if it has no regions
			if (worldRegions.size() == 0) {
				this.regionMap.remove(worldId);
			}

			this.saveRegions();
			return true;
		}

		return false;
	}

	private Optional<RegionList> getWorldRegions(UUID worldId) {
		RegionList worldRegions = this.regionMap.get(worldId);
		if (worldRegions != null) {
			return Optional.of(worldRegions);
		}

		return Optional.empty();
	}

	private Optional<Region> getRegionObject(UUID worldId, String name) {
		Optional<RegionList> optWorldRegions = this.getWorldRegions(worldId);
		if (optWorldRegions.isPresent()) {
			return optWorldRegions.get().getRegion(name);
		}

		return Optional.empty();
	}

	private void loadRegions() {
		/*
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile == null) {
			this.serviceRef.log("Error with data file. Nothing can be loaded!");
			return;
		}
		this.regionMap = RegionsServiceSavefileHelper.readDataFile(dataFile);
		this.serviceRef.log("Loaded " + this.getNumAllRegions() + " regions.");
		*/
	}

	private void saveRegions() {
		/*
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile == null) {
			this.serviceRef.log("Error with data file. Nothing will be saved!");
			return;
		}

		RegionsServiceSavefileHelper.writeDataFile(dataFile, this.regionMap);
		this.serviceRef.log("Wrote " + this.getNumAllRegions() + " regions to save file.");
		*/
	}

	/*
	private int getNumAllRegions() {
		int count = 0;
		for (RegionList regionList : this.regionMap.values()) {
			count += regionList.size();
		}
		return count;
	}
	*/

}
