package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionUtils;
import net.minecraft.stats.StatList;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;

public class ItemCanteen extends Item {

    private final int maxCapacity = 3;

    public ItemCanteen(String name) {
        this.setUnlocalizedName(name);
        this.setRegistryName(Constants.MOD_ID, name);
        this.setCreativeTab(CreativeTabs.FOOD);
        this.setHasSubtypes(true);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        items.add(new ItemStack(this, 1, 0)); // empty canteen
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            for (int j = 0; j < Constants.CANTEEN_CAPACITY; j++) {
                items.add(new ItemStack(this, 1, i*Constants.CANTEEN_CAPACITY + j + 1));
            }
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack.getMetadata() == 0) {
            return "Empty Canteen";
        }
        int level = getCanteenLevel(stack.getMetadata()) + 1;
        return getDrink(stack.getMetadata()).drinkName + " Canteen " + level  + "/" + Constants.CANTEEN_CAPACITY;
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0d - (getCanteenLevel(stack.getMetadata()) + 1) / (double) Constants.CANTEEN_CAPACITY;
    }

    public Drink getDrink(int metadata) {
        return Drink.ALL_DRINKS.get((metadata - 1) / Constants.CANTEEN_CAPACITY);
    }

    public int getCanteenLevel(int metadata) {
        return (metadata - 1) % 3;
    }

    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        Drink drink = getDrink(stack.getMetadata());

        if (!world.isRemote && player != null) {
            ThirstStats stats = ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            stats.addStats(drink.thirstReplenish, drink.saturationReplenish);
            player.addStat(StatList.getObjectUseStats(this));
        }

        if (getCanteenLevel(stack.getMetadata()) == 0) {
            stack.setItemDamage(0);
            return stack;
        }
        stack.setItemDamage(stack.getMetadata() - 1);
        return stack;
    }

    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        RayTraceResult result = this.rayTrace(world, player, true);
        if (result == null || itemstack.getMetadata() > 0) {
            ThirstStats stats = world.isRemote ? ThirstMod.getClientProxy().clientStats : ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
            if ((stats.canDrink() || player.capabilities.isCreativeMode) && itemstack.getMetadata() > 0) {
                player.setActiveHand(hand);
                return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
            }
        } else if (result.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos blockpos = result.getBlockPos();
            if (world.getBlockState(blockpos).getMaterial() == Material.WATER) {
                world.playSound(player, player.posX, player.posY, player.posZ, SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.NEUTRAL, 1.0F, 1.0F);
                return new ActionResult(EnumActionResult.SUCCESS, new ItemStack(ThirstMod.getProxy().CANTEEN, 1, getIndexOfDrink(Drink.getDrinkByName("Fresh Water"))));
            }
        }
        return new ActionResult(EnumActionResult.PASS, itemstack);
    }

    public static int getIndexOfDrink(Drink d) {
        int drinkIndex = Drink.ALL_DRINKS.indexOf(d);
        return drinkIndex * Constants.CANTEEN_CAPACITY + Constants.CANTEEN_CAPACITY;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.DRINK;
    }

    public static class CanteenColorHandler implements IItemColor {
        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            if (tintIndex > 0 && stack.getMetadata() > 0) {
                return ThirstMod.getProxy().CANTEEN.getDrink(stack.getMetadata()).drinkColor;
            }
            return 0xffffff;
        }
    }
}
