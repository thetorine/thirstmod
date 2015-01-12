package com.thetorine.thirstmod.common.utils;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.item.ItemStack;

public class DrinkLists {
	public static List<DrinkLists> drinkLists = new ArrayList<DrinkLists>();
	public static List<DrinkLists> extraList = new ArrayList<DrinkLists>();

	public ItemStack item;
	public int replenish;
	public float saturation;
	public boolean poison;
	public float poisonChance;
	public boolean extra;

	public int storeRecipe;

	public DrinkLists(ItemStack item, int replenish, float saturation, boolean extra, boolean poison, float chance) {
		this.item = item;
		this.replenish = replenish;
		this.saturation = saturation;
		this.poison = poison;
		this.poisonChance = chance;

		this.storeRecipe = replenish + 5;

		if (extra) {
			extraList.add(this);
			drinkLists.add(this);
		} else {
			drinkLists.add(this);
		}
	}

	/**
	 * Used by the actual mod, NOT FOR MODDERS ONLY FOR tarun1998!
	 * 
	 * @param item
	 *            the item of the drink
	 * @param replenish
	 *            amount to replenish the thirst bar.
	 */
	public static void addDrink(ItemStack item, int replenish) {
		new DrinkLists(item, replenish, 0, false, false, 0f);
	}

	/**
	 * Add an item to a secret list which allows the thirst bar to be
	 * replenished when drunk.
	 * 
	 * @param item
	 *            the item to add.
	 * @param replenish
	 *            amount to replenish the thirst bar. max 20
	 * @param saturation
	 *            amount of saturation to be added; max 10
	 */
	public static void addDrink(ItemStack item, int replenish, float saturation) {
		new DrinkLists(item, replenish, saturation, true, false, 0f);
	}

	/**
	 * Add an item to a secret list which allows the thirst bar to be
	 * replenished when drunk.
	 * 
	 * @param item
	 *            the item to add.
	 * @param replenish
	 *            amount to replenish the thirst bar. max 20
	 * @param saturation
	 *            amount of saturation to be added; max 10
	 * @param poison
	 *            if this drink can poison the player.
	 * @param poisonChance
	 *            how much chance does the drink have to poison. 0.1 = 10% and
	 *            so on. Max 0.9
	 */
	public static void addDrink(ItemStack item, int replenish, float saturation, boolean poison, float poisonChance) {
		new DrinkLists(item, replenish, saturation, true, poison, poisonChance);
	}
}
