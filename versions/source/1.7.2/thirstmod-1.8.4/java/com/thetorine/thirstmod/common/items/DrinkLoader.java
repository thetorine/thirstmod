package com.thetorine.thirstmod.common.items;

import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.registry.GameRegistry;

public class DrinkLoader {
	public static Item freshWater;
	public static Item milk;
	public static Item woodGlass = new ItemThirst().setUnlocalizedName("wood_cup").setCreativeTab(ThirstMod.drinkTab).setTextureName("thirstmod:cup");
	public static Item woodWater = ((Drink) ((Drink) ((Drink) new Drink(3, 1.2f, false).setUnlocalizedName("wood_water").setTextureName("thirstmod:water"))
			.setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE)).setReturn(woodGlass)).setPoisoningChance(0.3f);
	public static Item woodFWater = ((Drink) ((Drink) new Drink(5, 1.6f, false).setUnlocalizedName("filtered_water").setTextureName("thirstmod:fWater"))
			.setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE)).setReturn(woodGlass);
	public static Item fBucket;

	public void loadDrinks() {
		GameRegistry.addRecipe(new ItemStack(woodGlass), new Object[] { "* *", "* *", " * ", Character.valueOf('*'), Blocks.planks, });

		freshWater = ((Drink) new Drink(7, 2f, false).setUnlocalizedName("fresh_water")).setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE).setTextureName("thirstmod:fresh_water");
		GameRegistry.addSmelting(Items.potionitem, new ItemStack(freshWater, 1), 0.3f);

		if (ThirstMod.CONFIG.WANT_MILK) {
			milk = ((Drink) new Drink(6, 1.8f, false).setUnlocalizedName("milk_bottle")).setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE).setTextureName("thirstmod:milkBottle");
			GameRegistry.addRecipe(new ItemStack(milk, 3), new Object[] { " * ", "^^^", '*', Items.milk_bucket, '^', Items.glass_bottle });
			GameRegistry.registerItem(milk, "milk_bottle");
		}
		if (ThirstMod.CONFIG.WANT_CHOC_MILK && ThirstMod.CONFIG.WANT_MILK) {
			Item cMilk = ((Drink) new Drink(5, 1.6f, false).setUnlocalizedName("chocolate_milk")).setTextureName("thirstmod:choc").setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE);
			GameRegistry.addShapelessRecipe(new ItemStack(cMilk, 1), new Object[] { milk, new ItemStack(Items.dye, 1, 3), Items.sugar, });
			GameRegistry.registerItem(cMilk, "chocolate_milk");
		}
		if (ThirstMod.CONFIG.WANT_FILTERED_BUCKET) {
			fBucket = (((Drink) ((Drink) new Drink(10, 4f, false).setUnlocalizedName("clean_bucket").setTextureName("thirstmod:clean_bucket").setMaxStackSize(ThirstMod.CONFIG.MAX_STACK_SIZE))
					.setReturn(Items.bucket)));
			GameRegistry.addSmelting(Items.water_bucket, new ItemStack(fBucket, 1), 0.4f);
			GameRegistry.registerItem(fBucket, "fresh_water_bucket");
		}

		for (int i = 0; i < 6; i++) {
			GameRegistry.addShapelessRecipe(new ItemStack(freshWater, 1), new Object[] { new ItemStack(ThirstMod.ccFilter, 0, i), new ItemStack(Items.potionitem, 0, 0) });

			GameRegistry.addShapelessRecipe(new ItemStack(woodFWater), new Object[] { new ItemStack(ThirstMod.ccFilter, 0, i), woodWater, });

			GameRegistry.addShapelessRecipe(new ItemStack(freshWater, 1), new Object[] { new ItemStack(ThirstMod.filter, 0, i), new ItemStack(Items.potionitem, 0, 0) });

			GameRegistry.addShapelessRecipe(new ItemStack(woodFWater), new Object[] { new ItemStack(ThirstMod.filter, 0, i), woodWater, });

			GameRegistry.addShapelessRecipe(new ItemStack(ThirstMod.canteen, 1, 10), new Object[] { new ItemStack(ThirstMod.filter, 0, i), new ItemStack(ThirstMod.canteen, 1, 5), });
		}

		GameRegistry.registerItem(freshWater, "fresh_water");
		GameRegistry.registerItem(woodGlass, "wood_glass");
		GameRegistry.registerItem(woodWater, "wood_water");
		GameRegistry.registerItem(woodFWater, "fresh_cup_water");
		GameRegistry.registerItem(ThirstMod.canteen, "canteen");
	}
}
