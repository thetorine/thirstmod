package com.thetorine.thirstmod.core.player;

import java.lang.reflect.*;
import java.util.Random;

import com.thetorine.thirstmod.core.content.temperature.Temperature;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.network.NetworkHandler;
import com.thetorine.thirstmod.core.network.PacketUpdateClient;
import com.thetorine.thirstmod.core.utils.Config;
import com.thetorine.thirstmod.core.utils.Constants;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.IChatComponent;

public class ThirstLogic {
	public EntityPlayer player;
	public int thirstLevel;
	public float thirstSaturation;
	public float thirstExhaustion;
	public int movementSpeed;
	public int timer; 
	
	private Config config = ThirstMod.config;
	private DamageSource thirst = new DamageThirst("thirst");
	public PoisonLogic poisonLogic = new PoisonLogic();
	
	public Temperature temperature;
	
	public ThirstLogic(EntityPlayer player) {
		this.thirstLevel = Constants.MAX_LEVEL;
		this.thirstSaturation = Constants.MAX_SATURATION;
		this.player = player;
		this.temperature = new Temperature(player);
		
		readData();
	}
	
	public void onTick() {
		int difSet = ThirstMod.config.PEACEFUL_ON ? 1 : getDifficulty(player);
		if (thirstExhaustion > 5f) {
			thirstExhaustion = 0f;
			if (thirstSaturation > 0f) {
				thirstSaturation = Math.max(thirstSaturation - 1f, 0);
			} else if (difSet > 0) {
				thirstLevel = Math.max(thirstLevel - 1, 0);
			}
		}
		
		if (thirstLevel <= 0) {
			timer++;
			if (timer > 200) {
				if ((player.getHealth() > 10) || ((player.getHealth() > 1) && (difSet >= 2))) {
					player.attackEntityFrom(thirst, 1);
					player.addPotionEffect(new PotionEffect(Potion.confusion.id, 15 * 20, 1));
					timer = 0;
				}
			}
		}
		
		if (thirstLevel <= 6) {
			player.setSprinting(false);
		}
		
		this.computeExhaustion(player);
		this.poisonLogic.onTick(player);
		this.writeData();
		
		if(Constants.ECLIPSE_ENVIRONMENT) {
			temperature.onTick();
		}
		
		NetworkHandler.networkWrapper.sendTo(new PacketUpdateClient(thirstLevel, thirstSaturation, poisonLogic.isPoisoned(), temperature.airTemperture), (EntityPlayerMP) player);
	}
	
	public void readData() {
		if (player != null) {
			NBTTagCompound oldnbt = player.getEntityData();
			NBTTagCompound nbt = oldnbt.getCompoundTag("ThirstMod");
			if (nbt.hasKey("level")) {
				thirstLevel = nbt.getInteger("level");
				thirstExhaustion = nbt.getFloat("exhaustion");
				thirstSaturation = nbt.getFloat("saturation");
				timer = nbt.getInteger("timer");

				poisonLogic.setPoisonedTo(nbt.getBoolean("poisoned"));
				poisonLogic.setPoisonTime(nbt.getInteger("poisonTime"));
			}
		}
	}

	public void writeData() {
		if (player != null) {
			NBTTagCompound oldNBT = player.getEntityData();
			NBTTagCompound nbt = oldNBT.getCompoundTag("ThirstMod");
			if (!oldNBT.hasKey("ThirstMod")) {
				oldNBT.setTag("ThirstMod", nbt);
			}
			nbt.setInteger("level", thirstLevel);
			nbt.setFloat("exhaustion", thirstExhaustion);
			nbt.setFloat("saturation", thirstSaturation);
			nbt.setInteger("timer", timer);

			nbt.setBoolean("poisoned", poisonLogic.isPoisoned());
			nbt.setInteger("poisonTime", poisonLogic.poisonTimeRemain());
		} 
	}
	
	public void computeExhaustion(EntityPlayer player) {
		int movement = player.isRiding() ? 0 : movementSpeed;
		float exhaustAmplifier = isNight(player) ? config.NIGHT_RATE : 1;
		float multiplier = getCurrentBiome(player) == "Desert" ? config.DESERT_RATE : 1;
		if (player.isInsideOfMaterial(Material.water)) {
			if (movement > 0) {
				addExhaustion(config.IN_WATER_RATE * movement * 0.003F * exhaustAmplifier);
			}
		} else if (player.isInWater()) {
			if (movement > 0) {
				addExhaustion(config.IN_WATER_RATE * movement * 0.003F * exhaustAmplifier);
			}
		} else if (player.onGround) {
			if (movement > 0) {
				if (player.isSprinting()) {
					addExhaustion(config.RUNNING_RATE * movement * 0.018F * multiplier * exhaustAmplifier);
				} else {
					addExhaustion(config.WALKING_RATE * movement * 0.018F * multiplier * exhaustAmplifier);
				}
			}
		} else if (!player.onGround && !player.isRiding()) {
			if (player.isSprinting()) {
				addExhaustion((config.JUMP_RATE * 2) * multiplier * exhaustAmplifier);
			} else {
				addExhaustion(config.JUMP_RATE * multiplier * exhaustAmplifier);
			}
		}
	}

	public boolean isNight(EntityPlayer player) {
		return player.worldObj.getWorldInfo().getWorldTime() > 14000;
	}
	
	public static String getCurrentBiome(EntityPlayer entityplayer) {
		return entityplayer.worldObj.getWorldChunkManager().getBiomeGenAt((int) entityplayer.posX, (int) entityplayer.posZ).biomeName;
	}
	
	public void addStats(int par1, float par2) {
		thirstLevel = Math.min(par1 + thirstLevel, 20);
		thirstSaturation = Math.min(thirstSaturation + (par1 * par2 * 2.0F), thirstLevel);
	}
	
	public void addExhaustion(float par1) {
		thirstExhaustion = Math.min(thirstExhaustion + par1, 40F);
	}
	
	@Override
	public String toString() {
		return String.format("%s, Level = %d, Saturation = %f, Exhaustion = %f", player.getDisplayName(), thirstLevel, thirstSaturation, thirstExhaustion);
	}
	
	public static int getDifficulty(EntityPlayer player) {
		switch (player.worldObj.difficultySetting) {
			case EASY:
				return 1;
			case NORMAL:
				return 2;
			case HARD:
				return 3;
			case PEACEFUL:
				return 0;
		}
		return 0;
	}
	
	public static class DamageThirst extends DamageSource {
		public DamageThirst(String par1Str) {
			super(par1Str);
		}

		@Override
		public IChatComponent func_151519_b(EntityLivingBase player) {
			String s1 = ((EntityPlayer) player).getDisplayName() + "'s body is now made up of 0% water!";
			return new ChatComponentText(s1);
		}
	}
}