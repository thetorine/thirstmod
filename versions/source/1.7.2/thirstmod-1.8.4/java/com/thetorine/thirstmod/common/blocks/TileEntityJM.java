package com.thetorine.thirstmod.common.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.registry.GameRegistry;

public class TileEntityJM extends TileEntity implements IInventory {
	// 0 = item to brew, 1 = type, 2 = return, 3 = fuel.
	private ItemStack stacks[];
	public int fuelTime;
	public int currentItemCreate;
	public int brewCreateTime;
	public int brewTime;

	public TileEntityJM() {
		stacks = new ItemStack[4];
		fuelTime = 0;
		currentItemCreate = 0;
		brewCreateTime = 0;
		brewTime = 0;
	}

	@Override
	public void updateEntity() {
		boolean flag = fuelTime > 0;
		boolean flag1 = false;
		if (fuelTime > 0) {
			fuelTime--;
		}

		if (!worldObj.isRemote) {

			if ((brewTime == 0) && canSolidify()) {
				brewTime = getItemMake(stacks[3]);
				if (brewTime > 0) {
					if (stacks[0] != null) {
						if (stacks[3].getItem().hasContainerItem()) {
							stacks[3] = new ItemStack(stacks[3].getItem().getContainerItem());
						} else {
							stacks[3].stackSize--;
						}
						if (stacks[3].stackSize == 0) {
							stacks[3] = null;
						}
					}
				}
			}

			if ((fuelTime == 0) && (brewTime > 0) && canSolidify()) {
				currentItemCreate = fuelTime = getItemfuelTime(stacks[1]);
				if (fuelTime > 0) {
					flag1 = true;
					if (stacks[1] != null) {
						if (stacks[1].getItem().hasContainerItem()) {
							stacks[1] = new ItemStack(stacks[1].getItem().getContainerItem());
						} else {
							stacks[1].stackSize--;
						}
						if (stacks[1].stackSize == 0) {
							stacks[1] = null;
						}
					}
				}
			}
			if (isFreezing() && canSolidify()) {
				if (brewTime > 0) {
					brewTime--;
				}
				brewCreateTime++;

				if (brewCreateTime == 200) {

					brewCreateTime = 0;
					solidifyItem();
					flag1 = true;
				}
			} else {
				brewCreateTime = 0;
			}
		}
		if (flag != (fuelTime > 0)) {
			flag1 = true;
		}
		if (flag1) {
			markDirty();
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbttagcompound) {
		super.readFromNBT(nbttagcompound);
		NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 0);
		stacks = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			byte byte0 = nbttagcompound1.getByte("Slotbrewr");
			if ((byte0 >= 0) && (byte0 < stacks.length)) {
				stacks[byte0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}

		fuelTime = nbttagcompound.getShort("fuelTime");
		brewCreateTime = nbttagcompound.getShort("CoolTime");
		currentItemCreate = getItemfuelTime(stacks[1]);
		brewTime = nbttagcompound.getShort("SomeTime");
	}

	@Override
	public void writeToNBT(NBTTagCompound nbttagcompound) {
		super.writeToNBT(nbttagcompound);
		nbttagcompound.setShort("fuelTime", (short) fuelTime);
		nbttagcompound.setShort("CoolTime", (short) brewCreateTime);
		nbttagcompound.setShort("SomeTime", (short) brewTime);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < stacks.length; i++) {
			if (stacks[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slotbrewr", (byte) i);
				stacks[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}

		nbttagcompound.setTag("Items", nbttaglist);
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
		} else {
			return null;
		}
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		if (this.stacks[i] != null) {
			ItemStack var2 = this.stacks[i];
			this.stacks[i] = null;
			return var2;
		} else {
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		stacks[i] = itemstack;
		if ((itemstack != null) && (itemstack.stackSize > getInventoryStackLimit())) {
			itemstack.stackSize = getInventoryStackLimit();
		}
	}

	@Override
	public String getInventoryName() {
		// Is this even needed??? I think this is used for mc language stuff.
		return "Juice Maker";
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
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	public static int getItemfuelTime(ItemStack itemstack) {
		int fail = 0;
		if (itemstack == null) { return 0; }
		Item item = itemstack.getItem();
		if (item.getUnlocalizedName().contains("glassBottle")) {
			return 200;
		} else {
			return fail;
		}
	}

	private boolean canSolidify() {
		if (this.stacks[0] == null) {
			return false;
		} else {
			ItemStack var1 = JMRecipes.solidifying().getSmeltingResult(this.stacks[0]);
			if (var1 == null) { return false; }
			if (this.stacks[2] == null) { return true; }
			if (!this.stacks[2].isItemEqual(var1)) { return false; }
			int result = stacks[2].stackSize + var1.stackSize;
			return ((result <= getInventoryStackLimit()) && (result <= var1.getMaxStackSize()));
		}
	}

	public void solidifyItem() {
		if (this.canSolidify()) {
			ItemStack var1 = JMRecipes.solidifying().getSmeltingResult(stacks[0]);

			if (this.stacks[2] == null) {
				this.stacks[2] = var1.copy();
			} else if (this.stacks[2].isItemEqual(var1)) {
				++this.stacks[2].stackSize;
			}

			--this.stacks[0].stackSize;

			if (this.stacks[0].stackSize <= 0) {
				this.stacks[0] = null;
			}
		}
	}

	public static int getItemMake(ItemStack itemstack) {
		if (itemstack == null) {
			return 0;
		} else {
			Item item = itemstack.getItem();

			if ((item instanceof ItemBlock) && (Block.getBlockFromItem(item) != Blocks.air)) {
				Block block = Block.getBlockFromItem(item);

				if (block == Blocks.wooden_slab) { return 150; }

				if (block.getMaterial() == Material.wood) { return 300; }

				if (block == Blocks.coal_block) { return 16000; }
			}

			if ((item instanceof ItemTool) && ((ItemTool) item).getToolMaterialName().equals("WOOD")) { return 200; }
			if ((item instanceof ItemSword) && ((ItemSword) item).getToolMaterialName().equals("WOOD")) { return 200; }
			if ((item instanceof ItemHoe) && ((ItemHoe) item).getToolMaterialName().equals("WOOD")) { return 200; }
			if (item == Items.stick) { return 100; }
			if (item == Items.coal) { return 1600; }
			if (item == Items.lava_bucket) { return 20000; }
			if (item == Item.getItemFromBlock(Blocks.sapling)) { return 100; }
			if (item == Items.blaze_rod) { return 2400; }
			return GameRegistry.getFuelValue(itemstack);
		}
	}

	public boolean isFreezing() {
		return fuelTime > 0;
	}

	public int getCoolProgressScaled(int i) {
		return (brewCreateTime * i) / 200;
	}

	public int getFreezeTimeRemainingScaled(int i) {
		if (currentItemCreate == 0) {
			currentItemCreate = 200;
		}
		return (brewTime * i) / currentItemCreate;
	}

	public static boolean isItemFuel(ItemStack par0ItemStack) {
		return getItemfuelTime(par0ItemStack) > 0;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}
}