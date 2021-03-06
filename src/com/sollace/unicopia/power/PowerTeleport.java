package com.sollace.unicopia.power;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockWall;
import net.minecraft.block.material.MaterialLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;

import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.Unicopia;
import com.sollace.util.Util;
import com.sollace.util.VecHelper;

public class PowerTeleport extends Power<Power.LocationData> {
	public PowerTeleport(String name, int key) {
		super(name, key);
	}
	
	public int getWarmupTime(PlayerExtension player) {
		return 20;
	}
	
	public int getCooldownTime(PlayerExtension player) {
		return 50;
	}
	
	public boolean canActivate(World w, EntityPlayer player) {
		return true; 
	}
	
	public boolean canUse(Race playerSpecies) {
		return playerSpecies.canCast();
	}

	public LocationData tryActivate(EntityPlayer player, World w) {
		return SelectCoordinates(player, w);
	}
	
	public LocationData fromBytes(ByteBuf buf) {
		LocationData result = new LocationData();
		result.fromBytes(buf);
		return result;
	}

	public void apply(EntityPlayer player, LocationData data) {
		ApplyTeleport(player, (LocationData)data);
	}
	
	private void ApplyTeleport(EntityPlayer player, LocationData data) {
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "random.pop", 1.0F, 1.0F);
		double distance = player.getDistance(data.x, data.y, data.z) / 10;
		player.mountEntity(null);
		player.setPositionAndUpdate(data.x + (player.posX - Math.floor(player.posX)), data.y, data.z + (player.posZ - Math.floor(player.posZ)));
		TakeFromPlayer(player, distance);
		player.fallDistance /= distance;
		player.worldObj.playSoundEffect(data.x, data.y, data.z, "random.pop", 1.0F, 1.0F);
	}
	
	public void preApply(EntityPlayer player) {
		postApply(player);
	}
	
	public void postApply(EntityPlayer player) {
		spawnParticles(Unicopia.Particles.unicorn.getData(), player, 1);
	}
	
	private LocationData SelectCoordinates(EntityPlayer player, World w) {
		MovingObjectPosition objectMouseOver = VecHelper.getObjectMouseOverExcept(player, 100, 1f, Util.NOT_CLOUDS);
		if (objectMouseOver != null && objectMouseOver.typeOfHit != MovingObjectType.MISS) {
			int x,y,z;
			
			if (objectMouseOver.typeOfHit == MovingObjectType.ENTITY) {
				x = (int)objectMouseOver.entityHit.posX;
				y = (int)objectMouseOver.entityHit.posY;
				z = (int)objectMouseOver.entityHit.posZ;
			} else {
				x = objectMouseOver.getBlockPos().getX();
				y = objectMouseOver.getBlockPos().getY();
				z = objectMouseOver.getBlockPos().getZ();
			}
			
			boolean airAbove = enterable(w, x, y + 1, z) && enterable(w, x, y + 2, z);
			if (exception(w, x, y, z)) {
				if (player.isSneaking()) {
					switch (objectMouseOver.sideHit) {
						case UP: y --;
							break;
						case DOWN: y ++;
							break;
						case SOUTH: z --;
							break;
						case NORTH: z ++;
							break;
						case EAST: x --;
							break;
						case WEST: x ++;
							break;
					}
				} else {
					switch (objectMouseOver.sideHit) {
						case UP: y ++;
							break;
						case DOWN: y --;
							break;
						case SOUTH: z ++;
							break;
						case NORTH: z --;
							break;
						case EAST: x ++;
							break;
						case WEST: x --;
							break;
					}
				}
			}
			
			if (enterable(w, x, y - 1, z)) {
				y--;
				if (enterable(w, x, y - 1, z)) {
					if (airAbove) {
						x = objectMouseOver.getBlockPos().getX();
						z = objectMouseOver.getBlockPos().getZ();
						y += 2;
					} else {
						return null;
					}
				}
			}
			
			if (!enterable(w, x, y, z) && exception(w, x, y, z)) return null;
			if (!enterable(w, x, y + 1, z) && exception(w, x, y + 1, z)) return null;
			
			return new LocationData(x, y, z);
		}
		
		return null;
	}
	
	private boolean enterable(World w, int x, int y, int z) {
		BlockPos pos = new BlockPos(x,y,z);
		IBlockState state = w.getBlockState(pos);
		if (state != null) {
			Block block = state.getBlock();
			if (w.isAirBlock(pos)) return true;
			return block.isReplaceable(w, pos) || BlockLeaves.class.isAssignableFrom(block.getClass());
		}
		return false;
	}
	
	private boolean exception(World w, int x, int y, int z) {
		BlockPos pos = new BlockPos(x,y,z);
		boolean result = World.doesBlockHaveSolidTopSurface(w, pos);
		IBlockState state = w.getBlockState(pos);
		if (state != null) {
			Block block = state.getBlock();
			Class c = block.getClass();
			return result || MaterialLiquid.class.isAssignableFrom(block.getMaterial().getClass()) || BlockWall.class.isAssignableFrom(c) || BlockFence.class.isAssignableFrom(c) || BlockLeaves.class.isAssignableFrom(c);
		}
		return result;
	}
}
