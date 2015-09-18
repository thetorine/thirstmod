package com.thetorine.thirstmod.core.content.blocks;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RCRecipes {
	public static final Map<String, ItemStack> recipeList = new HashMap<String, ItemStack>();
	public static final Map<String, Integer> fillTime = new HashMap<String, Integer>();;

	public static void addRecipe(Item item, int time, ItemStack itemstack) {
		recipeList.put(item.getUnlocalizedName(), itemstack);
		fillTime.put(item.getUnlocalizedName(), Integer.valueOf(time));
	}

	public static ItemStack getInputResult(String s) {
		return recipeList.get(s);
	}

	public static int getFillTimeFor(String s) {
		if (fillTime.get(s) != null) {
			return fillTime.get(s);
		} else {
			return 200;
		}
	}
}
