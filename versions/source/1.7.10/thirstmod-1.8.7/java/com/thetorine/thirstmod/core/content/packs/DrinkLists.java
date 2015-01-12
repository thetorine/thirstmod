package com.thetorine.thirstmod.core.content.packs;

import java.util.*;
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

	public static void addDrink(ItemStack item, int replenish) {
		new DrinkLists(item, replenish, 0, false, false, 0f);
	}

	public static void addDrink(ItemStack item, int replenish, float saturation) {
		new DrinkLists(item, replenish, saturation, true, false, 0f);
	}

	public static void addDrink(ItemStack item, int replenish, float saturation, boolean poison, float poisonChance) {
		new DrinkLists(item, replenish, saturation, true, poison, poisonChance);
	}
}
