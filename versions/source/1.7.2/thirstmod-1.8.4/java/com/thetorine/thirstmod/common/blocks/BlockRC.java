package com.thetorine.thirstmod.common.blocks;

import java.util.Random;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockRC extends BlockContainer {
	private static boolean keepRCInventory = false;
	private Random rand = new Random();
	@SideOnly(Side.CLIENT)
	private IIcon topTexture;

	public BlockRC() {
		super(Material.rock);
	}

	@Override
	public Item getItemDropped(int par1, Random par2Random, int par3) {
		return Item.getItemFromBlock(ThirstMod.waterCollector);
	}

	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(ThirstMod.INSTANCE, 91, world, x, y, z);
		return true;
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityRC();
	}

	@Override
	public void onBlockAdded(World world, int par2, int par3, int par4) {
		super.onBlockAdded(world, par2, par3, par4);
		setDefaultDirection(world, par2, par3, par4);
	}

	private void setDefaultDirection(World world, int x, int y, int z) {
		if (!world.isRemote) {
			Block block = world.getBlock(x, y, z - 1);
			Block block1 = world.getBlock(x, y, z + 1);
			Block block2 = world.getBlock(x - 1, y, z);
			Block block3 = world.getBlock(x + 1, y, z);
			byte b0 = 3;

			if (block.func_149730_j() && !block1.func_149730_j()) {
				b0 = 3;
			}

			if (block1.func_149730_j() && !block.func_149730_j()) {
				b0 = 2;
			}

			if (block2.func_149730_j() && !block3.func_149730_j()) {
				b0 = 5;
			}

			if (block3.func_149730_j() && !block2.func_149730_j()) {
				b0 = 4;
			}

			world.setBlockMetadataWithNotify(x, y, z, b0, 2);
		}
	}

	public void onBlockPlacedBy(World world, int par2, int par3, int par4, EntityLiving living, ItemStack stack) {
		int l = MathHelper.floor_double((living.rotationYaw * 4.0F) / 360.0F + 0.5D) & 3;
		if (l == 0) {
			world.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
		}
		if (l == 1) {
			world.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
		}
		if (l == 2) {
			world.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
		}
		if (l == 3) {
			world.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		}
	}

	@Override
	public void breakBlock(World world, int par2, int par3, int par4, Block par5, int par6) {
		if (!keepRCInventory) {
			TileEntityRC tileentityfurnace = (TileEntityRC) world.getTileEntity(par2, par3, par4);

			if (tileentityfurnace != null) {
				label0:

				for (int i = 0; i < tileentityfurnace.getSizeInventory(); i++) {
					ItemStack itemstack = tileentityfurnace.getStackInSlot(i);

					if (itemstack == null) {
						continue;
					}

					float f = (rand.nextFloat() * 0.8F) + 0.1F;
					float f1 = (rand.nextFloat() * 0.8F) + 0.1F;
					float f2 = (rand.nextFloat() * 0.8F) + 0.1F;

					do {
						if (itemstack.stackSize <= 0) {
							continue label0;
						}

						int j = rand.nextInt(21) + 10;

						if (j > itemstack.stackSize) {
							j = itemstack.stackSize;
						}

						itemstack.stackSize -= j;
						EntityItem entityitem = new EntityItem(world, par2 + f, par3 + f1, par4 + f2, new ItemStack(itemstack.getItem(), j, itemstack.getItemDamage()));

						if (itemstack.hasTagCompound()) {
							entityitem.getEntityItem().setTagCompound((NBTTagCompound) itemstack.getTagCompound().copy());
						}

						float f3 = 0.05F;
						entityitem.motionX = (float) rand.nextGaussian() * f3;
						entityitem.motionY = ((float) rand.nextGaussian() * f3) + 0.2F;
						entityitem.motionZ = (float) rand.nextGaussian() * f3;
						world.spawnEntityInWorld(entityitem);
					} while (true);
				}
			}
		}

		super.breakBlock(world, par2, par3, par4, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thirstmod:side");
		topTexture = iconRegister.registerIcon("thirstmod:rain_top");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return side == 1 ? topTexture : blockIcon;
	}
}
