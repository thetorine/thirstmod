package com.thetorine.thirstmod.core.content.blocks;

import com.thetorine.thirstmod.core.content.ItemLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ContainerDS extends Container {
	private TileEntityDS tile;
	
	public ContainerDS(InventoryPlayer inv, TileEntityDS tile) {
		this.tile = tile;

		addSlotToContainer(new SlotDS(tile, 0, 34, 28)); // drink
		addSlotToContainer(new SlotFurnaceOutput(inv.player, tile, 1, 34, 56)); //return
		addSlotToContainer(new Slot(tile, 2, 8, 41)); //coins

		for(int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(inv, j + (i * 9) + 9, 8 + (j * 18), 84 + (i * 18)));
			}
		}

		for(int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(inv, i, 8 + (i * 18), 142));
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
					if(stack.getUnlocalizedName().equals(ItemLoader.gold_coin.getUnlocalizedName())) {
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
		craft.func_175173_a(this, tile);
	}
}
