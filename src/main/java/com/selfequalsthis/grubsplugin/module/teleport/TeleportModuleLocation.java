package com.selfequalsthis.grubsplugin.module.teleport;

import java.util.Optional;

import org.spongepowered.api.Game;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import com.flowpowered.math.vector.Vector3d;

public class TeleportModuleLocation {

	private TeleportModule moduleRef;
	private Game game;

	private World world;
	private Location<World> location;
	private Vector3d rotation;

	public TeleportModuleLocation(TeleportModule module, Game game) {
		this.moduleRef = module;
		this.game = game;
	}

	public void fromPlayer(Player player) {
		this.world = player.getWorld();
		this.location = player.getLocation();
		this.rotation = player.getRotation();
	}

	public boolean fromPropValue(String propValue) {
		String[] parts = propValue.split(",");

		Optional<World> optWorld = this.game.getServer().getWorld(parts[0]);
		if (!optWorld.isPresent()) {
			this.moduleRef.log("Unable to find location in world: '" + parts[0] + "'");
			return false;
		}

		this.world = optWorld.get();

		this.location = new Location<World>(this.world,
				Double.parseDouble(parts[1]),
				Double.parseDouble(parts[2]),
				Double.parseDouble(parts[3]));

		this.rotation = new Vector3d(Double.parseDouble(parts[4]), Double.parseDouble(parts[5]), 0F);

		return true;
	}

	public String toPropValue() {
		double locParts[] = new double[3];
		double  viewParts[] = new double[2];
		String settingStr = "";

		locParts[0] = this.location.getX();
		locParts[1] = this.location.getY();
		locParts[2] = this.location.getZ();
		viewParts[0] = this.rotation.getX(); // yaw
		viewParts[1] = this.rotation.getY(); // pitch

		settingStr = this.world.getName() +  "," +
				Double.toString(locParts[0]) + "," +
				Double.toString(locParts[1]) + "," +
				Double.toString(locParts[2]) + "," +
				Double.toString(viewParts[0]) + "," +
				Double.toString(viewParts[1]);

		return settingStr;
	}

}
