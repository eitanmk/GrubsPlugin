package com.selfequalsthis.grubsplugin.service.regions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.selfequalsthis.grubsplugin.service.AbstractGrubsService;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionsServiceProvider implements RegionService {

	private AbstractGrubsService serviceRef;

	private HashMap<UUID,HashMap<String,Region>> regionMap = new HashMap<UUID,HashMap<String,Region>>();

	public RegionsServiceProvider(AbstractGrubsService module) {
		this.serviceRef = module;
	}

	public void init() {
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile != null) {
			this.serviceRef.log("Loading regions.");
			this.loadRegions();
		}
	}

	public void shutdown() {
		this.serviceRef.log("Saving regions.");
		this.saveRegions();
	}

	@Override
	public String getRegion(Location<World> location, boolean useBoundingBox) {
		String retVal = null;

		HashMap<String,Region> worldRegions = this.regionMap.get(location.getExtent().getUniqueId());
		if (worldRegions != null) {
			for (String name : worldRegions.keySet()) {
				Region curReg = worldRegions.get(name);
				if (curReg.containsLocation(location, useBoundingBox)) {
					retVal = name;
					break;
				}
			}
		}

		return retVal;
	}

	public boolean createRegion(String name, UUID worldId) {
		Region newRegion = new Region(name, worldId);
		HashMap<String,Region> worldRegions = this.regionMap.get(worldId);
		if (worldRegions == null) {
			HashMap<String,Region> worldRegionMap = new HashMap<String,Region>();
			worldRegionMap.put(name, newRegion);
			this.regionMap.put(worldId, worldRegionMap);
			return true;
		}

		if (worldRegions.containsKey(name)) {
			newRegion = null;
			return false;
		}

		worldRegions.put(name, newRegion);
		return true;
	}

	public boolean addVertex(String regionName, Location<World> loc) {
		Region reg = this.getRegion(loc.getExtent().getUniqueId(), regionName);
		if (reg == null) {
			return false;
		}

		if (reg.isComplete()) {
			return false;
		}

		// check to make sure this vertex isn't contained by another region
		String overlap = this.getRegion(loc, true);
		if (overlap != null) {
			return false;
		}

		reg.addVertex(loc);
		return true;
	}

	public boolean completeRegion(String regionName, UUID worldId) {
		Region reg = this.getRegion(worldId, regionName);
		if (reg == null) {
			return false;
		}

		if (reg.isComplete()) {
			return false;
		}

		if (reg.getNumVerticies() < 3) {
			return false;
		}

		reg.complete();
		this.saveRegions();
		return true;
	}

	public ArrayList<Text> listRegions(UUID worldId) {
		ArrayList<Text> retVal = new ArrayList<Text>();
		HashMap<String,Region> worldRegions = this.regionMap.get(worldId);
		if (worldRegions != null) {
			for (String key : worldRegions.keySet()) {
				retVal.add(Texts.of(key));
			}
		}

		return retVal;
	}

	public boolean deleteRegion(String regionName, UUID worldId) {
		HashMap<String,Region> worldRegions = this.regionMap.get(worldId);
		if (worldRegions.containsKey(regionName)) {
			worldRegions.remove(regionName);
			this.saveRegions();
			return true;
		}

		return false;
	}



	private Region getRegion(UUID worldId, String name) {
		Region retVal = null;

		HashMap<String,Region> worldRegions = this.regionMap.get(worldId);
		if (worldRegions != null) {
			retVal = worldRegions.get(name);
		}

		return retVal;
	}

	private void loadRegions() {
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile == null) {
			this.serviceRef.log("Error with data file. Nothing can be loaded!");
			return;
		}

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {
			fis = new FileInputStream(dataFile);
			in = new ObjectInputStream(fis);

			Object obj = in.readObject();
			while (obj != null) {
				if (obj instanceof Region) {
					Region loadedRegion = (Region)obj;
					UUID loadedRegionWorldUID = loadedRegion.getWorldUID();
					String loadedRegionName = loadedRegion.getName();

					HashMap<String,Region> worldRegions = this.regionMap.get(loadedRegionWorldUID);
					if (worldRegions == null) {
						HashMap<String,Region> worldRegionMap = new HashMap<String,Region>();
						worldRegionMap.put(loadedRegionName, loadedRegion);
						this.regionMap.put(loadedRegionWorldUID, worldRegionMap);
					}
					else {
						worldRegions.put(loadedRegionName, loadedRegion);
					}
				}
				obj = in.readObject();
			}
		}
		catch (EOFException eof) { }
		catch (Exception ex) {
			this.serviceRef.log("Error reading Regions file!");
			ex.printStackTrace();
		}
		finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void saveRegions() {
		File dataFile = this.serviceRef.getDataFile();
		if (dataFile == null) {
			this.serviceRef.log("Error with data file. Nothing will be saved!");
			return;
		}

		this.serviceRef.log("Writing Regions save file.");
		try {
			FileOutputStream fos = new FileOutputStream(dataFile);
			ObjectOutputStream out = new ObjectOutputStream(fos);

			for (HashMap<String,Region> regions : this.regionMap.values()) {
				for (Region region : regions.values()) {
					out.writeObject(region);
				}
			}

			out.close();
		}
		catch (Exception ex) {
			this.serviceRef.log("Error writing Regions file!");
			ex.printStackTrace();
		}
	}

}