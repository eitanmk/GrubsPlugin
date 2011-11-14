package com.selfequalsthis.grubsplugin.modules.wirelessredsone;

import java.io.Serializable;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class ChannelNode implements Serializable {

	private static final long serialVersionUID = -7889393871253577443L;
	
	protected static final Logger log = Logger.getLogger("Minecraft");
	
	private String world;
	private int x;
	private int y;
	private int z;
	private boolean isWallSign = false;
	private int direction = 0;
	private boolean isPowered = false;
	private boolean isInverted = false;
	
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
	
	public boolean isAtLocation(Location loc) {	
		//log.info("event loc: " + loc.toString());
		//log.info("node loc: " + this.world + ":" + this.x + ":" + this.y + ":" + this.z);
		return (
			this.world.equalsIgnoreCase(loc.getWorld().getName())
			&& this.x == loc.getBlockX()
			&& this.y == loc.getBlockY()
			&& this.z == loc.getBlockZ()
		);
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
			Location loc = new Location(world, this.x, this.y, this.z);
			Block blockAtLoc = loc.getBlock();
			
			if (blockAtLoc.getType() == Material.SIGN_POST) {
				blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x5, true);
			}
			else if (blockAtLoc.getType() == Material.WALL_SIGN) {
				// facing east, replace with torch with data 0x4 to face east
				if (blockAtLoc.getData() == 0x2) {
					blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x4, true); 
				}
				// facing west, replace with torch with data 0x3 to face west
				else if (blockAtLoc.getData() == 0x3) {
					blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x3, true); 
				}
				// facing north, replace with torch with data 0x2 to face north
				else if (blockAtLoc.getData() == 0x4) {
					blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x2, true); 
				}
				// facing south, replace with torch with data 0x1 to face south
				else if (blockAtLoc.getData() == 0x5) {
					blockAtLoc.setTypeIdAndData(Material.REDSTONE_TORCH_ON.getId(), (byte)0x1, true); 
				}
			}
		}
	}
	
	public void toSign(World world, String channelName) {
		if (this.world.equalsIgnoreCase(world.getName())) {
			Location loc = new Location(world, this.x, this.y, this.z);
			Block blockAtLoc = loc.getBlock();
			
			// no idea why this is needed, but it is if you want two wall signs 
			//  attached to two faces of the same block
			blockAtLoc.setType(Material.AIR);
			
			if (isWallSign) {
				blockAtLoc.setTypeIdAndData(Material.WALL_SIGN.getId(), (byte)direction, true);
			}
			else {
				blockAtLoc.setTypeIdAndData(Material.SIGN_POST.getId(), (byte)direction, true);
			}
			
			if (blockAtLoc.getState() instanceof Sign) {
				Sign newSignRef = (Sign)blockAtLoc.getState();
				if (isInverted) {
					newSignRef.setLine(0, GrubsWirelessRedstone.RECEIVER_INVERTED_TEXT);
				}
				else {
					newSignRef.setLine(0, GrubsWirelessRedstone.RECEIVER_TEXT);
				}
				newSignRef.setLine(1, channelName);
				newSignRef.update(true);
			}
		}
	}
	
	public boolean physicsWillCauseDestruction(Block block) {
		boolean willBeDropped = false;
		World world = block.getWorld();
		
		// pulled most of this logic from the MC jar
		//  have to determine when this will be dropped naturally
		if (block.getState() instanceof Sign) {
			//log.info("is a sign");
			if (!this.isWallSign) {
				//log.info("sign post");
				//log.info("" + (world.getBlockAt(this.x, this.y - 1, this.z).getType() == Material.AIR));
				if (world.getBlockAt(this.x, this.y - 1, this.z).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
			else {
				//log.info("wall sign");
				//log.info("type id: " + block.getData());
				// net.minecraft.server.BlockSign:doPhysics()
				if (block.getData() == 2) {
					if (world.getBlockAt(this.x, this.y, this.z + 1).getType() == Material.AIR) {
						willBeDropped = true;
					}
				}
				else if (block.getData() == 3) {
					if (world.getBlockAt(this.x, this.y, this.z - 1).getType() == Material.AIR) {
						willBeDropped = true;
					}
				}
				else if (block.getData() == 4) {
					if (world.getBlockAt(this.x + 1, this.y, this.z).getType() == Material.AIR) {
						willBeDropped = true;
					}
				}
				else if (block.getData() == 5) {
					if (world.getBlockAt(this.x - 1, this.y, this.z).getType() == Material.AIR) {
						willBeDropped = true;
					}
				}
			}
		}
		else if (block.getType() == Material.REDSTONE_TORCH_ON) {
			//log.info("redstone torch");
			//log.info("type id: " + block.getData());
			// net.minecraft.server.BlockTorch:doPhysics()
			if (block.getData() == 1) {
				if (world.getBlockAt(this.x - 1, this.y, this.z).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
			else if (block.getData() == 2) {
				if (world.getBlockAt(this.x + 1, this.y, this.z).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
			else if (block.getData() == 3) {
				if (world.getBlockAt(this.x, this.y, this.z - 1).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
			else if (block.getData() == 4) {
				if (world.getBlockAt(this.x, this.y, this.z + 1).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
			else if (block.getData() == 5) {
				if (world.getBlockAt(this.x, this.y - 1, this.z).getType() == Material.AIR) {
					willBeDropped = true;
				}
			}
		}
		
		return willBeDropped;
	}
}
