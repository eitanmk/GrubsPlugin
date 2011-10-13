package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

public class ChannelNode implements Serializable {

	private static final long serialVersionUID = -7889393871253577443L;
	
	private String world;
	private int x;
	private int y;
	private int z;
	private boolean isWallSign = false;
	private int direction = 0;
	
	public ChannelNode(Block block) {
		Location blockLoc = block.getLocation();
		this.world = blockLoc.getWorld().getName();
		this.x = blockLoc.getBlockX();
		this.y = blockLoc.getBlockY();
		this.z = blockLoc.getBlockZ();
		this.direction = block.getData();
		this.isWallSign = (block.getType() == Material.WALL_SIGN);
	}

	public String getWorld() {
		return world;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getZ() {
		return z;
	}

	public boolean isWallSign() {
		return isWallSign;
	}

	public int getDirection() {
		return direction;
	}
	
	public boolean isAtLocation(Location loc) {
		return (
			this.world == loc.getWorld().getName()
			&& this.x == loc.getBlockX()
			&& this.y == loc.getBlockY()
			&& this.z == loc.getBlockZ()
		);
	}
}
