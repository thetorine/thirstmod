package com.thetorine.thirstmod.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.*;
import net.minecraft.item.ItemStack;

public class ContainerDrinksStore extends Container {

    public IInventory tileEntity;
    private int drinkId;

    public ContainerDrinksStore(InventoryPlayer inventoryPlayer, IInventory iInventory) {
        tileEntity = iInventory;

        addSlotToContainer(new Slot(iInventory, 0, 34, 28)); // container
        addSlotToContainer(new SlotFurnaceOutput(inventoryPlayer.player, iInventory, 1, 34, 56)); //return

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
            if (drinkId != tileEntity.getField(0)) {
                listener.sendWindowProperty(this, 0, tileEntity.getField(0));
            }
        }
        drinkId = tileEntity.getField(0);
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
        return super.transferStackInSlot(playerIn, index);
    }
}
