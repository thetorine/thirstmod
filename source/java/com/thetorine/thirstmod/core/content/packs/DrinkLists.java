package com.thetorine.thirstmod.core.content.packs;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class DrinkLists {
	public static List<Drink> LOADED_DRINKS = new ArrayList<Drink>();
	public static List<Drink> EXTERNAL_DRINKS = new ArrayList<Drink>();

	public static void addDrink(ItemStack item, int replenish, float saturation) {
		Drink d = new Drink(item, replenish, saturation, false, 0f);
		LOADED_DRINKS.add(d);
	}

	public static void addDrink(ItemStack item, int replenish, float saturation, boolean poison, float poisonChance) {
		Drink d = new Drink(item, replenish, saturation, poison, poisonChance);
		LOADED_DRINKS.add(d);
		EXTERNAL_DRINKS.add(d);
	}
	
	public static class Drink {
		public ItemStack item;
		public int replenish;
		public float saturation;
		public boolean poison;
		public float poisonChance;
		public int storeRecipe;
		public int brewTime;
		
		public Drink(ItemStack item, int rep, float sat, boolean poisonable, float chance) {
			this.item = item;
			this.replenish = rep;
			this.saturation = sat;
			this.poison = poisonable;
			this.poisonChance = chance;
			this.storeRecipe = replenish + (int)sat;
			this.brewTime = replenish*30;
		}
	}
}
