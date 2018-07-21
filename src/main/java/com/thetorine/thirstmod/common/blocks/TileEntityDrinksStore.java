package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.common.logic.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;

public class TileEntityDrinksStore extends TileEntity implements ITickable, ISidedInventory {

    private NonNullList<ItemStack> itemStacks = NonNullList.withSize(2, ItemStack.EMPTY);
    private int drinkId;

    @Override
    public void update() {
        ItemStack stack = getStackInSlot(0);
        if (!stack.isEmpty()) {
            Drink drink = Drink.getDrinkByIndex(drinkId);
            if (stack.getItem().equals(Items.GLASS_BOTTLE)) {
                itemStacks.set(1, new ItemStack(CommonProxy.DRINKS, stack.getCount(), drinkId));
            } else if (stack.getItem().equals(CommonProxy.CUP) && stack.getMetadata() == 0) {
                itemStacks.set(1, new ItemStack(CommonProxy.CUP, stack.getCount(), CommonProxy.CUP.getMetadataForDrink(drink)));
            } else if (stack.getItem().equals(CommonProxy.CANTEEN) && stack.getMetadata() == 0) {
                itemStacks.set(1, new ItemStack(CommonProxy.CANTEEN, stack.getCount(), CommonProxy.CANTEEN.getMetadataForDrink(drink)));
            } else {
                itemStacks.set(1, ItemStack.EMPTY);
            }
        } else {
            itemStacks.set(1, ItemStack.EMPTY);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("drinkId", drinkId);
        ItemStackHelper.saveAllItems(compound, itemStacks);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, itemStacks);
        drinkId = compound.getInteger("drinkId");
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        return index == 0;
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
            if (!stack.isEmpty()) {
                return false;
            }
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
        if (index == 1) {
            return ItemStack.EMPTY;
        }
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
    public void openInventory(EntityPlayer player) {}

    @Override
    public void closeInventory(EntityPlayer player) {}

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        return index == 0;
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return drinkId;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: {
                drinkId = value;
                int size = Drink.ALL_DRINKS.size();
                if (drinkId < 0)
                    drinkId = size - 1;
                else if (drinkId >= size)
                    drinkId = 0;
                break;
            }
        }
    }

    @Override
    public int getFieldCount() {
        return 1;
    }

    @Override
    public void clear() {
        itemStacks.clear();
    }

    @Override
    public String getName() {
        return "tile.drinks_store";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
