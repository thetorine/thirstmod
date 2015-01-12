package com.thetorine.thirstmod.common.player;

import java.util.Random;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

public class PoisonController {
	private int poisonTimer;
	private int healthPoison;
	private boolean poisonPlayer = false;
	private boolean preloadedPoison = false;
	private boolean isPoisoned = false;
	private boolean setPoison = false;
	private float poisonID;
	public boolean drankPoisonStoper = false;
	private boolean addedPotion = false;

	/**
	 * PoisonControler ticks. Called from PlayerStatistics.class.
	 */
	public void onTick(EntityPlayer player) {
		if (shouldPoison() == true) {
			if (preloadedPoison == false) {
				Random random = new Random();
				poisonID = random.nextFloat();
				preloadedPoison = true;
			}
			if (drankPoisonStoper == true) {
				poisonTimer = 0;
				isPoisoned = false;
				poisonPlayer = false;
			}
			addPoisonType(poisonID, player);
		}
		if (setPoison == false) {
			setPoison = true;
		}
	}

	/**
	 * Starts the poison.
	 */
	public void startPoison() {
		if (ThirstMod.CONFIG.POISON_ON) {
			preloadedPoison = false;
			poisonPlayer = true;
			drankPoisonStoper = false;
			addedPotion = false;
		}
	}

	private void addPoisonType(float f, EntityPlayer player) {
		if (f < 0.3f) {
			increaseExhaust(player);
		} else if ((f > 0.3f) && (f < 0.75f)) {
			potionEffects(player);
		} else if (f > 0.75f) {
			damageHealth(player);
		}
	}

	public void increaseExhaust(EntityPlayer player) {
		if (shouldPoison()) {
			poisonTimer++;
			PlayerHandler.getPlayer(player.getDisplayName()).addExhaustion(0.061111111111111f);
			isPoisoned = true;
			if (poisonTimer > 360) {
				poisonTimer = 0;
				isPoisoned = false;
				poisonPlayer = false;
			}
		}
	}

	public void potionEffects(EntityPlayer player) {
		if (shouldPoison()) {
			poisonTimer++;
			isPoisoned = true;

			if (addedPotion == false) {
				player.addPotionEffect(new PotionEffect(Potion.moveSlowdown.id, 34 * 20, 1));
				player.addPotionEffect(new PotionEffect(Potion.weakness.id, 34 * 20, 1));
				addedPotion = true;
			}

			if (poisonTimer > 680) {
				poisonTimer = 0;
				isPoisoned = false;
				poisonPlayer = false;
			}
		}
	}

	public void damageHealth(EntityPlayer player) {
		if (shouldPoison()) {
			poisonTimer++;
			healthPoison++;
			isPoisoned = true;

			if (healthPoison > 40) {
				player.attackEntityFrom(DamageSource.starve, 1);
				healthPoison = 0;
			}

			if (addedPotion == false) {
				player.attackEntityFrom(DamageSource.starve, 1);
				addedPotion = true;
			}

			if (poisonTimer > 360) {
				poisonTimer = 0;
				isPoisoned = false;
				poisonPlayer = false;
			}
		}
	}

	/**
	 * Poisons the player depending on chance
	 */
	public void startPoison(Random random, float f) {
		if (random.nextFloat() < f) {
			startPoison();
		}
	}

	/**
	 * Checks if the game should poison.
	 * 
	 * @return if can poison.
	 */
	public boolean shouldPoison() {
		if (poisonPlayer == true) { return true; }
		return false;
	}

	/**
	 * Checks if the player is currently poisoned.
	 * 
	 * @return if the player is poisoned.
	 */
	public boolean isPoisoned() {
		return isPoisoned;
	}

	/**
	 * Checks how much time is remaining until the poison stops.
	 * 
	 * @return remaining time until poison stops.
	 */
	public int poisonTimeRemain() {
		if (poisonTimer > 0) {
			return poisonTimer;
		} else {
			return 0;
		}
	}

	public void setPoisonedTo(boolean what) {
		poisonPlayer = what;
	}

	public void setPoisonTime(int what) {
		poisonTimer = what;
	}
}
