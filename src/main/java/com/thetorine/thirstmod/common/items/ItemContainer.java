package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.Constants;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemContainer extends Item {

    public ItemContainer(String unlocalisedName) {
        this.setUnlocalizedName(unlocalisedName);
        this.setRegistryName(Constants.MOD_ID, unlocalisedName);
        this.setCreativeTab(CreativeTabs.FOOD);
        this.setHasSubtypes(true);
    }

    public Drink getDrinkFromMetadata(int metadata) {
        return null;
    }

    public int getMetadataForDrink(Drink drink) {
        return 0;
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        return getDrinkFromMetadata(stack.getMetadata()).shiny;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }
}
