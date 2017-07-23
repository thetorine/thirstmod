package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemFilter extends Item {
    private int filterType;

    public ItemFilter(String name, int type) {
        setUnlocalizedName(name);
        setRegistryName(Constants.MOD_ID, name);
        setNoRepair();
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.FOOD);
        filterType = type;
        switch (type) {
            case 0: {
                setMaxDamage(4);
                break;
            }
            case 1: {
                setMaxDamage(6);
            }
        }
    }

    @Override
    public boolean hasContainerItem(ItemStack stack) {
        return filterType <= 1;
    }

    @Override
    public ItemStack getContainerItem(ItemStack itemStack) {
        if (itemStack.getItemDamage() < itemStack.getMaxDamage() - 1) {
            return new ItemStack(this, 1, itemStack.getItemDamage() + 1);
        } else if (filterType == 0) {
            return new ItemStack(ThirstMod.getProxy().DIRTY_FILTER, 1);
        }
        return ItemStack.EMPTY;
    }
}
