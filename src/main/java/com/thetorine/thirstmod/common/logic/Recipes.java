package com.thetorine.thirstmod.common.logic;

import net.minecraft.item.Item;
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

    public static void addDrinksBrewerRecipe(Item input, ItemStack output, int time) {
        drinksBrewerRecipes.put(input, new Output(output, time));
    }

    public static Output getDrinksBrewerRecipe(Item input) {
        return drinksBrewerRecipes.get(input);
    }

    public static class Output {
        public ItemStack outputItem;
        public int manufactureTime;

        public Output(ItemStack itemStack, int time) {
            outputItem = itemStack;
            manufactureTime = time;
        }
    }
}
