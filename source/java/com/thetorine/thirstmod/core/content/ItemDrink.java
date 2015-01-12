package com.thetorine.thirstmod.core.content;

import java.util.List;
import java.util.Random;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.player.PlayerContainer;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemDrink extends Item {
	public int itemColour;
	public int itemStackSize;
	public boolean specialEffect;
	public boolean alwaysDrinkable;
	public Item returnItem = Items.glass_bottle;
	
	private IIcon drinkable;
	private IIcon overlay;
	
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
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if(!world.isRemote) {
			stack.stackSize--;
			
			PlayerContainer playerCon = PlayerContainer.getPlayer(player.getDisplayName());
			playerCon.addStats(thirstHeal, saturationHeal);
			if ((poisonChance > 0) && ThirstMod.config.POISON_ON) {
				Random rand = new Random();
				if (rand.nextFloat() < poisonChance) {
					playerCon.getStats().poisonLogic.startPoison();
				}
			}
			if (curesPotion) {
				player.curePotionEffects(new ItemStack(Items.milk_bucket));
			}
			if ((hungerHeal > 0) && (hungerSatHeal > 0)) {
				player.getFoodStats().addStats(hungerHeal, hungerSatHeal);
			}
			if ((potionID > 0) && (world.rand.nextFloat() < 1)) {
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
		String s = Integer.toString(thirstHeal);
		float f = Float.parseFloat(s) / 2;
		String s2 = Float.toString(f);

		list.add("Heals " + (s2.endsWith(".0") ? s2.replace(".0", "") : s2) + " Droplets");
	}
	
	@Override
	public boolean hasEffect(ItemStack par1ItemStack, int pass) {
		if(pass == 0) {
			return specialEffect;
		}
		return false;
	}
	
	@Override
	public EnumAction getItemUseAction(ItemStack itemstack) {
		return EnumAction.drink;
	}
	
	@Override
	public IIcon getIconFromDamageForRenderPass(int par1, int par2) {
		return par2 == 0 ? this.overlay : this.drinkable;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean requiresMultipleRenderPasses() {
		return true;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getColorFromItemStack(ItemStack par1ItemStack, int par2) {
		return par2 > 0 ? 16777215 : itemColour;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		this.drinkable = par1IconRegister.registerIcon("potion_bottle_drinkable");
		this.overlay = par1IconRegister.registerIcon("potion_overlay");
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
	
	public boolean canDrink(EntityPlayer player) {
		switch(FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT: return ClientStats.getInstance().level < 20;
			case SERVER: return PlayerContainer.getPlayer(player.getDisplayName()).stats.thirstLevel < 20;
		}
		return false;
	}
}