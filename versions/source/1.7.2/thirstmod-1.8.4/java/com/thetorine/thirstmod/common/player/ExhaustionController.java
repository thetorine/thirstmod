package com.thetorine.thirstmod.common.player;

import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.utils.ConfigHelper;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;

public class ExhaustionController {

	private ThirstStats stats;
	private ConfigHelper CONFIG = ThirstMod.CONFIG;

	public ExhaustionController(ThirstStats stats) {
		this.stats = stats;
	}

	public void exhaustPlayer(EntityPlayer player) {
		int movement = player.isRiding() ? 0 : stats.movementSpeed;
		float exhaustAmplifier = isNight(player) ? CONFIG.NIGHT_RATE : 1;
		float multiplier = ThirstUtils.getCurrentBiome(player) == "Desert" ? CONFIG.DESERT_RATE : 1;
		if (player.isInsideOfMaterial(Material.water)) {
			if (movement > 0) {
				addExhaustion(CONFIG.IN_WATER_RATE * movement * 0.003F * exhaustAmplifier);
			}
		} else if (player.isInWater()) {
			if (movement > 0) {
				addExhaustion(CONFIG.IN_WATER_RATE * movement * 0.003F * exhaustAmplifier);
			}
		} else if (player.onGround) {
			if (movement > 0) {
				if (player.isSprinting()) {
					addExhaustion(CONFIG.RUNNING_RATE * movement * 0.018F * multiplier * exhaustAmplifier);
				} else {
					addExhaustion(CONFIG.WALKING_RATE * movement * 0.018F * multiplier * exhaustAmplifier);
				}
			}
		} else if (!player.onGround && !player.isRiding()) {
			if (player.isSprinting()) {
				addExhaustion((CONFIG.JUMP_RATE * 2) * multiplier * exhaustAmplifier);
			} else {
				addExhaustion(CONFIG.JUMP_RATE * multiplier * exhaustAmplifier);
			}
		}
	}

	public void addExhaustion(float par1) {
		stats.exhaustion = Math.min(stats.exhaustion + par1, 40F);
	}

	public boolean isNight(EntityPlayer player) {
		return FMLCommonHandler.instance().getEffectiveSide().isClient() ? FMLClientHandler.instance().getClient().theWorld.getWorldInfo().getWorldTime() > 14000 : player.worldObj.getWorldInfo()
				.getWorldTime() > 14000;
	}
}
