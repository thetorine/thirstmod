package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.common.logic.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class TileEntityRainCollector extends TileEntity implements ITickable, ISidedInventory {

    private NonNullList<ItemStack> itemStacks = NonNullList.<ItemStack>withSize(2, ItemStack.EMPTY);
    private final int tankCapacity = 640;
    private int currentLevel = 0;
    private int fillTime = 0;

    @Override
    public void update() {
        if (!world.isRemote) {
            if (canRainOn(pos, world)) {
                currentLevel = Math.min(tankCapacity, currentLevel + 1);
            }

            ItemStack inputStack = itemStacks.get(0);
            ItemStack outputStack = itemStacks.get(1);
            Recipes.Output output = Recipes.getRainCollectorRecipe(inputStack.getItem());

            if (currentLevel > 0 && output != null) {
                if (outputStack.isEmpty() ||
                        (outputStack.isItemEqual(output.outputItem) && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    fillTime = Math.min(fillTime + 1, output.manufactureTime);
                    currentLevel = Math.max(currentLevel - 1, 0);
                    if (fillTime == output.manufactureTime) {
                        decrStackSize(0, 1);
                        if (itemStacks.get(1).isEmpty()) {
                            itemStacks.set(1, output.outputItem.copy());
                        } else {
                            itemStacks.get(1).setCount(itemStacks.get(1).getCount() + 1);
                        }
                        fillTime = 0;
                    }
                }
            }
        }
    }

    public boolean canRainOn(BlockPos pos, World world) {
        Biome biome = world.getBiome(pos);
        //don't turn rain collector on if there is no rain (rain disabled or snowing)
        if(!biome.canRain() || biome.getFloatTemperature(pos) < 0.15f) return false;
        for (int l = pos.getY() + 1; l < world.getHeight(); l++) {
            if (world.getBlockState(new BlockPos(pos.getX(), l, pos.getZ())).getBlock() != Blocks.AIR) {
                return false;
            }
        }
        return world.isRaining();
    }

    public int getTankCapacityScaled(int scale) {
        return (currentLevel * scale) / tankCapacity;
    }

    public int getProgressBarScaled(int scale) {
        ItemStack inputStack = itemStacks.get(0);
        Recipes.Output output = Recipes.getRainCollectorRecipe(inputStack.getItem());
        if (output != null) {
            return (fillTime * scale) / output.manufactureTime;
        }
        return 0;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setInteger("current_level", currentLevel);
        compound.setInteger("fill_time", fillTime);
        ItemStackHelper.saveAllItems(compound, itemStacks);
        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        ItemStackHelper.loadAllItems(compound, itemStacks);
        currentLevel = compound.getInteger("current_level");
        fillTime = compound.getInteger("fill_time");
    }

    @Override
    public int[] getSlotsForFace(EnumFacing side) {
        return new int[0];
    }

    @Override
    public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
        if (index == 1) return false;
        return Recipes.getRainCollectorRecipe(itemStackIn.getItem()) != null;
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
        if (index == 0 && !flag) {
            fillTime = 0;
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
        if (index == 1) return false;
        else {
            return Recipes.getRainCollectorRecipe(stack.getItem()) != null;
        }
    }

    @Override
    public int getField(int id) {
        switch (id) {
            case 0: return currentLevel;
            case 1: return fillTime;
            default: return 0;
        }
    }

    @Override
    public void setField(int id, int value) {
        switch (id) {
            case 0: currentLevel = value; break;
            case 1: fillTime = value;
        }
    }

    @Override
    public int getFieldCount() {
        return 2;
    }

    @Override
    public void clear() {
        itemStacks.clear();
    }

    @Override
    public String getName() {
        return "tile.rain_collector";
    }

    @Override
    public boolean hasCustomName() {
        return false;
    }
}
