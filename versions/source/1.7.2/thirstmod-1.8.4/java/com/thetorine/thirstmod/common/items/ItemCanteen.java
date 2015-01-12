package com.thetorine.thirstmod.common.items;

import java.util.List;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemCanteen extends Item {
	private String[] canteenNames = { "canteen_0", "canteen_1", "canteen_2", "canteen_3", "canteen_4", "canteen_5", "canteen_6", "canteen_7", "canteen_8", "canteen_9", "canteen_10", };

	public ItemCanteen() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(true);
		setTextureName("thirstmod:canteen");
		setCreativeTab(ThirstMod.drinkTab);
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return canteenNames[stack.getItemDamage()];
	}

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
			PlayerHandler playerT = PlayerHandler.getPlayer(player.getDisplayName());
			if (itemstack.getItemDamage() > 0) {
				playerT.getStats().addStats((itemstack.getItemDamage() < 6 ? 2 : 3), 1.2F);
				if ((itemstack.getItemDamage() <= 5) && (world.rand.nextFloat() < 0.4f)) {
					PlayerHandler.getPlayer(player.getDisplayName()).getStats().getPoison().startPoison();
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

	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player) {
		PlayerHandler playerT = PlayerHandler.getPlayer(player.getDisplayName());
		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);
		if (movingobjectposition == null) {
			if ((itemstack.getItemDamage() > 0) && canDrink(player.getDisplayName())) {
				player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
			return itemstack;
		}
		if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			int i = movingobjectposition.blockX;
			int j = movingobjectposition.blockY;
			int k = movingobjectposition.blockZ;
			if(FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) {
				if (world.getBlock(i, j, k).getMaterial() == Material.water) {
					if (itemstack.getItemDamage() < 5) { return new ItemStack(this, 1, 5); }
				} else if ((itemstack.getItemDamage() > 0) && (playerT.getStats().level < 20)) {
					player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
				}
			}
		}
		return itemstack;
	}

	public boolean canDrink(String username) {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? PlayerHandler.getPlayer(username).getStats().level < 20 : StatsHolder.getInstance().level < 20;
	}
}