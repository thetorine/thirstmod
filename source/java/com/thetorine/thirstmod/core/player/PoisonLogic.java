package com.thetorine.thirstmod.core.player;

import java.util.Random;

import com.thetorine.thirstmod.core.main.ThirstMod;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;

//TODO rewrite. this is some 2012 shit.
public class PoisonLogic {
	private int poisonTimer;
	private int healthPoison;
	private boolean poisonPlayer;
	private boolean preloadedPoison;
	private boolean isPoisoned;
	private boolean setPoison;
	private float poisonID;
	public boolean poisonStopper;
	private boolean addedPotion;
	
	public void onTick(EntityPlayer player) {
		if (shouldPoison()) {
			if (!preloadedPoison) {
				Random random = new Random();
				poisonID = random.nextFloat();
				preloadedPoison = true;
			}
			if (poisonStopper) {
				poisonTimer = 0;
				isPoisoned = false;
				poisonPlayer = false;
			}
			poisonPlayer(poisonID, player);
		}
		if (!setPoison) {
			setPoison = true;
		}
	}
	
	public void startPoison() {
		if (ThirstMod.config.POISON_ON) {
			preloadedPoison = false;
			poisonPlayer = true;
			poisonStopper = false;
			addedPotion = false;
		}
	}
	
	private void poisonPlayer(float f, EntityPlayer player) {
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
			PlayerContainer.getPlayer(player.getDisplayName()).addExhaustion(0.061f);
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

			if (!addedPotion) {
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

			if (!addedPotion) {
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
	
	public void startPoison(Random random, float f) {
		if (random.nextFloat() < f) {
			startPoison();
		}
	}
	
	public boolean shouldPoison() {
		if (poisonPlayer == true) { return true; }
		return false;
	}
	
	public boolean isPoisoned() {
		return isPoisoned;
	}
	
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
