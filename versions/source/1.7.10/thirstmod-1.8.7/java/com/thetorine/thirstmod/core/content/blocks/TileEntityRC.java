package com.thetorine.thirstmod.core.content.blocks;

import com.thetorine.thirstmod.core.content.BlockLoader;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class TileEntityRC extends TileEntity implements IInventory {
	public ItemStack rainItemStacks[];
	public int rainMeter;
	public int internalBucket;
	public boolean isActive;

	public TileEntityRC() {
		rainItemStacks = new ItemStack[2];
		rainMeter = 0;
		internalBucket = 0;
	}

	@Override
	public void updateEntity() {
		if (worldObj != null) {
			boolean flag = worldObj.getWorldInfo().isRaining();
			boolean flag1 = canRainOn(xCoord, yCoord, zCoord, worldObj);
			isActive = flag && flag1;
			if ((isActive == true) && canFill()) {
				rainMeter++;
				if (rainMeter == RCRecipes.getFillTimeFor(rainItemStacks[0].getItem().getUnlocalizedName())) {
					rainMeter = 0;
					fillItem();
				}
			} else if (isActive && !canFill()) {
				if (internalBucket < 2000) {
					internalBucket++;
				}
				rainMeter = 0;
			} else if (!isActive && canFill() && (internalBucket > 0)) {
				rainMeter++;
				internalBucket--;
				if (rainMeter == RCRecipes.getFillTimeFor(rainItemStacks[0].getItem().getUnlocalizedName())) {
					rainMeter = 0;
					fillItem();
				}
			} else if (!canFill()) {
				rainMeter = 0;
			}
		}
	}


	@Override
	public int getSizeInventory() {
		return 2;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return rainItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		if (rainItemStacks[i] != null) {
			if (rainItemStacks[i].stackSize <= j) {
				ItemStack itemstack = rainItemStacks[i];
				rainItemStacks[i] = null;
				return itemstack;
			}
			ItemStack itemstack1 = rainItemStacks[i].splitStack(j);
			if (rainItemStacks[i].stackSize == 0) {
				rainItemStacks[i] = null;
			}
			return itemstack1;
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.rainItemStacks[i] != null) {
			ItemStack var2 = this.rainItemStacks[i];
			this.rainItemStacks[i] = null;
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		rainItemStacks[i] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		return BlockLoader.rain_collector.getUnlocalizedName();
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		if (worldObj.getTileEntity(xCoord, yCoord, zCoord) != this) {
			return false;
		} else {
			return entityplayer.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64D;
		}
	}

	@Override
	public void openInventory() {}

	@Override
	public void closeInventory() {}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList tagList = nbttagcompound.getTagList("Items", 10);
		rainItemStacks = new ItemStack[getSizeInventory()];
		for(int i = 0; i < tagList.tagCount(); i++ ) {
			NBTTagCompound nbt2 = tagList.getCompoundTagAt(i);
			byte b0 = nbt2.getByte("Slot");
            if (b0 >= 0 && b0 < this.rainItemStacks.length) {
                this.rainItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbt2);
            }
		}

		rainMeter = nbttagcompound.getShort("RainMeter");
		internalBucket = nbttagcompound.getShort("InternalBucket");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("RainMeter", (short) rainMeter);
		nbttagcompound.setShort("InternalBucket", (short) internalBucket);
		NBTTagList nbttaglist = new NBTTagList();
		for(int i = 0; i < this.rainItemStacks.length; ++i) {
			if(this.rainItemStacks[i] != null) {
				NBTTagCompound nbt2 = new NBTTagCompound();
				nbt2.setByte("Slot", (byte)i);
				this.rainItemStacks[i].writeToNBT(nbt2);
				nbttaglist.appendTag(nbt2);
			}
		}
		nbttagcompound.setTag("Items", nbttaglist);
	}

	public void fillItem() {
		if (!canFill()) { return; }

		ItemStack itemstack = RCRecipes.getInputResult(rainItemStacks[0].getItem().getUnlocalizedName());

		if (rainItemStacks[1] == null) {
			rainItemStacks[1] = itemstack.copy();
		} else if (rainItemStacks[1].getUnlocalizedName().equals(itemstack.getUnlocalizedName())) {
			rainItemStacks[1].stackSize++;
		}

		if (rainItemStacks[0].getItem().isMap()) {
			rainItemStacks[0] = new ItemStack(rainItemStacks[0].getItem().setFull3D());
		} else {
			rainItemStacks[0].stackSize--;
		}

		if (rainItemStacks[0].stackSize <= 0) {
			rainItemStacks[0] = null;
		}
	}

	private boolean canFill() {
		if (rainItemStacks[0] == null) { return false; }

		ItemStack itemstack = RCRecipes.getInputResult(rainItemStacks[0].getItem().getUnlocalizedName());

		if (itemstack == null) { return false; }

		if (rainItemStacks[1] == null) { return true; }

		if (!rainItemStacks[1].isItemEqual(itemstack)) { return false; }

		if ((rainItemStacks[1].stackSize < getInventoryStackLimit()) && (rainItemStacks[1].stackSize < rainItemStacks[1].getMaxStackSize())) { return true; }

		return rainItemStacks[1].stackSize < itemstack.getMaxStackSize();
	}

	public boolean canRainOn(int i, int j, int k, World world) {
		for (int l = j + 1; l < world.getHeight(); l++) {
			if (world.getBlock(i, l, k) != Blocks.air) { return false; }
		}
		return true;
	}

	public int getRainMeterScaled(int i) {
		int fillTime;
		if (rainItemStacks[0] != null) {
			fillTime = RCRecipes.getFillTimeFor(rainItemStacks[0].getItem().getUnlocalizedName());
		} else {
			fillTime = 200;
		}
		return (rainMeter * i) / fillTime;
	}

	public int getInternalBucketScaled(int i) {
		return (internalBucket * i) / 2000;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
}
