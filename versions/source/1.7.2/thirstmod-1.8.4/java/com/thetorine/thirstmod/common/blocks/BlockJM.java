package com.thetorine.thirstmod.common.blocks;

import java.util.Random;
import com.thetorine.thirstmod.common.content.ContentLoader;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockJM extends BlockContainer {
	private Random random = new Random();
	private static boolean keepJuiceInventory = false;
	private IIcon frontTexture;

	public BlockJM() {
		super(Material.rock);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return Item.getItemFromBlock(ThirstMod.juiceMaker);
	}

	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer entityplayer, int j, float f, float f1, float f2) {
		if (ContentLoader.addedfiles.size() > 0) {
			entityplayer.openGui(ThirstMod.INSTANCE, 90, par1World, x, y, z);
		} else {
			entityplayer.addChatComponentMessage(new ChatComponentText("Install Drink Packs to use the Drinks Brewer!"));
		}
		return true;
	}

	@Override
	public void onBlockAdded(World world, int i, int j, int k) {
		super.onBlockAdded(world, i, j, k);
		setDefaultDirection(world, i, j, k);
	}

	@Override
	public TileEntity createNewTileEntity(World world, int i) {
		return new TileEntityJM();
	}

	@Override
	public void onBlockPlacedBy(World par1World, int par2, int par3, int par4, EntityLivingBase par5EntityLivingBase, ItemStack par6ItemStack) {
		int l = MathHelper.floor_double((par5EntityLivingBase.rotationYaw * 4F) / 360F + 0.5D) & 3;
		if (l == 0) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 2, 2);
		}
		if (l == 1) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 5, 2);
		}
		if (l == 2) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 3, 2);
		}
		if (l == 3) {
			par1World.setBlockMetadataWithNotify(par2, par3, par4, 4, 2);
		}
	}

	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
		if (!keepJuiceInventory) {
			TileEntityJM tileentityJuice = (TileEntityJM) par1World.getTileEntity(par2, par3, par4);
			label0: for (int l = 0; l < tileentityJuice.getSizeInventory(); l++) {
				ItemStack itemstack = tileentityJuice.getStackInSlot(l);
				if (itemstack == null) {
					continue;
				}
				float f = (random.nextFloat() * 0.8F) + 0.1F;
				float f1 = (random.nextFloat() * 0.8F) + 0.1F;
				float f2 = (random.nextFloat() * 0.8F) + 0.1F;
				do {
					if (itemstack.stackSize <= 0) {
						continue label0;
					}
					int i1 = random.nextInt(21) + 10;
					if (i1 > itemstack.stackSize) {
						i1 = itemstack.stackSize;
					}
					itemstack.stackSize -= i1;
					EntityItem entityitem = new EntityItem(par1World, par2 + f, par3 + f1, par4 + f2, new ItemStack(itemstack.getItem(), i1, itemstack.getItemDamage()));
					float f3 = 0.05F;
					entityitem.motionX = (float) random.nextGaussian() * f3;
					entityitem.motionY = ((float) random.nextGaussian() * f3) + 0.2F;
					entityitem.motionZ = (float) random.nextGaussian() * f3;
					par1World.spawnEntityInWorld(entityitem);
				} while (true);
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister iconRegister) {
		blockIcon = iconRegister.registerIcon("thirstmod:side");
		frontTexture = iconRegister.registerIcon("thirstmod:db_front");
	}

	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int metadata) {
		return side != metadata ? blockIcon : frontTexture;
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
}
