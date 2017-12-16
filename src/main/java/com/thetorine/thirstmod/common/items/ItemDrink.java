package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

public class ItemDrink extends ItemContainer {

    public ItemDrink(String unlocalisedName) {
        super(unlocalisedName);
    }

    public Drink getDrinkFromMetadata(int metadata) {
        return Drink.getDrinkByIndex(metadata);
    }

    public int getMetadataForDrink(Drink drink) {
        return Drink.ALL_DRINKS.indexOf(drink);
    }

    @Override
    public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items) {
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            items.add(new ItemStack(this, 1, i));
        }
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return "Bottle of " + getDrinkFromMetadata(stack.getMetadata()).drinkName;
    }

    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving) {
        EntityPlayer player = entityLiving instanceof EntityPlayer ? (EntityPlayer)entityLiving : null;
        this.onDrinkItem(player, stack);
        stack.shrink(1);
        if (stack.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        }
        player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
        return stack;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack itemstack = player.getHeldItem(hand);
        ThirstStats stats = world.isRemote ? ThirstMod.getClientProxy().clientStats : ThirstMod.getProxy().getStatsByUUID(player.getUniqueID());
        if (stats.canDrink() || player.capabilities.isCreativeMode || getDrinkFromMetadata(itemstack.getMetadata()).alwaysDrinkable) {
            player.setActiveHand(hand);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
        } else {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, itemstack);
        }
    }

    public static class BottleColorHandler implements IItemColor {
        @Override
        public int getColorFromItemstack(ItemStack stack, int tintIndex) {
            return tintIndex > 0 ? ThirstMod.getProxy().DRINKS.getDrinkFromMetadata(stack.getMetadata()).drinkColor : 0xffffff;
        }
    }

}
