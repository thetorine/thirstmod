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
	private TileEntityDB drinksBrewer;
	private int lastFuelLevel;
	private int lastBrewTime;
	private int lastMaxFuelLevel;
	private int lastMaxBrewTime;

	public ContainerDB(InventoryPlayer inv, TileEntityDB tile) {
		this.drinksBrewer = tile;
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
			if(lastFuelLevel != drinksBrewer.fuelLevel) {
				craft.sendProgressBarUpdate(this, 0, drinksBrewer.fuelLevel);
			}
			if(lastBrewTime != drinksBrewer.brewTime) {
				craft.sendProgressBarUpdate(this, 1, drinksBrewer.brewTime);
			}
			if(lastMaxFuelLevel != drinksBrewer.maxFuelLevel) {
				craft.sendProgressBarUpdate(this, 2, drinksBrewer.maxFuelLevel);
			}
			if(lastMaxBrewTime != drinksBrewer.maxBrewTime) {
				craft.sendProgressBarUpdate(this, 3, drinksBrewer.maxBrewTime);
			}
		}
		lastFuelLevel = drinksBrewer.fuelLevel;
		lastBrewTime = drinksBrewer.brewTime;
		lastMaxFuelLevel = drinksBrewer.maxFuelLevel;
		lastMaxBrewTime = drinksBrewer.maxBrewTime;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int packet, int data) {
		switch(packet) {
			case 0: drinksBrewer.fuelLevel = data; break;
			case 1: drinksBrewer.brewTime = data; break;
			case 2: drinksBrewer.maxFuelLevel = data; break;
			case 3: drinksBrewer.maxBrewTime = data; break;
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
					} else if(drinksBrewer.getBrewedDrink(stack) != null) {
						if(!this.mergeItemStack(stack, 0, 1, false)) return null;
					} else if(drinksBrewer.getItemFuelValue(stack) > 0) {
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
		craft.sendProgressBarUpdate(this, 0, drinksBrewer.fuelLevel);
		craft.sendProgressBarUpdate(this, 1, drinksBrewer.brewTime);
		craft.sendProgressBarUpdate(this, 2, drinksBrewer.maxFuelLevel);
		craft.sendProgressBarUpdate(this, 3, drinksBrewer.maxBrewTime);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.drinksBrewer.isUseableByPlayer(entityplayer);
	}
}
