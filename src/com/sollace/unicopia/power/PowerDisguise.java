package com.sollace.unicopia.power;

import com.blazeloader.api.particles.ApiParticles;
import com.blazeloader.api.particles.ParticleData;
import com.sollace.unicopia.PlayerExtension;
import com.sollace.unicopia.Race;
import com.sollace.unicopia.disguise.Disguise;
import com.sollace.unicopia.server.PlayerSpeciesRegister;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class PowerDisguise extends Power<Power.EmptyData> {

	public PowerDisguise(String name, int key) {
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
		return playerSpecies == Race.CHANGELING;
	}

	public EmptyData tryActivate(EntityPlayer player, World w) {
		Entity looked = getLookedAtEntity(player, 10);
		if (!(looked instanceof EntityLivingBase)) {
			looked = null;
		}
		if (!PlayerExtension.get(player).getDisguise().match(looked)) {
			return new EmptyData();
		}
		return null;
	}

	public EmptyData fromBytes(ByteBuf buf) {
		return new EmptyData();
	}
	
	public void apply(EntityPlayer player, EmptyData data) {
//		PlayerExtension.get(player).getDisguise().setPlayer(new PlayerIdent("Notch"));
		Entity looked = getLookedAtEntity(player, 10);
		if (!(looked instanceof EntityLivingBase)) {
			looked = null;
		}
		if (looked instanceof EntityPlayer) {
			if (PlayerSpeciesRegister.getPlayerSpecies((EntityPlayer)looked) == Race.CHANGELING) {
				Disguise disguise = PlayerExtension.get((EntityPlayer)looked).getDisguise();
				if (disguise.isActive()) {
					if (!disguise.isPlayer()) {
						looked = disguise.getEntity();
					} else {
						PlayerExtension.get(player).getDisguise().setPlayer(disguise.getPlayer());
					}
				}
			}
		}
		PlayerExtension.get(player).setDisguise((EntityLivingBase)looked);
		player.worldObj.playSoundEffect(player.posX, player.posY, player.posZ, "mob.zombie.remedy", 0.5f, 0.5f);
	}

	public void preApply(EntityPlayer player) {}

	public void postApply(EntityPlayer player) {
		if (player.worldObj.rand.nextInt(200) < 120) {
			ParticleData particle = ParticleData.get(EnumParticleTypes.CRIT_MAGIC, true);
			for (int i = 0; i < 5; i++) {
				particle.setPos(
						player.posX + player.worldObj.rand.nextFloat()*2 - 1,
						(player.posY) + player.worldObj.rand.nextFloat()*2 - 1,
						player.posZ + player.worldObj.rand.nextFloat()*2 - 1);
				particle.setVel(0, 0.25, 0);
				ApiParticles.spawnParticle(particle, player.worldObj);
			}
			particle = ParticleData.get(EnumParticleTypes.FLAME, true);
			for (int i = 0; i < 5; i++) {
				particle.setPos(
						player.posX + player.worldObj.rand.nextFloat()*2 - 1,
						(player.posY) + player.worldObj.rand.nextFloat()*2 - 1,
						player.posZ + player.worldObj.rand.nextFloat()*2 - 1);
				particle.setVel(0, 0.25, 0);
				ApiParticles.spawnParticle(particle, player.worldObj);
			}
		}
	}
}