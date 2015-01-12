package com.thetorine.thirstmod.core.content;

import net.minecraft.item.Item;

public class ItemFilter extends Item {
	
	//0 = filter, 1 = dirty filter, 2 = charcoal filter
	public ItemFilter(int filter) {
		this.setNoRepair();
		switch(filter) {
			case 0: {
				this.setMaxStackSize(1);
				this.setMaxDamage(4);
				break;
			}
			case 2: {
				this.setMaxStackSize(1);
				this.setMaxDamage(4);
			}
		}
	}
}
