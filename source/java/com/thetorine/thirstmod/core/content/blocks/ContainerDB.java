package com.thetorine.thirstmod.core.content.blocks;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		this.addSlotToContainer(new SlotFurnaceOutput(inv.player, tile, 3, 116, 35)); // return
		
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
			if(lastFuelLevel != drinksBrewer.getField(0)) {
				craft.sendProgressBarUpdate(this, 0, drinksBrewer.getField(0));
			}
			if(lastBrewTime != drinksBrewer.getField(1)) {
				craft.sendProgressBarUpdate(this, 1, drinksBrewer.getField(1));
			}
			if(lastMaxFuelLevel != drinksBrewer.getField(2)) {
				craft.sendProgressBarUpdate(this, 2, drinksBrewer.getField(2));
			}
			if(lastMaxBrewTime != drinksBrewer.getField(3)) {
				craft.sendProgressBarUpdate(this, 3, drinksBrewer.getField(3));
			}
		}
		lastFuelLevel = drinksBrewer.getField(0);
		lastBrewTime = drinksBrewer.getField(1);
		lastMaxFuelLevel = drinksBrewer.getField(2);
		lastMaxBrewTime = drinksBrewer.getField(3);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int packet, int data) {
		drinksBrewer.setField(packet, data);
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
		craft.func_175173_a(this, drinksBrewer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		return this.drinksBrewer.isUseableByPlayer(entityplayer);
	}
}
