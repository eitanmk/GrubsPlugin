package com.selfequalsthis.grubsplugin.module.regionannouncer;

import java.util.HashMap;
import java.util.UUID;

import org.spongepowered.api.entity.living.player.Player;

public class GrubsPlayerRegionTracker {

	private static GrubsPlayerRegionTracker instance = new GrubsPlayerRegionTracker();
	private HashMap<UUID,HashMap<Player,String>> playerMap = new HashMap<UUID,HashMap<Player,String>>();

	private GrubsPlayerRegionTracker() { }

	public static GrubsPlayerRegionTracker getInstance() {
		return instance;
	}

	private HashMap<Player,String> getPlayerMapForWorld(UUID id) {
		return this.playerMap.get(id);
	}

	public void updatePlayerRegion(Player player, String region) {
		UUID worldId = player.getWorld().getUniqueId();
		HashMap<Player,String> worldPlayerMap = this.getPlayerMapForWorld(worldId);

		if (worldPlayerMap == null) {
			worldPlayerMap = new HashMap<Player,String>();
			worldPlayerMap.put(player, region);
			this.playerMap.put(worldId, worldPlayerMap);
		}
		else {
			worldPlayerMap.put(player, region);
		}
	}

	public void removePlayer(Player player) {
		UUID worldId = player.getWorld().getUniqueId();
		HashMap<Player,String> worldPlayerMap = this.getPlayerMapForWorld(worldId);

		if (worldPlayerMap != null) {
			worldPlayerMap.remove(player);
		}
	}

	public String getPlayerRegion(Player player) {
		UUID worldId = player.getWorld().getUniqueId();
		HashMap<Player,String> worldPlayerMap = this.getPlayerMapForWorld(worldId);

		if (worldPlayerMap == null) {
			return null;
		}

		return worldPlayerMap.get(player);
	}
}
