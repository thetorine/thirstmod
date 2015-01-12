package com.thetorine.thirstmod.core.content.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerDS extends Container {
	private TileEntityDS tile;

	private int lastPage;
	private int lastAmount;
	private int lastBuy;

	public ContainerDS(InventoryPlayer inv, TileEntityDS tile) {
		this.tile = tile;

		// 0=drink, 1=coins, 2=return
		addSlotToContainer(new SlotDS(tile, 0, 34, 28));
		addSlotToContainer(new SlotFurnace(inv.player, tile, 1, 34, 56));
		addSlotToContainer(new Slot(tile, 2, 8, 41));

		int var3;
		for (var3 = 0; var3 < 3; ++var3) {
			for (int var4 = 0; var4 < 9; ++var4) {
				addSlotToContainer(new Slot(inv, var4 + (var3 * 9) + 9, 8 + (var4 * 18), 84 + (var3 * 18)));
			}
		}

		for (var3 = 0; var3 < 9; ++var3) {
			addSlotToContainer(new Slot(inv, var3, 8 + (var3 * 18), 142));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tile.isUseableByPlayer(player);
	}

	@Override
	public void onContainerClosed(EntityPlayer player) {
		if ((tile.items[2] != null) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
			player.dropItem(tile.items[2].getItem(), tile.items[2].stackSize);
			tile.items[2] = null;
		}

		if ((tile.items[1] != null) && (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER)) {
			player.dropItem(tile.items[1].getItem(), tile.items[1].stackSize);
			tile.items[1] = null;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotid) {
		super.transferStackInSlot(player, slotid);
		ItemStack stack = null;
		Slot slot = (Slot) inventorySlots.get(slotid);
		if ((slot != null) && slot.getHasStack()) {
			ItemStack stack1 = slot.getStack();
			stack = stack1.copy();

			if (slotid == 1) {
				mergeItemStack(stack1, 3, 38, false);
			} else if (slotid == 2) {
				mergeItemStack(stack1, 3, 38, false);
			} else if ((slotid > 2) && (slotid < 38)) {
				mergeItemStack(stack1, 1, 2, false);
			}

			if (stack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (stack1.stackSize == stack.stackSize) { return null; }
			slot.putStack(stack1);
		}
		return stack;
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int var1 = 0; var1 < crafters.size(); ++var1) {
			ICrafting var2 = (ICrafting) crafters.get(var1);
			if (lastPage != tile.page) {
				var2.sendProgressBarUpdate(this, 0, tile.page);
			}

			if (lastAmount != tile.amountToBuy) {
				var2.sendProgressBarUpdate(this, 1, tile.amountToBuy);
			}

			if (lastBuy != tile.canBuy) {
				var2.sendProgressBarUpdate(this, 2, tile.canBuy);
			}
		}

		lastPage = tile.page;
		lastAmount = tile.amountToBuy;
		lastBuy = tile.canBuy;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int packet, int value) {
		super.updateProgressBar(packet, value);
		switch (packet) {
			case 0:
				tile.page = value;
				return;
			case 1:
				tile.amountToBuy = value;
				return;
			case 2:
				tile.canBuy = value;
				return;
			default:
		}
	}

	@Override
	public void addCraftingToCrafters(ICrafting craft) {
		super.addCraftingToCrafters(craft);
		craft.sendProgressBarUpdate(this, 0, tile.page);
		craft.sendProgressBarUpdate(this, 1, tile.amountToBuy);
		craft.sendProgressBarUpdate(this, 2, tile.canBuy);
	}
}
