package com.selfequalsthis.grubsplugin.modules.regions;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Location;

import com.selfequalsthis.grubsplugin.modules.AbstractGrubsModule;
import com.selfequalsthis.grubsplugin.service.RegionService;

public class RegionsServiceProvider implements RegionService {
	protected final Logger logger = Logger.getLogger("Minecraft");

	private AbstractGrubsModule moduleRef;

	private HashMap<UUID,HashMap<String,Region>> regionMap = new HashMap<UUID,HashMap<String,Region>>();

	public RegionsServiceProvider(AbstractGrubsModule module) {
		this.moduleRef = module;
	}

	public void init() {
		File dataFile = this.moduleRef.getDataFile();
		if (dataFile != null) {
			this.moduleRef.log("Loading regions.");
			this.loadRegions();
		}
	}

	public void shutdown() {
		this.moduleRef.log("Saving regions.");
		this.saveRegions();
	}

	@Override
	public String getRegion(Location loc) {
		String retVal = null;

		HashMap<String,Region> worldRegions = this.regionMap.get(loc.getWorld().getUID());
		if (worldRegions != null) {
			for (String name : worldRegions.keySet()) {
				Region curReg = worldRegions.get(name);
				logger.info(curReg.getName() + ": " + curReg.polygon.npoints);
				for (int i = 0; i < curReg.polygon.npoints; ++i) {
					logger.info("" + curReg.polygon.xpoints[i] +","+curReg.polygon.ypoints[i]);
				}
				logger.info("loc: " + loc.getBlockX() + "," + loc.getBlockZ());
				if (curReg.containsLocation(loc)) {
					logger.info("conatains loc");
					retVal = name;
					break;
				}
				logger.info("doesn't conatian loc");
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

	public boolean addVertex(String regionName, Location loc) {
		Region reg = this.getRegion(loc.getWorld().getUID(), regionName);
		if (reg == null) {
			return false;
		}

		if (reg.isComplete()) {
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

		reg.complete();
		this.saveRegions();
		return true;
	}

	public String[] listRegions(UUID worldId) {
		String[] retVal = null;
		HashMap<String,Region> worldRegions = this.regionMap.get(worldId);
		if (worldRegions != null) {
			retVal = worldRegions.keySet().toArray(new String[1]);
		}

		return retVal;
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
		File dataFile = this.moduleRef.getDataFile();
		if (dataFile == null) {
			this.moduleRef.log("Error with data file. Nothing can be loaded!");
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
					loadedRegion.polygon.invalidate();
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
			this.moduleRef.log("Error reading Regions file!");
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
		File dataFile = this.moduleRef.getDataFile();
		if (dataFile == null) {
			this.moduleRef.log("Error with data file. Nothing will be saved!");
			return;
		}

		this.moduleRef.log("Writing Regions save file.");
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
			this.moduleRef.log("Error writing Regions file!");
			ex.printStackTrace();
		}
	}

}
