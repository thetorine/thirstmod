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
	public static Item gold_coin = new Item().setCreativeTab(ThirstMod.thirst).setUnlocalizedName("gold_coin").setTextureName("thirstmod:coin_gold");
	public static Item filter = new ItemFilter(0).setCreativeTab(ThirstMod.thirst).setUnlocalizedName("filter").setTextureName("thirstmod:filter");
	public static Item dirty_filter = new ItemFilter(1).setCreativeTab(ThirstMod.thirst).setUnlocalizedName("dirty_filter").setTextureName("thirstmod:dirty_filter");
	public static Item charcoal_filter = new ItemFilter(2).setCreativeTab(ThirstMod.thirst).setUnlocalizedName("charcoal_filter").setTextureName("thirstmod:coal_filter");
	
	public static Item cup = new ItemInternalDrink("thirstmod:cup").setUnlocalizedName("cup");
	public static Item water_cup = new ItemInternalDrink(3, 1.2f, 0.3f, "thirstmod:water", 0).setUnlocalizedName("water_cup");
	public static Item filtered_water_cup = new ItemInternalDrink(5, 1.6f, 0f, "thirstmod:filtered_water", 0).setUnlocalizedName("filtered_water_cup");
	
	public static Item fresh_water = new ItemDrink(7, 2f, 0x11DEF5, Constants.DRINKS_STACKSIZE, false, false, "fresh_water");
	public static Item milk = new ItemDrink(6, 1.8f, 0xF0E8DF, Constants.DRINKS_STACKSIZE, false, false, "milk");
	public static Item chocolate_milk = new ItemDrink(7, 2f, 0x6E440D, Constants.DRINKS_STACKSIZE, false, false, "chocolate_milk");
	public static Item fresh_water_bucket = new ItemInternalDrink(10, 4f, 0f, "thirstmod:clean_bucket", 1).setReturnItem(Items.bucket).setUnlocalizedName("clean_bucket");
	public static Item canteen = new ItemCanteen().setCreativeTab(ThirstMod.thirst);
	
	public ItemLoader() {
		GameRegistry.registerItem(gold_coin, gold_coin.getUnlocalizedName());
		GameRegistry.registerItem(filter, filter.getUnlocalizedName());
		GameRegistry.registerItem(dirty_filter, dirty_filter.getUnlocalizedName());
		GameRegistry.registerItem(charcoal_filter, charcoal_filter.getUnlocalizedName());
		GameRegistry.registerItem(cup, cup.getUnlocalizedName());
		GameRegistry.registerItem(water_cup, water_cup.getUnlocalizedName());
		GameRegistry.registerItem(filtered_water_cup, filtered_water_cup.getUnlocalizedName());
		GameRegistry.registerItem(fresh_water, fresh_water.getUnlocalizedName());
		GameRegistry.registerItem(milk, milk.getUnlocalizedName());
		GameRegistry.registerItem(chocolate_milk, chocolate_milk.getUnlocalizedName());
		GameRegistry.registerItem(fresh_water_bucket, fresh_water_bucket.getUnlocalizedName());
		GameRegistry.registerItem(canteen, canteen.getUnlocalizedName());
		
		GameRegistry.addSmelting(Items.potionitem, new ItemStack(fresh_water, 1), 0.3f);
		GameRegistry.addSmelting(Items.water_bucket, new ItemStack(fresh_water_bucket, 1), 0.4f);
		
		GameRegistry.addShapelessRecipe(new ItemStack(gold_coin, 5), new Object[] { Items.gold_nugget });
		GameRegistry.addShapelessRecipe(new ItemStack(filter), new Object[] { Items.string, dirty_filter });
		GameRegistry.addShapelessRecipe(new ItemStack(chocolate_milk, 1), new Object[] { milk, new ItemStack(Items.dye, 1, 3), Items.sugar, });
		GameRegistry.addRecipe(new ItemStack(filter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', Items.string });
		GameRegistry.addRecipe(new ItemStack(charcoal_filter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', new ItemStack(Items.coal, 0, 1) });
		GameRegistry.addRecipe(new ItemStack(cup), new Object[] { "* *", "* *", " * ", Character.valueOf('*'), Blocks.planks, });
		GameRegistry.addRecipe(new ItemStack(canteen, 1, 0), new Object[] { "* *", " * ", '*', Items.leather });
		GameRegistry.addRecipe(new ItemStack(milk, 3), new Object[] { " * ", "^^^", '*', Items.milk_bucket, '^', Items.glass_bottle });
		
		for (int i = 0; i < 6; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(filtered_water_cup), new Object[] { new ItemStack(charcoal_filter, 0, i), water_cup, });
			GameRegistry.addShapelessRecipe(new ItemStack(filtered_water_cup), new Object[] { new ItemStack(filter, 0, i), water_cup, });
			GameRegistry.addShapelessRecipe(new ItemStack(fresh_water, 1), new Object[] { new ItemStack(charcoal_filter, 0, i), new ItemStack(Items.potionitem, 0, 0) });
			GameRegistry.addShapelessRecipe(new ItemStack(fresh_water, 1), new Object[] { new ItemStack(filter, 0, i), new ItemStack(Items.potionitem, 0, 0) });
		}
		
		RCRecipes.addRecipe(Items.glass_bottle, 200, new ItemStack(fresh_water));
		RCRecipes.addRecipe(cup, 150, new ItemStack(filtered_water_cup));
		RCRecipes.addRecipe(Items.bucket, 600, new ItemStack(fresh_water_bucket));
		RCRecipes.addRecipe(canteen, 175, new ItemStack(canteen, 1, 10));
	}
}
