package com.thetorine.thirstmod.core.content.blocks;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.item.ItemStack;

public class DBRecipes {
	private static final DBRecipes dbRecipes = new DBRecipes();
	private Map<HashMap<String, Integer>, ItemStack> metaBrewingList = new HashMap<HashMap<String, Integer>, ItemStack>();

	public static final DBRecipes instance() {
		return dbRecipes;
	}
	
	public void addRecipe(String id, ItemStack itemstack) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put(id, 0);
		metaBrewingList.put(map, itemstack);
	}
	
	public void addRecipe(String id, int metadata, ItemStack itemstack) {
		HashMap<String, Integer> map = new HashMap<String, Integer>();
		map.put(id, metadata);
		metaBrewingList.put(map, itemstack);
	}

	@SuppressWarnings("unchecked")
	public ItemStack getBrewingResult(ItemStack item) {
		if (item == null) return null;
		for(HashMap<String, Integer> map : metaBrewingList.keySet()) {
			String name = ((Entry<String, Integer>)map.entrySet().toArray()[0]).getKey();
			int metadata = map.get(name);
			if(item.getUnlocalizedName().equals(name) && item.getItemDamage() == metadata) {
				return metaBrewingList.get(map);
			}
		}
		return null;
	}
}