package com.thetorine.thirstmod.core.content.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.thetorine.thirstmod.core.content.BlockLoader;
import com.thetorine.thirstmod.core.content.packs.DrinkLists;
import com.thetorine.thirstmod.core.content.packs.DrinkLists.Drink;

public class TileEntityDB extends TileEntityLockable implements IUpdatePlayerListBox {
	// 0 = item to brew, 1 = glass bottle, 2 = fuel, 3 = return.
	public ItemStack stacks[] = new ItemStack[4];
	public int fuelLevel;
	public int brewTime;
	public int maxBrewTime;
	public int maxFuelLevel;
	
	@Override
	public void update() {
		if(!worldObj.isRemote) {
			if(stacks[0] != null && stacks[1] != null && canBrew()) {
				if(fuelLevel > 0) {
					brewTime++;
					if(maxBrewTime <= 0) {
						maxBrewTime = calculateMaxBrewTime();
					}
					if(brewTime >= maxBrewTime) {
						createDrink(getBrewedDrink(stacks[0]));
						brewTime = 0;
						maxBrewTime = 0;
					}
				} else {
					int tempValue = getItemFuelValue(stacks[2]);
					if(tempValue > 0) {
						fuelLevel = tempValue;
						maxFuelLevel = tempValue;
						decrStackSize(2, 1);
					}
				}
			} else {
				brewTime = 0;
				maxBrewTime = 0;
			}
			
			if(fuelLevel > 0) {
				fuelLevel--;
			}
		}
	}
	
	public boolean canBrew() {
		ItemStack brewedDrink = getBrewedDrink(stacks[0]);
		if(brewedDrink == null) return false;
		if(stacks[3] != null) {
			if(brewedDrink.isItemEqual(stacks[3])) {
				return stacks[3].stackSize != stacks[3].getMaxStackSize();
			}
		} else {
			return true;
		}
		return false;
	}
	
	public void createDrink(ItemStack returnProduct) {
		if(stacks[3] == null) {
			stacks[3] = new ItemStack(returnProduct.getItem(), 1);
		} else {
			stacks[3].stackSize++;
		}
		decrStackSize(0, 1);
		decrStackSize(1, 1);
	}
	
	public ItemStack getBrewedDrink(ItemStack stack) {
		return DBRecipes.instance().getBrewingResult(stack);
	}
	
	public int getItemFuelValue(ItemStack stack) {
		if (stack == null) {
			return 0;
		} else {
			Item item = stack.getItem();
			if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air) {
				Block block = Block.getBlockFromItem(item);
				if (block == Blocks.wooden_slab) {
					return 150;
				} else if (block.getMaterial() == Material.wood) {
					return 300;
				} else if (block == Blocks.coal_block) {
					return 16000;
				}
			}

			if (item instanceof ItemTool && ((ItemTool) item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemSword && ((ItemSword) item).getToolMaterialName().equals("WOOD")) return 200;
			if (item instanceof ItemHoe && ((ItemHoe) item).getMaterialName().equals("WOOD")) return 200;
			if (item == Items.stick) return 100;
			if (item == Items.coal) return 1600;
			if (item == Items.lava_bucket) return 20000;
			if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
			if (item == Items.blaze_rod) return 2400;
			return GameRegistry.getFuelValue(stack);
		}
	}
	
	public int getFuelLevelScaled(int limit) {
		if(maxFuelLevel <= 0) return 0;
		return (fuelLevel * limit) / maxFuelLevel;
	}
	
	public int getBrewTimeScaled(int limit) {
		return (brewTime * limit) / maxBrewTime;
	}
	
	private int calculateMaxBrewTime() {
		ItemStack brewedDrink = getBrewedDrink(stacks[0]);
		for(Drink d : DrinkLists.LOADED_DRINKS) {
			String itemName = brewedDrink.getUnlocalizedName();
			if(itemName.equals(d.item.getUnlocalizedName())) {
				return Math.max(200, d.brewTime);
			}
		}
		return 0;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);
		NBTTagList tagList = nbt.getTagList("Items", 10);
		for(int i = 0; i < tagList.tagCount(); i++ ) {
			NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
			byte b0 = nbt2.getByte("Slot");
            if (b0 >= 0 && b0 < this.stacks.length) {
                this.stacks[b0] = ItemStack.loadItemStackFromNBT(nbt2);
            }
		}
		this.fuelLevel = nbt.getInteger("fuelLevel");
		this.brewTime = nbt.getInteger("brewTime");
		this.maxFuelLevel = nbt.getInteger("maxFuelLevel");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		super.writeToNBT(nbt);
		NBTTagList tagList = new NBTTagList();
		for(int i = 0; i < this.stacks.length; ++i) {
			if(this.stacks[i] != null) {
				NBTTagCompound nbt2 = new NBTTagCompound();
				nbt2.setByte("Slot", (byte)i);
				this.stacks[i].writeToNBT(nbt2);
				tagList.appendTag(nbt2);
			}
		}
		nbt.setTag("Items", tagList);
		
		nbt.setInteger("fuelLevel", fuelLevel);
		nbt.setInteger("brewTime", brewTime);
		nbt.setInteger("maxFuelLevel", maxFuelLevel);
	}

	@Override
	public int getSizeInventory() {
		return stacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return stacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (stacks[i] != null) {
			if (stacks[i].stackSize <= j) {
				ItemStack itemstack = stacks[i];
				stacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = stacks[i].splitStack(j);
			if (stacks[i].stackSize == 0) {
				stacks[i] = null;
			}
			return itemstack1;
		} 
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.stacks[i] != null) {
			ItemStack var2 = this.stacks[i];
			this.stacks[i] = null;
			return var2;
		} 
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		stacks[i] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(getPos()) != this) {
			return false;
		} else {
			return entityplayer.getDistanceSq(getPos().getX() + 0.5D, getPos().getY() + 0.5D, getPos().getZ() + 0.5D) <= 64D;
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		return new ContainerDB(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		return BlockLoader.drinks_brewer.getUnlocalizedName();
	}

	@Override
	public String getName() {
		return BlockLoader.drinks_brewer.getUnlocalizedName();
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public void openInventory(EntityPlayer playerIn) {}

	@Override
	public void closeInventory(EntityPlayer playerIn) {}

	@Override
	public int getField(int id) {
		switch(id) {
			case 0: return fuelLevel;
			case 1: return brewTime;
			case 2: return maxFuelLevel;
			case 3: return maxBrewTime;
			default: return 0;
		}
	}

	@Override
	public void setField(int id, int value) {
		switch(id) {
			case 0: fuelLevel = value; break;
			case 1: brewTime = value; break;
			case 2: maxFuelLevel = value; break;
			case 3: maxBrewTime = value; break;
		}
	}

	@Override
	public int getFieldCount() {
		return 4;
	}

	@Override
	public void clear() {
		stacks = new ItemStack[4];
	}
}