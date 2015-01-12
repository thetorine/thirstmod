/**
 * ThirstMods Drink class. Similar to ItemFood.
 */
package com.thetorine.thirstmod.common.items;

import java.util.List;
import java.util.Random;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.utils.DrinkLists;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class Drink extends Item {
	private int thirstReplenish;
	private float saturationReplenish;
	private boolean alwaysDrinkable;
	private int potionId;
	private int potionDuration;
	private int potionAmplifier;
	private float potionEffectProbability;
	private boolean hasEffect = false;
	private Item returnItem = Items.glass_bottle;
	public String username;
	public boolean curesPotion;
	private float poisonChance;

	private int foodHeal;
	private float satHeal;

	public Drink(int replenish, float saturation, boolean alwaysDrinkable) {
		if (alwaysDrinkable == true) {
			this.alwaysDrinkable = true;
		}
		setCreativeTab(ThirstMod.drinkTab);
		this.thirstReplenish = replenish;
		this.saturationReplenish = saturation;

		DrinkLists.addDrink(new ItemStack(this), replenish);
	}

	@Override
	public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
		if (!world.isRemote) {
			PlayerHandler.getPlayer(player.getDisplayName()).getStats().addStats(thirstReplenish, saturationReplenish);
			if ((poisonChance > 0) && ThirstMod.CONFIG.POISON_ON) {
				Random rand = new Random();
				if (rand.nextFloat() < poisonChance) {
					PlayerHandler.getPlayer(player.getDisplayName()).getStats().getPoison().startPoison();
				}
			}
			if (curesPotion) {
				player.curePotionEffects(new ItemStack(Items.milk_bucket));
			}
			if ((foodHeal > 0) && (satHeal > 0)) {
				player.getFoodStats().addStats(foodHeal, satHeal);
			}
			if ((potionId > 0) && (world.rand.nextFloat() < potionEffectProbability)) {
				player.addPotionEffect(new PotionEffect(potionId, potionDuration * 20, potionAmplifier));
			}
			
			 --stack.stackSize;
			 if(stack.stackSize <= 0) {
				 return new ItemStack(returnItem);
			 }
			 if(!player.inventory.addItemStackToInventory(new ItemStack(returnItem))) {
				 world.spawnEntityInWorld(new EntityItem(world, player.posX, player.posY, player.posZ, new ItemStack(returnItem)));
	         }
		}
		return stack;
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
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		username = entityplayer.getDisplayName();
		if ((canDrink() == true) || (alwaysDrinkable == true) || (entityplayer.capabilities.isCreativeMode == true)) {
			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
		super.addInformation(stack, player, list, flag);
		String s = Integer.toString(thirstReplenish);
		float f = Float.parseFloat(s) / 2;
		String s2 = Float.toString(f);

		list.add("Heals " + (s2.endsWith(".0") ? s2.replace(".0", "") : s2) + " Droplets");
	}

	/**
	 * Sets a potion effect when the drink is drunk.
	 * 
	 * @param i
	 *            Potion ID
	 * @param j
	 *            Duration
	 * @param k
	 *            Amplifier
	 * @param f
	 *            Probability
	 * @return
	 */
	public Drink setPotionEffect(int i, int j, int k, float f) {
		potionId = i;
		potionDuration = j;
		potionAmplifier = k;
		potionEffectProbability = f;
		return this;
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return hasEffect;
	}

	/**
	 * Sets a poisoning chance when drunk.
	 * 
	 * @param chance
	 *            Chance. 0.6f = 60% approximately.
	 * @return
	 */
	public Drink setPoisoningChance(float chance) {
		poisonChance = chance;
		return this;
	}

	/**
	 * Makes the item shiny like Golden Apple.
	 * 
	 * @return this
	 */
	public Item setHasEffect() {
		hasEffect = true;
		return this;
	}

	/**
	 * Makes the item shiny like Golden Apple. This one is for ContentLoader
	 * cause its a boolean.
	 * 
	 * @return this
	 */

	public Drink setEffect(boolean b) {
		hasEffect = b;
		return this;
	}

	/**
	 * Allows the drink to heal the food bar.
	 * 
	 * @param level
	 *            amount level.
	 * @param saturation
	 *            amount satuation.
	 * @return
	 */
	public Drink healFood(int level, float saturation) {
		foodHeal = level;
		satHeal = saturation;
		return this;
	}

	/**
	 * Can the person drink.
	 * 
	 * @return
	 */
	public boolean canDrink() {
		return FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER ? PlayerHandler.getPlayer(username).getStats().level < 20 : StatsHolder.getInstance().level < 20;
	}

	/**
	 * Sets the item that is returned.
	 * 
	 * @param item
	 *            Item that is returned after the drink is drunk.
	 * @return this.
	 */
	public Item setReturn(Item item) {
		this.returnItem = item;
		return this;
	}

	public Item setCuresPotions(boolean b) {
		curesPotion = b;
		return this;
	}

	/**
	 * Gets the return item for this drink.
	 * 
	 * @return the item that will be given after this drink is drunk.
	 */
	public Item getReturn() {
		return returnItem;
	}
}