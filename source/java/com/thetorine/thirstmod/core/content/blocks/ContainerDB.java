package com.thetorine.thirstmod.core.content.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;

public class ContainerDB extends Container {
	private TileEntityDB tileEntity;
	private int lastFuelLevel;
	private int lastBrewTime;
	private int lastMaxFuelLevel;
	private int lastMaxBrewTime;

	public ContainerDB(InventoryPlayer inv, TileEntityDB tile) {
		this.tileEntity = tile;
		this.addSlotToContainer(new Slot(tile, 0, 58, 24)); // brewing item
		this.addSlotToContainer(new Slot(tile, 1, 30, 24)); // glass
		this.addSlotToContainer(new Slot(tile, 2, 44, 47)); // fuel
		this.addSlotToContainer(new SlotFurnace(inv.player, tile, 3, 116, 35)); // return
		int i;

		for (i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				this.addSlotToContainer(new Slot(inv, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
			}
		}

		for (i = 0; i < 9; ++i) {
			this.addSlotToContainer(new Slot(inv, i, 8 + (i * 18), 142));
		}
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (int i = 0; i < this.crafters.size(); ++i) {
			ICrafting craft = (ICrafting) this.crafters.get(i);
			if(lastFuelLevel != tileEntity.fuelLevel) {
				craft.sendProgressBarUpdate(this, 0, tileEntity.fuelLevel);
			}
			if(lastBrewTime != tileEntity.brewTime) {
				craft.sendProgressBarUpdate(this, 1, tileEntity.brewTime);
			}
			if(lastMaxFuelLevel != tileEntity.maxFuelLevel) {
				craft.sendProgressBarUpdate(this, 2, tileEntity.maxFuelLevel);
			}
			if(lastMaxBrewTime != tileEntity.maxBrewTime) {
				craft.sendProgressBarUpdate(this, 3, tileEntity.maxBrewTime);
			}
		}
		lastFuelLevel = tileEntity.fuelLevel;
		lastBrewTime = tileEntity.brewTime;
		lastMaxFuelLevel = tileEntity.maxFuelLevel;
		lastMaxBrewTime = tileEntity.maxBrewTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int packet, int data) {
		switch(packet) {
			case 0: tileEntity.fuelLevel = data; break;
			case 1: tileEntity.brewTime = data; break;
			case 2: tileEntity.maxFuelLevel = data; break;
			case 3: tileEntity.maxBrewTime = data; break;
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int index) {
		super.transferStackInSlot(player, index);
		Slot slot = (Slot) this.inventorySlots.get(index);
		ItemStack stack = null;
		if(slot != null && slot.getHasStack()) {
			stack = slot.getStack();
			switch(index) {
				case 0:
				case 1:
				case 2: 
				case 3: {
					if(!this.mergeItemStack(stack, 4, inventorySlots.size(), false)) return null;
					break;
				}
				default: {
					if(stack.getUnlocalizedName().equals(Items.glass_bottle.getUnlocalizedName())) {
						if(!this.mergeItemStack(stack, 1, 2, true)) return null;
					} else if(tileEntity.getBrewedDrink(stack) != null) {
						if(!this.mergeItemStack(stack, 0, 1, false)) return null;
					} else if(tileEntity.getItemFuelValue(stack) > 0) {
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
		craft.sendProgressBarUpdate(this, 0, tileEntity.fuelLevel);
		craft.sendProgressBarUpdate(this, 1, tileEntity.brewTime);
		craft.sendProgressBarUpdate(this, 2, tileEntity.maxFuelLevel);
		craft.sendProgressBarUpdate(this, 3, tileEntity.maxBrewTime);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.tileEntity.isUseableByPlayer(entityplayer);
	}
}
