package com.thetorine.thirstmod.common.utils;

import com.thetorine.thirstmod.common.items.DrinkLoader;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class CreativeTabThirst extends CreativeTabs {

	public CreativeTabThirst(String label) {
		super(label);
	}

	@Override
	public Item getTabIconItem() {
		return DrinkLoader.freshWater;
	}
}
