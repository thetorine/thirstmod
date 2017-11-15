package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.common.logic.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public class TileEntityDrinksBrewer extends TileEntity implements ITickable, ISidedInventory {

    private NonNullList<ItemStack> itemStacks = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);

    private int fuelRemaining = 0;
    private int fillTime = 0;
    private int manufactureTime = 0;
    private int maxFuel = 0;

    @Override
    public void update() {
        ItemStack containerStack = getStackInSlot(0);
        ItemStack ingredientStack = getStackInSlot(1);
        ItemStack fuelStack = getStackInSlot(2);
        ItemStack outputStack = getStackInSlot(3);

        Recipes.Output output = Recipes.getDrinksBrewerRecipe(ingredientStack.getItem());
        boolean flag = containerStack.getItem() == Items.GLASS_BOTTLE && output != null;

        if (flag) {
            manufactureTime = output.manufactureTime;
            if (outputStack.isEmpty() || outputStack.isItemEqual(output.outputItem)) {
                if (isBurningFuel()) fillTime++;
                if (fillTime >= output.manufactureTime && outputStack.getCount() < getInventoryStackLimit()) {
                    decrStackSize(0, 1);
                    decrStackSize(1, 1);
                    if (outputStack.isEmpty()) {
                        itemStacks.set(3, output.outputItem.copy());
                    } else {
                        itemStacks.get(3).setCount(outputStack.getCount() + 1);
                    }
                    fillTime = 0;
                }
            }
        } else {
            fillTime = 0;
        }
    }

    public boolean isBurningFuel() {
        if (fuelRemaining == 0) {
            ItemStack fuelStack = itemStacks.get(2);
            int burnTime = TileEntityFurnace.getItemBurnTime(fuelStack);
            if (burnTime > 0) {
                decrStackSize(2, 1);
                fuelRemaining = burnTime;
                maxFuel = burnTime;
            }
        } else {
            fuelRemaining--;
        }
        return fuelRemaining > 0;
    }

    public int getFillTimeScaled(int scale) {
        return (int) (((float)fillTime / (float)manufactureTime) * scale);
    }

    public int getFuelRemainingScaled(int scale) {
        return (int) (((float)fuelRemaining / (float)maxFuel) * scale);
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index < 3;
    }

    @Override
    public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
        return false;
    }

    @Override
    public int getSizeInventory() {
        return itemStacks.size();
    }

    @Override
    public boolean isEmpty() {
        for (ItemStack stack : itemStacks) {
            if (!stack.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public ItemStack getStackInSlot(int index) {
        return itemStacks.get(index);
    }

    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(itemStacks, index, count);
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(itemStacks, index);
    }

    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        ItemStack existingStack = itemStacks.get(index);
        boolean flag = !existingStack.isEmpty() && existingStack.isItemEqual(stack) && ItemStack.areItemStackTagsEqual(stack, existingStack);
        itemStacks.set(index, stack);
        if (stack.getCount() > getInventoryStackLimit()) {
            stack.setCount(getInventoryStackLimit());
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUsableByPlayer(EntityPlayer player) {
        if (player.world.getTileEntity(pos) != this) {
            return false;
        } else {
            return player.getDistanceSq((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D) <= 64.0D;
        }
    }

    @Override
    public void openInventory(EntityPlayer player) {

    }

    @Override
    public void closeInventory(EntityPlayer player) {

    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index < 3;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return fuelRemaining;
            case 1: return fillTime;
            case 2: return manufactureTime;
            case 3: return maxFuel;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: fuelRemaining = value; break;
            case 1: fillTime = value; break;
            case 2: manufactureTime = value; break;
            case 3: maxFuel = value; break;
        }
    }

    @Override
    public int getFieldCount() {
        return 4;
    }

    @Override
    public void clear() {
        itemStacks.clear();
    }

    @Override
    public String getName() {
        return "tile.drinks_brewer";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
