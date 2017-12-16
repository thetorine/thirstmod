package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;

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

    public void onDrinkItem(EntityPlayer player, ItemStack stack) {
        if (!player.world.isRemote && player != null) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            Drink drink = getDrinkFromMetadata(stack.getMetadata());
            stats.addStats(drink.thirstReplenish, drink.saturationReplenish);
            stats.attemptToPoison(drink.poisonChance);
            player.addStat(StatList.getObjectUseStats(this));
        }
    }
}
