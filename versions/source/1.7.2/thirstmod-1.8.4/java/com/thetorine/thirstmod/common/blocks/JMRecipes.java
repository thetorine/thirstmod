package com.thetorine.thirstmod.common.blocks;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.ItemStack;

public class JMRecipes {
	private static final JMRecipes solidifyingBase = new JMRecipes();
	private Map<String, ItemStack> solidifyingList;
	private Map<List, ItemStack> metaSmeltingList = new HashMap<List, ItemStack>();

	public static final JMRecipes solidifying() {
		return solidifyingBase;
	}

	private JMRecipes() {
		solidifyingList = new HashMap<String, ItemStack>();
	}

	public void addSolidifying(String s, ItemStack itemstack) {
		solidifyingList.put(s, itemstack);
	}

	public void addSolidifyingg(String s, int metadata, ItemStack itemstack) {
		metaSmeltingList.put(Arrays.asList(s, metadata), itemstack);
	}

	public Map<String, ItemStack> getSolidifyingList() {
		return solidifyingList;
	}

	public ItemStack getSmeltingResult(ItemStack item) {
		if (item == null) { return null; }
		return solidifyingList.get(item.getUnlocalizedName());
	}
}
