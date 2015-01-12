package com.thetorine.thirstmod.core.content.blocks;

import java.util.Random;

import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.utils.Constants;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class BlockRC extends BlockContainer {
	private IIcon topTexture;
	private IIcon sideTexture;
	private static boolean keepInventory;

	public BlockRC() {
		super(Material.rock);
		setResistance(5f);
		setHardness(4f);
		setCreativeTab(ThirstMod.thirst);
	}

	@Override
	public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
		return new TileEntityRC();
	}
	
	@Override
	public boolean onBlockActivated(World par1World, int x, int y, int z, EntityPlayer player, int par6, float par7, float par8, float par9) {
		player.openGui(ThirstMod.instance, Constants.RAIN_COLLECTOR_ID, par1World, x, y, z);
		return true;
	}
	
	@Override
	public void registerBlockIcons(IIconRegister icon) {
		topTexture = icon.registerIcon("thirstmod:rain_top");
		sideTexture = icon.registerIcon("furnace_top");
	}
	
	@Override
	public IIcon getIcon(int side, int meta) {
		if(side == 1) {
			return topTexture;
		}
		return sideTexture;
	}
	
	@Override
	public void breakBlock(World par1World, int par2, int par3, int par4, Block par5, int par6) {
		if (!keepInventory) {
			TileEntityRC tileentity = (TileEntityRC) par1World.getTileEntity(par2, par3, par4);
			Random random = new Random();
			label0: for (int l = 0; l < tileentity.getSizeInventory(); l++) {
				ItemStack itemstack = tileentity.getStackInSlot(l);
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
}

