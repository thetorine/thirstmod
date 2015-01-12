package com.thetorine.thirstmod.common.items;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemCCFilter extends Item {

	public ItemCCFilter() {
		super();
		setMaxDamage(4);
		setMaxStackSize(1);
		setCreativeTab(CreativeTabs.tabMisc);
		setNoRepair();
	}
}
