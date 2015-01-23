package com.thetorine.thirstmod.core.content.blocks;

import com.thetorine.thirstmod.core.content.ItemLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerDS extends Container {
	private TileEntityDS tileEntity;
	
	public ContainerDS(InventoryPlayer inv, TileEntityDS tile) {
		this.tileEntity = tile;

		// 0=drink, 1=coins, 2=return
		addSlotToContainer(new SlotDS(tile, 0, 34, 28));
		addSlotToContainer(new SlotFurnace(inv.player, tile, 1, 34, 56));
		addSlotToContainer(new Slot(tile, 2, 8, 41));

		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
			}
		}
		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + (i * 18), 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUseableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		if (tileEntity.items[2] != null && !player.worldObj.isRemote) {
			player.dropItem(tileEntity.items[2].getItem(), tileEntity.items[2].stackSize);
			tileEntity.items[2] = null;
		}

		if (tileEntity.items[1] != null && !player.worldObj.isRemote) {
			player.dropItem(tileEntity.items[1].getItem(), tileEntity.items[1].stackSize);
			tileEntity.items[1] = null;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotid) {
		super.transferStackInSlot(player, slotid);
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotid);
		if (slot != null && slot.getHasStack()) {
			stack = slot.getStack();
			switch(slotid) {
				case 0: 
				case 1:
				case 2: {
					if(!this.mergeItemStack(stack, 3, inventorySlots.size(), false)) return null;
					break;
				}
				default: {
					if(stack.getUnlocalizedName().equals(ItemLoader.goldCoin.getUnlocalizedName())) {
						if(!this.mergeItemStack(stack, 2, 3, false)) return null;
					} else {
						return null;
					}
				}
			}
			if(stack.stackSize == 0) {
				slot.putStack(null);
			}
		}
		return stack;
	}

	@Override
	public void addCraftingToCrafters(ICrafting craft) {
		super.addCraftingToCrafters(craft);
	}
}
