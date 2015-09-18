package com.thetorine.thirstmod.core.content;

import java.util.List;
import java.util.Random;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.player.PlayerContainer;

public class ItemDrink extends Item {
	public int itemColour;
	public int itemStackSize;
	public boolean specialEffect;
	public boolean alwaysDrinkable;
	public Item returnItem = Items.glass_bottle;
	public ItemStack recipeItem;
	
	public int thirstHeal;
	public float saturationHeal;
	public float poisonChance;
	public boolean curesPotion;
	
	public int hungerHeal;
	public float hungerSatHeal;
	
	public int potionID;
	public int duration;
	
	public ItemDrink(int thirst, float saturation, int colour, int stacksize, boolean effect, boolean drinkable, String name) {
		this.thirstHeal = thirst;
		this.saturationHeal = saturation;
		this.itemColour = colour;
		this.specialEffect = effect; 
		this.alwaysDrinkable = drinkable;
		
		this.setUnlocalizedName(name);
		this.setCreativeTab(ThirstMod.thirst);
		this.setMaxStackSize(stacksize);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if(canDrink(player) || alwaysDrinkable || player.capabilities.isCreativeMode) {
			player.setItemInUse(stack, getMaxItemUseDuration(stack));
		}
		return stack;
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote) {
			stack.stackSize--;
			
			PlayerContainer playerCon = PlayerContainer.getPlayer(player);
			playerCon.addStats(thirstHeal, saturationHeal);
			if (poisonChance > 0 && ThirstMod.config.POISON_ON) {
				Random rand = new Random();
				if (rand.nextFloat() < poisonChance) {
					playerCon.getStats().poisonLogic.poisonPlayer();
				}
			}
			if (curesPotion) {
				player.curePotionEffects(new ItemStack(Items.milk_bucket));
			}
			if (hungerHeal > 0 && hungerSatHeal > 0) {
				player.getFoodStats().addStats(hungerHeal, hungerSatHeal);
			}
			if (potionID > 0) {
				player.addPotionEffect(new PotionEffect(potionID, duration * 20, 1));
			}
			player.inventory.addItemStackToInventory(new ItemStack(returnItem));
		}
		return stack;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		super.addInformation(stack, player, list, flag);
		float f = Float.parseFloat(Integer.toString(thirstHeal)) / 2;
		String s2 = Float.toString(f);

		list.add("Heals " + (s2.endsWith(".0") ? s2.replace(".0", "") : s2) + " Droplets");
		if(recipeItem != null) {
			list.add("Ingredient: " + recipeItem.getDisplayName());
		}
	}
	
	@Override
	public boolean hasEffect(ItemStack par1ItemStack) {
		return specialEffect;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.DRINK;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return par2 > 0 ? itemColour : 16777215;
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 32;
	}
	
	public ItemDrink setPotionEffect(int i, int j) {
		potionID = i;
		duration = j;
		return this;
	}
	
	public ItemDrink setPoisoningChance(float chance) {
		poisonChance = chance;
		return this;
	}
	
	public ItemDrink healFood(int level, float saturation) {
		hungerHeal = level;
		hungerSatHeal = saturation;
		return this;
	}
	
	public ItemDrink setReturn(Item item) {
		returnItem = item;
		return this;
	}
	
	public ItemDrink setCuresPotions(boolean b) {
		curesPotion = b;
		return this;
	}
	
	public ItemDrink setRecipeItem(Item i) {
		recipeItem = new ItemStack(i);
		return this;
	}
	
	public boolean canDrink(EntityPlayer player) {
		switch(FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT: return ClientStats.getInstance().level < 20;
			case SERVER: return PlayerContainer.getPlayer(player).stats.thirstLevel < 20;
			default: return false;
		}
	}
}