package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.common.logic.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;

public class ContainerDrinksBrewer extends Container {

    public IInventory tileEntity;
    private int fuelRemaining;
    private int fillTime;
    private int manufactureTime;
    private int maxFuel;

    public ContainerDrinksBrewer(InventoryPlayer inventoryPlayer, IInventory iInventory) {
        tileEntity = iInventory;
        addSlotToContainer(new Slot(tileEntity, 0, 30, 24));
        addSlotToContainer(new Slot(tileEntity, 1, 58, 24));
        addSlotToContainer(new Slot(tileEntity, 2, 44, 47));
        addSlotToContainer(new SlotFurnaceOutput(inventoryPlayer.player, tileEntity, 3, 116, 35));
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                addSlotToContainer(new Slot(inventoryPlayer, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
            }
        }
        for (int i = 0; i < 9; ++i) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + (i * 18), 142));
        }
    }

    @Override
    public void addListener(IContainerListener listener) {
        super.addListener(listener);
        listener.sendAllWindowProperties(this, tileEntity);
    }

    @Override
    public void detectAndSendChanges() {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners) {
            if (fuelRemaining != tileEntity.getField(0)) {
                listener.sendWindowProperty(this, 0, tileEntity.getField(0));
            }
            if (fillTime != tileEntity.getField(1)) {
                listener.sendWindowProperty(this, 1, tileEntity.getField(1));
            }
            if (manufactureTime != tileEntity.getField(2)) {
                listener.sendWindowProperty(this, 2, tileEntity.getField(2));
            }
            if (maxFuel != tileEntity.getField(3)) {
                listener.sendWindowProperty(this, 3, tileEntity.getField(3));
            }
        }
        fuelRemaining = tileEntity.getField(0);
        fillTime = tileEntity.getField(1);
        manufactureTime = tileEntity.getField(2);
        maxFuel = tileEntity.getField(3);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();
            switch(index) {
                case 0:
                case 1:
                case 2:
                case 3: {
                    if(!mergeItemStack(stack, 4, inventorySlots.size(), false)) return ItemStack.EMPTY;
                    break;
                }
                default: {
                    if (Recipes.getContainerFromItemStack(stack) != Recipes.DrinkContainer.UNKNOWN) {
                        if (!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
                    } else if (Recipes.getDrinksBrewerRecipe(stack.getItem()) != null) {
                        if(!mergeItemStack(stack, 1, 2, false)) return ItemStack.EMPTY;
                    } else if (TileEntityFurnace.getItemBurnTime(stack) > 0) {
                        if (!mergeItemStack(stack, 2, 3, false)) return ItemStack.EMPTY;
                    } else {
                        return ItemStack.EMPTY;
                    }
                }
            }
            if(stack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            }
        }
        return stack;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntity.isUsableByPlayer(playerIn);
    }
}
