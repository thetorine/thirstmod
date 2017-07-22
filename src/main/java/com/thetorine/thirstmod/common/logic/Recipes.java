package com.thetorine.thirstmod.common.logic;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class Recipes {

    private static HashMap<Item, Output> rainCollectorRecipes = new HashMap<>();

    public static void addRainCollectorRecipe(Item input, ItemStack output, int time) {
        rainCollectorRecipes.put(input, new Output(output, time));
    }

    public static Output getRainCollectorRecipe(Item input) {
        if (rainCollectorRecipes.containsKey(input)) {
            return rainCollectorRecipes.get(input);
        }
        return null;
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
