package com.selfequalsthis.grubsplugin.service.regions;

import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationOptions;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

public class RegionsServiceSavefileHelper {

	private static final int CURRENT_VERSION = 1;

	public static HashMap<UUID,HashMap<String,Region>> readDataFile(File dataFile) {
		if (dataFile == null || !dataFile.exists()) {
			System.out.println("Regions dataFile not found!");
			return null;
		}

		ConfigurationLoader<CommentedConfigurationNode> config = HoconConfigurationLoader.builder().setFile(dataFile).build();
		CommentedConfigurationNode rootNode = null;

		try {
			rootNode = config.load();
		} catch (IOException e) {
			rootNode = null;
		}

		if (rootNode == null) {
			return null;
		}

		HashMap<UUID,HashMap<String,Region>> retVal = null;
		int version = rootNode.getNode("version").getInt();
		switch (version) {
			case 1:
				retVal = readDataFileV1(rootNode.getNode("data"));
				break;
			default:
				System.out.println("No regions readDataFile handler found for version [" + version + "]");
				break;
		}

		return retVal;
	}

	public static void writeDataFile(File dataFile, HashMap<UUID,HashMap<String,Region>> data) {
		ConfigurationLoader<CommentedConfigurationNode> config = HoconConfigurationLoader.builder().setFile(dataFile).build();
		CommentedConfigurationNode rootNode = config.createEmptyNode(ConfigurationOptions.defaults());
		rootNode.getNode("version").setValue(CURRENT_VERSION).setComment("DO NOT MODIFY VERSION!");
		writeDataFileV1(rootNode.getNode("data"), data);
		try {
			config.save(rootNode);
		} catch (IOException e) {
			System.out.println("Error saving regions data file!");
		}
	}

	/*
	 ********************************
	 * Version 1
	 ********************************
	 */
	@SuppressWarnings("serial")
	public static HashMap<UUID,HashMap<String,Region>> readDataFileV1(CommentedConfigurationNode dataRootNode) {
		HashMap<UUID,HashMap<String,Region>> regionData = new HashMap<UUID,HashMap<String,Region>>();
		for (CommentedConfigurationNode worldNode : dataRootNode.getChildrenMap().values()) {
			UUID worldId = UUID.fromString((String) worldNode.getKey());
			HashMap<String,Region> worldRegions = new HashMap<String,Region>();
			for (CommentedConfigurationNode regionNode : worldNode.getChildrenMap().values()) {
				String regionName = (String) regionNode.getKey();
				Region r = new Region(worldId, regionName);
				CommentedConfigurationNode verticesNode = regionNode.getNode("vertices");
				try {
					for (ArrayList<Integer> vertices : verticesNode.getList(new TypeToken<ArrayList<Integer>> () {})) {
						r.addVertex(vertices.get(0), vertices.get(1));
					}
				} catch (ObjectMappingException e) {}
				r.complete();
				worldRegions.put(regionName, r);
			}
			regionData.put(worldId, worldRegions);
		}

		return regionData;
	}

	public static void writeDataFileV1(CommentedConfigurationNode dataRootNode, HashMap<UUID,HashMap<String,Region>> data) {
		for (UUID worldId : data.keySet()) {
			CommentedConfigurationNode worldNode = dataRootNode.getNode(worldId.toString());
			HashMap<String,Region> regions = data.get(worldId);
			for (Region region : regions.values()) {
				if (!region.isComplete()) {
					// drop incomplete regions
					continue;
				}
				CommentedConfigurationNode regionNode = worldNode.getNode(region.getName());
				ArrayList<ArrayList<Integer>> vertices = new ArrayList<ArrayList<Integer>>();
				Polygon p = region.getPolygon();
				for (int i = 0, len = region.getNumVerticies(); i < len; ++i) {
					ArrayList<Integer> point = new ArrayList<Integer>();
					point.add(p.xpoints[i]);
					point.add(p.ypoints[i]);
					vertices.add(point);
				}
				regionNode.getNode("vertices").setValue(vertices);
			}
		}
	}
}
