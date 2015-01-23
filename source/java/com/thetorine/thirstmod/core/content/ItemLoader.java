package com.thetorine.thirstmod.core.content;

import com.thetorine.thirstmod.core.content.blocks.RCRecipes;
import com.thetorine.thirstmod.core.main.ThirstMod;
import com.thetorine.thirstmod.core.utils.Constants;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemLoader {
	public static Item goldCoin = new Item().setCreativeTab(ThirstMod.thirstCreativeTab).setUnlocalizedName("gold_coin").setTextureName("thirstmod:coin_gold");
	public static Item filter = new ItemFilter(0).setCreativeTab(ThirstMod.thirstCreativeTab).setUnlocalizedName("filter").setTextureName("thirstmod:filter");
	public static Item dirtyFilter = new ItemFilter(1).setCreativeTab(ThirstMod.thirstCreativeTab).setUnlocalizedName("dirty_filter").setTextureName("thirstmod:dirty_filter");
	public static Item charcoalFilter = new ItemFilter(2).setCreativeTab(ThirstMod.thirstCreativeTab).setUnlocalizedName("charcoal_filter").setTextureName("thirstmod:coal_filter");
	
	public static Item cup = new ItemInternalDrink("thirstmod:cup").setUnlocalizedName("cup");
	public static Item waterCup = new ItemInternalDrink(3, 1.2f, 0.3f, "thirstmod:water", 0).setUnlocalizedName("water_cup");
	public static Item filteredWaterCup = new ItemInternalDrink(5, 1.6f, 0f, "thirstmod:filtered_water", 0).setUnlocalizedName("filtered_water_cup");
	
	public static Item freshWater = new ItemDrink(7, 2f, 0x11DEF5, Constants.DRINKS_STACKSIZE, false, false, "fresh_water");
	public static Item milk = new ItemDrink(6, 1.8f, 0xF0E8DF, Constants.DRINKS_STACKSIZE, false, false, "milk");
	public static Item chocolateMilk = new ItemDrink(7, 2f, 0x6E440D, Constants.DRINKS_STACKSIZE, false, false, "chocolate_milk");
	public static Item freshWaterBucket = new ItemInternalDrink(10, 4f, 0f, "thirstmod:clean_bucket", 1).setReturnItem(Items.bucket).setUnlocalizedName("clean_bucket");
	public static Item canteen = new ItemCanteen().setCreativeTab(ThirstMod.thirstCreativeTab).setUnlocalizedName("canteen");
	
	public ItemLoader() {
		registerItem(goldCoin);
		registerItem(filter);
		registerItem(dirtyFilter);
		registerItem(charcoalFilter);
		registerItem(cup);
		registerItem(waterCup);
		registerItem(filteredWaterCup);
		registerItem(freshWater);
		registerItem(milk);
		registerItem(chocolateMilk);
		registerItem(freshWaterBucket);
		registerItem(canteen);
		
		GameRegistry.addSmelting(Items.potionitem, new ItemStack(freshWater, 1), 0.3f);
		GameRegistry.addSmelting(Items.water_bucket, new ItemStack(freshWaterBucket, 1), 0.4f);
		
		GameRegistry.addShapelessRecipe(new ItemStack(goldCoin, 5), new Object[] { Items.gold_nugget });
		GameRegistry.addShapelessRecipe(new ItemStack(filter), new Object[] { Items.string, dirtyFilter });
		GameRegistry.addShapelessRecipe(new ItemStack(chocolateMilk, 1), new Object[] { milk, new ItemStack(Items.dye, 1, 3), Items.sugar, });
		GameRegistry.addRecipe(new ItemStack(filter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', Items.string });
		GameRegistry.addRecipe(new ItemStack(charcoalFilter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', new ItemStack(Items.coal, 0, 1) });
		GameRegistry.addRecipe(new ItemStack(cup), new Object[] { "* *", "* *", " * ", Character.valueOf('*'), Blocks.planks, });
		GameRegistry.addRecipe(new ItemStack(canteen, 1, 0), new Object[] { "* *", " * ", '*', Items.leather });
		GameRegistry.addRecipe(new ItemStack(milk, 3), new Object[] { " * ", "^^^", '*', Items.milk_bucket, '^', Items.glass_bottle });
		
		for (int i = 0; i < 6; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(filteredWaterCup), new Object[] { new ItemStack(charcoalFilter, 0, i), waterCup, });
			GameRegistry.addShapelessRecipe(new ItemStack(filteredWaterCup), new Object[] { new ItemStack(filter, 0, i), waterCup, });
			GameRegistry.addShapelessRecipe(new ItemStack(freshWater, 1), new Object[] { new ItemStack(charcoalFilter, 0, i), new ItemStack(Items.potionitem, 0, 0) });
			GameRegistry.addShapelessRecipe(new ItemStack(freshWater, 1), new Object[] { new ItemStack(filter, 0, i), new ItemStack(Items.potionitem, 0, 0) });
		}
		
		RCRecipes.addRecipe(Items.glass_bottle, 200, new ItemStack(freshWater));
		RCRecipes.addRecipe(cup, 150, new ItemStack(filteredWaterCup));
		RCRecipes.addRecipe(Items.bucket, 600, new ItemStack(freshWaterBucket));
		RCRecipes.addRecipe(canteen, 175, new ItemStack(canteen, 1, 10));
	}
	
	private void registerItem(Item i) {
		String name = i.getUnlocalizedName().replace("item.", "");
		GameRegistry.registerItem(i, name);
	}
}
