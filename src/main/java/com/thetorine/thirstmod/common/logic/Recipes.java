package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.items.Drink;
import com.thetorine.thirstmod.common.items.ItemContainer;
import com.thetorine.thirstmod.common.items.ItemDrink;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGlassBottle;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class Recipes {

    private static HashMap<Item, Output> rainCollectorRecipes = new HashMap<>();
    private static HashMap<Item, Output> drinksBrewerRecipes = new HashMap<>();

    public static void addRainCollectorRecipe(Item input, ItemStack output, int time) {
        rainCollectorRecipes.put(input, new Output(output, time));
    }

    public static Output getRainCollectorRecipe(Item input) {
        return rainCollectorRecipes.get(input);
    }

    public static void addDrinksBrewerRecipe(Item input, Drink drink, int manufactureTime) {
        drinksBrewerRecipes.put(input, new Output(drink, manufactureTime));
    }

    public static Output getDrinksBrewerRecipe(Item input) {
        return drinksBrewerRecipes.get(input);
    }

    public static boolean isContainer(ItemStack stack) {
        if (stack.getItem() instanceof ItemGlassBottle) return stack.getMetadata() == 0;
        return stack.getItem() instanceof ItemContainer && stack.getMetadata() == 0;
    }

    public static ItemStack getItemStackFromOutput(Output output, Item item) {
        if (item instanceof ItemGlassBottle) {
            return new ItemStack(ThirstMod.getProxy().DRINKS, 1, ThirstMod.getProxy().DRINKS.getMetadataForDrink(output.drinkType));
        } else if (item instanceof ItemContainer) {
            return new ItemStack(item, 1, ((ItemContainer)item).getMetadataForDrink(output.drinkType));
        }
        return null;
    }

    public static class Output {
        public ItemStack outputItem;
        public Drink drinkType;
        public int manufactureTime;

        public Output(ItemStack itemStack, int time) {
            outputItem = itemStack;
            manufactureTime = time;
        }

        public Output(Drink drink, int time) {
            drinkType = drink;
            manufactureTime = time;
        }
    }
}
