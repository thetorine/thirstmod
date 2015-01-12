package com.thetorine.thirstmod.common.blocks;

import java.util.HashMap;
import java.util.Map;
import com.thetorine.thirstmod.common.items.DrinkLoader;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class RCRecipes {
	private static final RCRecipes solidifyingBase = new RCRecipes();
	private Map<String, ItemStack> solidifyingList;
	private Map<String, Integer> fillTime;

	public static final RCRecipes fill() {
		return solidifyingBase;
	}

	public RCRecipes() {
		solidifyingList = new HashMap<String, ItemStack>();
		fillTime = new HashMap<String, Integer>();
		addRecipe(Items.glass_bottle.getUnlocalizedName(), 200, new ItemStack(DrinkLoader.freshWater));
		addRecipe(DrinkLoader.woodGlass.getUnlocalizedName(), 150, new ItemStack(DrinkLoader.woodFWater));
		addRecipe(ThirstMod.canteen.getUnlocalizedName(), 175, new ItemStack(ThirstMod.canteen, 1, 10));
		if (ThirstMod.CONFIG.WANT_FILTERED_BUCKET) {
			addRecipe(Items.bucket.getUnlocalizedName(), 600, new ItemStack(DrinkLoader.fBucket));
		}
	}

	public void addRecipe(String s, int time, ItemStack itemstack) {
		solidifyingList.put(s, itemstack);
		fillTime.put(s, Integer.valueOf(time));
	}

	public ItemStack getSolidifyingResult(String s) {
		return solidifyingList.get(s);
	}

	public Map<String, ItemStack> getSolidifyingList() {
		return solidifyingList;
	}

	public int getFillTimeFor(String s) {
		if (fillTime.get(s) != null) {
			return fillTime.get(s);
		} else {
			return 200;
		}
	}
}
