package com.thetorine.thirstmod.common.player;

import java.lang.reflect.Field;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.network.PacketUpdateClient;
import com.thetorine.thirstmod.common.utils.DamageThirst;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;

public class ThirstStats {
	public EntityPlayer player;

	public int level;
	public float saturation;
	public float exhaustion;
	public int timer;
	public int movementSpeed;

	private DamageSource thirst = new DamageThirst("thirst");

	private PoisonController poisonCon = new PoisonController();
	private ExhaustionController exhauster = new ExhaustionController(this);

	public ThirstStats(EntityPlayer player) {
		this.player = player;
		level = 20;
		saturation = 4f;
		exhaustion = 0f;
		timer = 0;
		readData(player);
	}

	/**
	 * Holds the Thirst Logic. Controls everything related to that Thirst Bar.
	 * 
	 * @param player
	 *            EntityPlayer instance.
	 */
	public void onTick(EntityPlayer player) {
		int difSet = ThirstMod.CONFIG.PEACEFUL_ON ? 1 : getDiffilculty();
		if (exhaustion > 4f) {
			exhaustion = 0f;
			if (saturation > 0f) {
				saturation = saturation - 1f;
			} else if (difSet > 0) {
				level = Math.max(level - 1, 0);
				if ((level < 10) && (level != 0)) {
					saturation += 1;
				}
			}
		}

		if (level <= 0) {
			timer++;
			if (timer > 200) {
				if ((player.getHealth() > 10) || (difSet >= 3) || ((player.getHealth() > 1) && (difSet >= 2))) {
					player.attackEntityFrom(thirst, 1);
					player.addPotionEffect(new PotionEffect(Potion.confusion.id, 15 * 20, 1));
					timer = 0;
				}
			}
		}

		if ((level < 7) || !ThirstMod.CONFIG.HEALTH_REGEN_OFF) {
			if (difSet > 0) {
				FoodStats foodStats = player.getFoodStats();
				Field field = FoodStats.class.getDeclaredFields()[3];
				field.setAccessible(true);
				try {
					field.set(foodStats, 0);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		if (level <= 6) {
			player.setSprinting(false);
		}

		poisonCon.onTick(player);
		exhauster.exhaustPlayer(player);

		writeData(player);
		ThirstMod.network.sendTo(new PacketUpdateClient(level, saturation, poisonCon.isPoisoned()), (EntityPlayerMP) player);
	}

	public void readData(EntityPlayer player) {
		if (player != null) {
			NBTTagCompound oldnbt = player.getEntityData();
			NBTTagCompound nbt = oldnbt.getCompoundTag("ThirstMod");
			if (nbt.hasKey("level")) {
				level = nbt.getInteger("level");
				exhaustion = nbt.getFloat("exhaustion");
				saturation = nbt.getFloat("saturation");
				timer = nbt.getInteger("timer");

				poisonCon.setPoisonedTo(nbt.getBoolean("poisoned"));
				poisonCon.setPoisonTime(nbt.getInteger("poisonTime"));
			} else {
				setDefaults();
			}
			ThirstUtils.print("Successfully loaded stats: " + this);
		} else {
			ThirstUtils.print("SERIOUS ERROR IN readData/ThirstStats. Player could not be found!!");
		}

	}

	public void writeData(EntityPlayer player) {
		if (player != null) {
			NBTTagCompound oldNBT = player.getEntityData();
			NBTTagCompound nbt = oldNBT.getCompoundTag("ThirstMod");
			if (!oldNBT.hasKey("ThirstMod")) {
				oldNBT.setTag("ThirstMod", nbt);
			}
			nbt.setInteger("level", level);
			nbt.setFloat("exhaustion", exhaustion);
			nbt.setFloat("saturation", saturation);
			nbt.setInteger("timer", timer);

			nbt.setBoolean("poisoned", poisonCon.isPoisoned());
			nbt.setInteger("poisonTime", poisonCon.poisonTimeRemain());
		} else {
			ThirstUtils.print("SERIOUS ERROR IN writeData/ThirstStats. A NULL player detected!! Crash imminent!");
		}
	}

	public void setDefaults() {
		level = 20;
		exhaustion = 0f;
		saturation = 5f;
		timer = 0;
		getPoison().setPoisonedTo(false);
		getPoison().setPoisonTime(0);
	}

	public PoisonController getPoison() {
		return poisonCon;
	}

	/**
	 * Adds exhaustion
	 * 
	 * @param par1
	 *            Amount to be added.
	 */
	public void addExhaustion(float par1) {
		exhaustion = Math.min(exhaustion + par1, 40F);
	}

	/**
	 * Adds stats to the level and saturation.
	 * 
	 * @param par1
	 *            Amount to add to level.
	 * @param par2
	 *            Amount to saturation.
	 */
	public void addStats(int par1, float par2) {
		level = Math.min(par1 + level, 20);
		saturation = Math.min(saturation + (par1 * par2 * 2.0F), level);
	}

	@Override
	public String toString() {
		return String.format("%s, Level = %d, Saturation = %f", player.getDisplayName(), level, saturation);
	}

	public int getDiffilculty() {
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
}