
package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemCup extends ItemContainer {

    public ItemCup(String unlocalisedName) {
        super(unlocalisedName);
    }

    public Drink getDrinkFromMetadata(int metadata) {
        return Drink.ALL_DRINKS.get(metadata - 1);
    }

    public int getMetadataForDrink(Drink drink) {
        return Drink.ALL_DRINKS.indexOf(drink) + 1;
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
        return "Cup of " + getDrinkFromMetadata(stack.getMetadata()).drinkName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        this.onDrinkItem(player, stack);
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
            if (itemstack.getMetadata() > 0 && (stats.canDrink() || player.capabilities.isCreativeMode || getDrinkFromMetadata(itemstack.getMetadata()).alwaysDrinkable)) {
                player.setActiveHand(hand);
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
            }
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = result.getBlockPos();
            if (world.getBlockState(blockpos).getMaterial() == Material.WATER) {
                world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                return new ActionResult(EnumActionResult.SUCCESS, new ItemStack(ThirstMod.getProxy().CUP, 1, getMetadataForDrink(Drink.getDrinkByName("Fresh Water"))));
            }
        }
        return new ActionResult(EnumActionResult.PASS, itemstack);
    }

    @Override
    public boolean hasEffect(ItemStack stack) {
        if (stack.getMetadata() == 0) return false;
        return super.hasEffect(stack);
    }

    public static class CupColorHandler implements IItemColor {
        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (stack.getMetadata() == 0) {
                return tintIndex > 0 ? 0x957328 : 0xffffff;
            }
            return tintIndex > 0 ? ThirstMod.getProxy().CUP.getDrinkFromMetadata(stack.getMetadata()).drinkColor : 0xffffff;
        }
    }
}
