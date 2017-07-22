package com.thetorine.thirstmod.common.blocks;

import com.thetorine.thirstmod.common.logic.Recipes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerRainCollector extends Container {

    public IInventory tileEntity;
    private int currentLevel;
    private int fillTime;

    public ContainerRainCollector(InventoryPlayer inventoryPlayer, IInventory iInventory) {
        tileEntity = iInventory;
        addSlotToContainer(new Slot(tileEntity, 0, 56, 53));
        addSlotToContainer(new SlotFurnaceOutput(inventoryPlayer.player, tileEntity, 1, 116, 35));
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
            if (currentLevel != tileEntity.getField(0)) {
                listener.sendWindowProperty(this, 0, tileEntity.getField(0));
            }
            if (fillTime != tileEntity.getField(1)) {
                listener.sendWindowProperty(this, 1, tileEntity.getField(1));
            }
        }
        currentLevel = tileEntity.getField(0);
        fillTime = tileEntity.getField(1);
    }

    @Override
    public void updateProgressBar(int id, int data) {
        tileEntity.setField(id, data);
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        return tileEntity.isUsableByPlayer(playerIn);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack stack = null;
        Slot slot = (Slot) inventorySlots.get(index);
        if (slot != null && slot.getHasStack()) {
            stack = slot.getStack();
            switch(index) {
                case 0:
                case 1: {
                    if(!mergeItemStack(stack, 2, inventorySlots.size(), false)) return ItemStack.EMPTY;
                    break;
                }
                default: {
                    if(Recipes.getRainCollectorRecipe(stack.getItem()) != null) {
                        if(!mergeItemStack(stack, 0, 1, false)) return ItemStack.EMPTY;
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
}
