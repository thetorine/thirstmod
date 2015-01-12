/**
 * ThirstMods Drink class. Similar to ItemFood.
 */
package com.thetorine.thirstmod.common.content;

import java.util.List;
import java.util.Random;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.utils.DrinkLists;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContentDrink extends Item {
	private int thirstReplenish;
	private float saturationReplenish;
	private boolean alwaysDrinkable;
	private int potionId;
	private int potionDuration;
	private int potionAmplifier;
	private float potionEffectProbability;
	private Item returnItem = Items.glass_bottle;
	public String username;
	public boolean curesPotion;
	private float poisonChance;
	private int colour;

	private int foodHeal;
	private float satHeal;

	private IIcon drinkable;
	private IIcon overlay;

	public ContentDrink(int replenish, float saturation, boolean alwaysDrinkable, int colour) {
		if (alwaysDrinkable == true) {
			this.alwaysDrinkable = true;
		}
		setCreativeTab(ThirstMod.drinkTab);
		this.thirstReplenish = replenish;
		this.saturationReplenish = saturation;

		DrinkLists.addDrink(new ItemStack(this), replenish);
		this.colour = colour;
	}

	@Override
	public ItemStack onEaten(ItemStack itemstack, World world, EntityPlayer entityplayer) {
		if (!world.isRemote) {
			itemstack.stackSize--;
			PlayerHandler.getPlayer(entityplayer.getDisplayName()).getStats().addStats(thirstReplenish, saturationReplenish);
			if ((poisonChance > 0) && ThirstMod.CONFIG.POISON_ON) {
				Random rand = new Random();
				if (rand.nextFloat() < poisonChance) {
					PlayerHandler.getPlayer(entityplayer.getDisplayName()).getStats().getPoison().startPoison();
				}
			}

			if (curesPotion) {
				entityplayer.curePotionEffects(new ItemStack(Items.milk_bucket));
			}

			if ((foodHeal > 0) && (satHeal > 0)) {
				entityplayer.getFoodStats().addStats(foodHeal, satHeal);
			}

			if ((potionId > 0) && (world.rand.nextFloat() < potionEffectProbability)) {
				entityplayer.addPotionEffect(new PotionEffect(potionId, potionDuration * 20, potionAmplifier));
			}
			entityplayer.inventory.addItemStackToInventory(new ItemStack(returnItem));
		}
		return itemstack;
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
	public ContentDrink setPotionEffect(int i, int j, int k, float f) {
		potionId = i;
		potionDuration = j;
		potionAmplifier = k;
		potionEffectProbability = f;
		return this;
	}

	@Override
	public boolean hasEffect(ItemStack itemstack) {
		return false;
	}

	/**
	 * Sets a poisoning chance when drunk.
	 * 
	 * @param chance
	 *            Chance. 0.6f = 60% approximately.
	 * @return
	 */
	public ContentDrink setPoisoningChance(float chance) {
		poisonChance = chance;
		return this;
	}

	/**
	 * Makes the item shiny like Golden Apple.
	 * 
	 * @return this
	 */
	public Item setHasEffect() {
		return this;
	}

	/**
	 * Makes the item shiny like Golden Apple. This one is for ContentLoader
	 * cause its a boolean.
	 * 
	 * @return this
	 */

	public ContentDrink setEffect(boolean b) {
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
	public ContentDrink healFood(int level, float saturation) {
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
		returnItem = item;
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
		return par2 > 0 ? 16777215 : colour;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister par1IconRegister) {
		this.drinkable = par1IconRegister.registerIcon("potion" + "_" + "bottle_drinkable");
		this.overlay = par1IconRegister.registerIcon("potion" + "_" + "overlay");
	}
}