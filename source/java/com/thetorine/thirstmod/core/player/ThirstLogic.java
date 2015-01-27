package com.thetorine.thirstmod.core.player;

import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.network.NetworkHandler;
import com.thetorine.thirstmod.core.network.PacketUpdateClient;
import com.thetorine.thirstmod.core.utils.Config;
import com.thetorine.thirstmod.core.utils.Constants;

import net.minecraft.block.material.Material;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.*;
import net.minecraft.world.biome.BiomeGenBase;

public class ThirstLogic {
	public EntityPlayer player;
	public int thirstLevel;
	public float thirstSaturation;
	public float thirstExhaustion;
	public int movementSpeed; 
	public int timer; 
	public boolean natRegen;
	
	private Config config = ThirstMod.config;
	public PoisonLogic poisonLogic = new PoisonLogic();
	
	public ThirstLogic(EntityPlayer player) {
		this.thirstLevel = Constants.MAX_LEVEL;
		this.thirstSaturation = Constants.MAX_SATURATION;
		this.player = player;
		this.natRegen = player.worldObj.getGameRules().getGameRuleBooleanValue("naturalRegeneration");
		
		readData();
	}
	
	public void onTick( ) {
		int difSet = ThirstMod.config.PEACEFUL_ON ? 1 : player.worldObj.difficultySetting.getDifficultyId();
		if (thirstExhaustion > 5f) {
			thirstExhaustion = 0f;
			if (thirstSaturation > 0f) {
				thirstSaturation = Math.max(thirstSaturation - 1f, 0);
			} else if (difSet > 0) {
				thirstLevel = Math.max(thirstLevel - 1, 0);
			}
		}
		
		if (thirstLevel <= 6) {
			player.setSprinting(false);
			if (thirstLevel <= 0) {
				timer++;
				if (timer > 200) {
					if ((player.getHealth() > 10) || (player.getHealth() > (ThirstMod.config.DEATH_FROM_THIRST ? 0 : (difSet == 3 ? 0 : 1)) && difSet >= 2)) {
						//alternative to attackEntityFrom(DamageSource, Float) which was not working
						player.setHealth(player.getHealth()-1f); 
						player.addPotionEffect(new PotionEffect(Potion.confusion.id, 15 * 20, 1));
						player.worldObj.getGameRules().setOrCreateGameRule("naturalRegeneration", "false");
						timer = 0;
					}
				}
			} else {
				player.worldObj.getGameRules().setOrCreateGameRule("naturalRegeneration", Boolean.toString(natRegen));
			}
		} else {
			player.worldObj.getGameRules().setOrCreateGameRule("naturalRegeneration", Boolean.toString(natRegen));
		}
		
		this.computeExhaustion();
		this.poisonLogic.onTick(player);
		this.writeData();
		
		NetworkHandler.networkWrapper.sendTo(new PacketUpdateClient(this), (EntityPlayerMP) player);
	}
	
	/**
	 * Load the data associated with this EntityPlayer.
	 */
	public void readData() {
		if (player != null) {
			NBTTagCompound oldnbt = player.getEntityData();
			NBTTagCompound nbt = oldnbt.getCompoundTag("ThirstMod");
			if (nbt.hasKey("level")) {
				thirstLevel = nbt.getInteger("level");
				thirstExhaustion = nbt.getFloat("exhaustion");
				thirstSaturation = nbt.getFloat("saturation");
				timer = nbt.getInteger("timer");
				poisonLogic.changeValues(nbt.getBoolean("poisoned"), nbt.getInteger("poisonTime"));
			}
		}
	}

	/**
	 * Writes the data associated with this EntityPlayer to the disk.
	 */ 
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

			nbt.setBoolean("poisoned", poisonLogic.isPlayerPoisoned());
			nbt.setInteger("poisonTime", poisonLogic.getPoisonTimeRemaining());
		} 
	}
	
	/**
	 * Calculates the exhaustion to be added to {@link #thirstExhaustion}
	 * @param player
	 */
	public void computeExhaustion() {
		int movement = player.isRiding() ? 0 : movementSpeed;
		float exhaustAmplifier = isNight() ? config.NIGHT_RATE : 1;
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

	/**
	 * Checks whether it is currently night-time in the world.
	 * @return Returns true for night.
	 */
	public boolean isNight() {
		long worldTime = player.worldObj.getWorldTime() % 24000;
		return worldTime >= 13000;
	}
	
	/**
	 * Get the current biome of the player as a string. See {@link BiomeGenBase#biomeName}
	 * @param player The EntityPlayer with which to retrieve the current biome.
	 * @return The string representation of the biome. 
	 */
	public static String getCurrentBiome(EntityPlayer player) {
		return player.worldObj.getWorldChunkManager().getBiomeGenAt((int) player.posX, (int) player.posZ).biomeName;
	}
	
	public void addStats(int thirst, float sat) {
		thirstLevel = Math.min(thirst + thirstLevel, 20);
		thirstSaturation = Math.min(thirstSaturation + (thirst * sat * 2.0F), thirstLevel);
	}
	
	public void addExhaustion(float exh) {
		thirstExhaustion = Math.min(thirstExhaustion + exh, 40F);
	}
	
	public void setStats(int level, float sat) {
		this.thirstLevel = level;
		this.thirstSaturation = sat;
	}
	
	/**
	 * Checks whether the current difficulty setting of the world allows the thirst to drain.
	 * @return Whether the thirst bar can drain.
	 */
	public boolean isThirstAllowedByDifficulty() {
		if(ThirstMod.config.PEACEFUL_ON) return true;
		else return player.worldObj.difficultySetting.getDifficultyId() > 0;
	}
	
	@Override
	public String toString() {
		return String.format("%s, Level = %d, Saturation = %f, Exhaustion = %f", player.getDisplayName(), thirstLevel, thirstSaturation, thirstExhaustion);
	}
}