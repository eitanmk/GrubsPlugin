package com.selfequalsthis.grubsplugin.module.wirelessredstone;

import java.io.Serializable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.manipulator.mutable.block.DirectionalData;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

public class ChannelNode implements Serializable {

	private static final long serialVersionUID = -7889393871253577443L;

	private String world;
	private int x;
	private int y;
	private int z;
	private BlockState state;
	private boolean isWallSign = false;
	private boolean isPowered = false;
	private boolean isInverted = false;

	public ChannelNode(Location<World> location) {
		this.world = location.getExtent().getName();
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
		this.state = location.getBlock();
		this.isWallSign = (location.getBlock().getType() == BlockTypes.WALL_SIGN);
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

	public void setIsPowered(boolean isPowered) {
		this.isPowered = isPowered;
	}

	public boolean isPowered() {
		return isPowered;
	}

	public void setIsInverted(boolean isInverted) {
		this.isInverted = isInverted;
	}

	public boolean isInverted() {
		return isInverted;
	}

	public boolean isAtLocation(Location<World> loc) {
		return (this.world.equalsIgnoreCase(loc.getExtent().getName())
				&& this.x == loc.getBlockX()
				&& this.y == loc.getBlockY()
				&& this.z == loc.getBlockZ());
	}

	public void handleChannelStartTransmitting(World world, String channelName) {
		if (isInverted) {
			this.toSign(world, channelName);
		}
		else {
			this.toTorch(world);
		}
	}

	public void handleChannelEndTransmitting(World world, String channelName) {
		if (isInverted) {
			this.toTorch(world);
		}
		else {
			this.toSign(world, channelName);
		}
	}

	
	public void toTorch(World world) {
		if (this.world.equalsIgnoreCase(world.getName())) {
			Location<World> loc = world.getLocation(this.x, this.y, this.z);
			BlockState blockAtLoc = loc.getBlock();
			
			if (blockAtLoc.getType() == BlockTypes.STANDING_SIGN) {
				BlockState newState = BlockTypes.REDSTONE_TORCH.getDefaultState();
				loc.setBlock(newState, true);
			}
			else if (blockAtLoc.getType() == BlockTypes.WALL_SIGN) {
				DirectionalData direction = loc.getOrCreate(DirectionalData.class).get();
				BlockState newState = BlockState.builder()
						.blockType(BlockTypes.REDSTONE_TORCH)
						.add(direction)
						.build();
				loc.setBlock(newState, true);
			}
		}
	}

	public void toSign(World world, String channelName) {
		if (this.world.equalsIgnoreCase(world.getName())) {
			Location<World> loc = world.getLocation(this.x, this.y, this.z);

			BlockState newSign = BlockState.builder().from(state).build();
			loc.setBlock(newSign, false);
		}
	}

	public Location<World> getLocation() {
		return new Location<World>(Sponge.getGame().getServer().getWorld(this.world).get(),
				this.x, this.y, this.z);
	}

	public String toString() {
		return "Location: " + this.x + ":" + this.y + ":" + this.z + (this.isInverted ? ", inverted" : "") + "\n";
	}
}
