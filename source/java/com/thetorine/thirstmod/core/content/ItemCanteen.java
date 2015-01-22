package com.thetorine.thirstmod.core.content;

import java.util.List;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.player.PlayerContainer;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCanteen extends Item {
	private String[] canteenNames = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

	public ItemCanteen() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(true);
		setTextureName("thirstmod:canteen");
		setCreativeTab(ThirstMod.thirst);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.canteen." + canteenNames[stack.getItemDamage()];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		for (int j = 0; j < 11; ++j) {
			par3List.add(new ItemStack(par1, 1, j));
		}
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			PlayerContainer playerT = PlayerContainer.getPlayer(player.getDisplayName());
			if (itemstack.getItemDamage() > 0) {
				playerT.getStats().addStats((itemstack.getItemDamage() < 6 ? 2 : 3), 1.2F);
				if ((itemstack.getItemDamage() <= 5) && (world.rand.nextFloat() < 0.4f)) {
					PlayerContainer.getPlayer(player.getDisplayName()).getStats().poisonLogic.poisonPlayer();;
				}
				return new ItemStack(this, 1, getDecrementedDamage(itemstack.getItemDamage()));
			}
		}
		return itemstack;
	}

	public int getDecrementedDamage(int i) {
		return i == 6 ? 0 : i - 1;
	}

	@Override
	public int getMaxItemUseDuration(ItemStack itemstack) {
		return 32;
	}

	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.drink;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		super.addInformation(stack, player, list, flag);
		if(stack.getItemDamage() > 0) {
			if(stack.getItemDamage() < 6) {
				list.add("Heals 1 Droplet");
			} else {
				list.add("Heals 1.5 Droplets");
			}
		}
	}
	

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		PlayerContainer playerT = PlayerContainer.getPlayer(player.getDisplayName());
		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);
		if (movingobjectposition == null) {
			if ((itemstack.getItemDamage() > 0) && canDrink(player)) {
				player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
			return itemstack;
		}
		if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;
			if (world.getBlock(i, j, k).getMaterial() == Material.water) {
				if (itemstack.getItemDamage() < 5) { 
					return new ItemStack(this, 1, 5); 
				}
			} else if ((itemstack.getItemDamage() > 0) && (playerT.getStats().thirstLevel < 20)) {
				player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
		}
		return itemstack;
	}

	public boolean canDrink(EntityPlayer player) {
		if(!player.worldObj.isRemote) {
			return PlayerContainer.getPlayer(player.getDisplayName()).getStats().thirstLevel < 20;
		} else {
			return ClientStats.getInstance().level < 20;
		}
	}
}