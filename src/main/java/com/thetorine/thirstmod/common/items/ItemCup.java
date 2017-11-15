package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemCup extends Item {

    public ItemCup(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Constants.MOD_ID, name);
        this.setCreativeTab(CreativeTabs.FOOD);
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0));
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            items.add(new ItemStack(this, 1, i + 1));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() == 0) {
            return "Cup";
        }
        return "Cup of " + Drink.ALL_DRINKS.get(stack.getMetadata() - 1).drinkName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;

        if (!world.isRemote && player != null) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            Drink drink = Drink.ALL_DRINKS.get(stack.getMetadata() - 1);
            stats.addStats(drink.thirstReplenish, drink.saturationReplenish);
            player.addStat(StatList.getObjectUseStats(this));
        }

        stack.shrink(1);
        if (stack.isEmpty()) {
            return new ItemStack(ThirstMod.getProxy().CUP, 1, 0);
        }
        player.inventory.addItemStackToInventory(new ItemStack(ThirstMod.getProxy().CUP, 1, 0));

        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        RayTraceResult result = this.rayTrace(world, player, true);
        if (result == null || itemstack.getMetadata() > 0) {
            ThirstStats stats = world.isRemote ? ThirstMod.getClientProxy().clientStats : ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            if ((stats.canDrink() || player.capabilities.isCreativeMode)) {
                player.setActiveHand(hand);
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
            }
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = result.getBlockPos();
            if (world.getBlockState(blockpos).getMaterial() == Material.WATER) {
                world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                return new ActionResult(EnumActionResult.SUCCESS, new ItemStack(ThirstMod.getProxy().CUP, 1, Drink.getDrinkIndexByName("Fresh Water") + 1));
            }
        }
        return new ActionResult(EnumActionResult.PASS, itemstack);
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    public static class CupColorHandler implements IItemColor {
        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (stack.getMetadata() == 0) {
                return 0xffffff;
            }
            return tintIndex > 0 ? Drink.ALL_DRINKS.get(stack.getMetadata() - 1).drinkColor : 0xffffff;
        }
    }
}
