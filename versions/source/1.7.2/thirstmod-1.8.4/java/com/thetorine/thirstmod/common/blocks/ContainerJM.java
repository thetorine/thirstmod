package com.thetorine.thirstmod.common.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerJM extends Container {
	private TileEntityJM drinksBrewer;
	private int lastCookTime = 0;
	private int lastBurnTime = 0;
	private int lastItemBurnTime = 0;

	public ContainerJM(InventoryPlayer inv, TileEntityJM tile) {
		this.drinksBrewer = tile;
		this.addSlotToContainer(new Slot(tile, 0, 58, 24)); // brewing item
		this.addSlotToContainer(new Slot(tile, 1, 30, 24)); // glass
		this.addSlotToContainer(new Slot(tile, 3, 44, 47)); // fuel
		this.addSlotToContainer(new SlotFurnace(inv.player, tile, 2, 116, 35)); // return
		int i;

		for (i = 0; i < 3; ++i) {
			for (int var4 = 0; var4 < 9; ++var4) {
				this.addSlotToContainer(new Slot(inv, var4 + (i * 9) + 9, 8 + (var4 * 18), 84 + (i * 18)));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + (i * 18), 142));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (int var1 = 0; var1 < this.crafters.size(); ++var1) {
			ICrafting var2 = (ICrafting) this.crafters.get(var1);

			if (this.lastCookTime != this.drinksBrewer.brewCreateTime) {
				var2.sendProgressBarUpdate(this, 0, this.drinksBrewer.brewCreateTime);
			}

			if (this.lastBurnTime != this.drinksBrewer.brewTime) {
				var2.sendProgressBarUpdate(this, 1, this.drinksBrewer.brewTime);
			}

			if (this.lastItemBurnTime != this.drinksBrewer.currentItemCreate) {
				var2.sendProgressBarUpdate(this, 2, this.drinksBrewer.currentItemCreate);
			}
		}

		this.lastCookTime = this.drinksBrewer.brewCreateTime;
		this.lastBurnTime = this.drinksBrewer.brewTime;
		this.lastItemBurnTime = this.drinksBrewer.currentItemCreate;
	}

	@Override
	public void updateProgressBar(int par1, int par2) {
		if (par1 == 0) {
			this.drinksBrewer.brewCreateTime = par2;
		}

		if (par1 == 1) {
			this.drinksBrewer.brewTime = par2;
		}

		if (par1 == 2) {
			this.drinksBrewer.currentItemCreate = par2;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int par1) {
		ItemStack stack = null;
		Slot i = (Slot) this.inventorySlots.get(par1);

		if ((i != null) && i.getHasStack()) {
			ItemStack var4 = i.getStack();
			stack = var4.copy();

			if (par1 == 2) {
				i.onSlotChange(var4, stack);
			} else if ((par1 != 1) && (par1 != 0) && (par1 != 3)) {
				if (JMRecipes.solidifying().getSmeltingResult(var4) != null) {
					if (!this.mergeItemStack(var4, 0, 1, false)) { return null; }
				} else if (TileEntityJM.getItemMake(var4) > 0) {
					if (!this.mergeItemStack(var4, 3, 2, false)) { return null; }
				} else if ((par1 >= 4) && (par1 < 30)) {
					if (!this.mergeItemStack(var4, 30, 39, false)) { return null; }
				}
			} else if (!this.mergeItemStack(var4, 4, 39, false)) { return null; }

			if (var4.stackSize == 0) {
				i.putStack((ItemStack) null);
			} else {
				i.onSlotChanged();
			}

			if (var4.stackSize == stack.stackSize) { return null; }

			i.onPickupFromSlot(player, var4);
		}
		return stack;
	}

	@Override
	public void addCraftingToCrafters(ICrafting par1ICrafting) {
		super.addCraftingToCrafters(par1ICrafting);
		par1ICrafting.sendProgressBarUpdate(this, 0, this.drinksBrewer.brewTime);
		par1ICrafting.sendProgressBarUpdate(this, 1, this.drinksBrewer.brewCreateTime);
		par1ICrafting.sendProgressBarUpdate(this, 2, this.drinksBrewer.currentItemCreate);
		par1ICrafting.sendProgressBarUpdate(this, 3, this.drinksBrewer.brewTime);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.drinksBrewer.isUseableByPlayer(entityplayer);
	}
}
