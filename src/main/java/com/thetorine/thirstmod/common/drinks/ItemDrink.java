package com.thetorine.thirstmod.common.drinks;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Generic drink item class.
 */

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.ThirstStats;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemDrink extends Item {
    public ItemDrink(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Constants.MOD_ID, name);
        this.setCreativeTab(CreativeTabs.FOOD);
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

        if (!world.isRemote && player != null) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            Drink drink = Drink.ALL_DRINKS.get(stack.getMetadata());
            stats.addStats(drink.thirstReplenish, drink.saturationReplenish);
            player.addStat(StatList.getObjectUseStats(this));
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            if (stack.isEmpty()) {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }
        }

        return stack;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        ThirstStats stats = world.isRemote ? ThirstMod.getClientProxy().clientStats : ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
        if (stats.canDrink() || player.capabilities.isCreativeMode) {
            player.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    public static class ColorHandler implements IItemColor {
        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            return tintIndex > 0 ? Drink.ALL_DRINKS.get(stack.getMetadata()).drinkColor : 0xffffff;
        }
    }

}
