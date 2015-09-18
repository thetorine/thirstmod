package com.thetorine.thirstmod.core.content;

import java.util.List;

import net.minecraft.block.material.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.player.PlayerContainer;

public class ItemCanteen extends Item {
	private String[] canteenNames = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

	public ItemCanteen() {
		super();
		setMaxStackSize(1);
		setMaxDamage(0);
		setHasSubtypes(true);
		setCreativeTab(ThirstMod.thirst);
		
		//add modelid for each subtype of this item.
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			String[] names = new String[canteenNames.length];
			for(int i = 0; i < names.length; i++) {
				names[i] = "thirstmod:canteen";
			}
			ModelBakery.addVariantName(this, names);
		}
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "item.canteen." + canteenNames[stack.getItemDamage()];
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubItems(Item item, CreativeTabs itemCreativeTab, List tabItemList) {
		for (int j = 0; j < 11; j+=5) {
			ItemStack stack = new ItemStack(item, 1, j);
			tabItemList.add(stack);
		}
	}

	@Override
	public ItemStack onItemUseFinish(ItemStack itemstack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			PlayerContainer playerT = PlayerContainer.getPlayer(player);
			if (itemstack.getItemDamage() > 0) {
				playerT.getStats().addStats((itemstack.getItemDamage() < 6 ? 2 : 3), 1.2F);
				if ((itemstack.getItemDamage() <= 5) && (world.rand.nextFloat() < 0.4f)) {
					PlayerContainer.getPlayer(player).getStats().poisonLogic.poisonPlayer();
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
		return EnumAction.DRINK;
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
		MovingObjectPosition movingobjectposition = getMovingObjectPositionFromPlayer(world, player, true);
		if (movingobjectposition == null) {
			if (itemstack.getItemDamage() > 0 && canDrink(player)) {
				player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
			return itemstack;
		}
		if (movingobjectposition.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
			BlockPos pos = movingobjectposition.getBlockPos();
			if (world.getBlockState(pos).getBlock().getMaterial() == Material.water) {
				if (itemstack.getItemDamage() < 5) { 
					return new ItemStack(this, 1, 5); 
				}
			} else if (canDrink(player)) {
				player.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
			}
		}
		return itemstack;
	}

	public boolean canDrink(EntityPlayer player) {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? PlayerContainer.getPlayer(player).getStats().thirstLevel < 20 : ClientStats.getInstance().level < 20;
	}
}