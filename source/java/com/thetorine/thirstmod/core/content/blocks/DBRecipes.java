package com.thetorine.thirstmod.core.content.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.item.ItemStack;

public class DBRecipes {
	private static final DBRecipes dbRecipes = new DBRecipes();
	@SuppressWarnings("rawtypes")
	private Map<List, ItemStack> metaBrewingList = new HashMap<List, ItemStack>();

	public static final DBRecipes instance() {
		return dbRecipes;
	}
	
	public void addRecipe(String id, ItemStack itemstack) {
		metaBrewingList.put(Arrays.asList(id, 0), itemstack);
	}
	
	public void addRecipe(String id, int metadata, ItemStack itemstack) {
		metaBrewingList.put(Arrays.asList(id, metadata), itemstack);
	}

	@SuppressWarnings("rawtypes")
	public ItemStack getBrewingResult(ItemStack item) {
		if (item == null) return null;
		for(List l : metaBrewingList.keySet()) {
			String name = (String) l.get(0);
			int metadata = (int) l.get(1);
			if(item.getUnlocalizedName().equals(name) && item.getItemDamage() == metadata) {
				return metaBrewingList.get(l);
			}
		}
		return null;
	}
}
