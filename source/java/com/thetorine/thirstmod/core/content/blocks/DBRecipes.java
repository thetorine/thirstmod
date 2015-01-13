package com.thetorine.thirstmod.core.content.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class DBRecipes {
	private static final DBRecipes dbRecipes = new DBRecipes();
	private Map<String, ItemStack> brewingList;
	@SuppressWarnings("rawtypes")
	private Map<List, ItemStack> metaBrewingList = new HashMap<List, ItemStack>();

	public static final DBRecipes instance() {
		return dbRecipes;
	}

	private DBRecipes() {
		brewingList = new HashMap<String, ItemStack>();
	}

	public void addRecipe(String s, ItemStack itemstack) {
		brewingList.put(s, itemstack);
	}

	public void addRecipe(String s, int metadata, ItemStack itemstack) {
		metaBrewingList.put(Arrays.asList(s, metadata), itemstack);
	}

	public Map<String, ItemStack> getBrewingList() {
		return brewingList;
	}

	public ItemStack getBrewingResult(ItemStack item) {
		if (item == null) { return null; }
		return brewingList.get(item.getUnlocalizedName());
	}
}
