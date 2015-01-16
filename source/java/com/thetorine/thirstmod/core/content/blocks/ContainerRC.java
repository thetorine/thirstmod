package com.thetorine.thirstmod.core.content.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerRC extends Container {
	private TileEntityRC rc;
	private int lastRainMeter = 0;
	private int lastInternalBucket = 0;

	public ContainerRC(InventoryPlayer ip, TileEntityRC tile) {
		rc = tile;
		addSlotToContainer(new Slot(tile, 0, 56, 53));
		addSlotToContainer(new SlotFurnace(ip.player, tile, 1, 116, 35));

		int i;
		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(ip, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
			}
		}

		for (i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(ip, i, 8 + (i * 18), 142));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			stack = slot.getStack();
			switch(index) {
				case 0:
				case 1: {
					if(!mergeItemStack(stack, 2, inventorySlots.size(), false)) return null;
					break;
				}
				default: {
					if(RCRecipes.getInputResult(stack.getUnlocalizedName()) != null) {
						if(!mergeItemStack(stack, 0, 1, false)) return null;
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
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int var1 = 0; var1 < crafters.size(); ++var1) {
			ICrafting var2 = (ICrafting) crafters.get(var1);
			if (lastRainMeter != rc.rainMeter) {
				var2.sendProgressBarUpdate(this, 0, rc.rainMeter);
			}

			if (lastInternalBucket != rc.internalBucket) {
				var2.sendProgressBarUpdate(this, 1, rc.internalBucket);
			}
		}

		lastRainMeter = rc.rainMeter;
		lastInternalBucket = rc.internalBucket;
	}

	@Override
	public void updateProgressBar(int i, int j) {
		super.updateProgressBar(i, j);
		switch (i) {
			case 0:
				rc.rainMeter = j;
				return;
			case 1:
				rc.internalBucket = j;
				return;
			default:
		}
	}

	@SuppressWarnings({ "unchecked" })
	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		crafters.add(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, this.rc.rainMeter);
		par1ICrafting.sendProgressBarUpdate(this, 1, this.rc.internalBucket);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return rc.isUseableByPlayer(entityplayer);
	}
}
