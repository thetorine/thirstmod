package com.thetorine.thirstmod.common.items;

import java.util.Random;
import com.thetorine.thirstmod.common.main.ThirstMod;
import cpw.mods.fml.common.FMLCommonHandler;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenJungle;

public class ItemThirst extends Item {
	
	private boolean addItem;

	public ItemThirst() {
		super();
		setCreativeTab(ThirstMod.drinkTab);
	}

	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (this.getUnlocalizedName().equals(DrinkLoader.woodGlass.getUnlocalizedName())) {
			MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(world, player, true);

			if (mop != null) {
				int x = mop.blockX;
				int y = mop.blockY;
				int z = mop.blockZ;
				
				if(!world.isRemote) {
					if (world.getBlock(x, y, z).getMaterial() == Material.water) {
						addItem = true;
					} else if (world.getBlock(x, y, z) == Blocks.leaves) {
						Random random = new Random();
						if (world.getBiomeGenForCoords(x, z) instanceof BiomeGenJungle) {
							world.setBlockMetadataWithNotify(x, y, z, 0, 0x02);
							if (random.nextFloat() < 0.3f) {
								addItem = true;
							}
						}
					}
					
					if(addItem) {
						 --stack.stackSize;
						 if(stack.stackSize <= 0) {
							 return new ItemStack(DrinkLoader.woodWater);
						 }
						 if(!player.inventory.addItemStackToInventory(new ItemStack(DrinkLoader.woodWater))) {
							 world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(DrinkLoader.woodWater)));
				         }
					}
				}
			}
		}
		return stack;
	}
}
