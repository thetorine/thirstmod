package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.items.Drink;
import com.thetorine.thirstmod.common.items.ItemCanteen;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;

public class Recipes {

    private static HashMap<Item, Output> rainCollectorRecipes = new HashMap<>();
    private static HashMap<Item, Output> drinksBrewerRecipes = new HashMap<>();

    public enum DrinkContainer {GLASS_BOTTLE, CUP, CANTEEN, UNKNOWN}

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

    public static ItemStack getItemStackFromOutput(Output output, DrinkContainer container) {
        int drinkIndex = Drink.ALL_DRINKS.indexOf(output.drinkType);
        switch (container) {
            case GLASS_BOTTLE: return new ItemStack(ThirstMod.getProxy().DRINKS, 1, drinkIndex);
            case CUP:          return new ItemStack(ThirstMod.getProxy().CUP, 1, 1 + drinkIndex);
            case CANTEEN:      return new ItemStack(ThirstMod.getProxy().CANTEEN, 1, ItemCanteen.getIndexOfDrink(output.drinkType));
            default:           return ItemStack.EMPTY;
        }
    }

    public static DrinkContainer getContainerFromItemStack(ItemStack item) {
        if (item.getItem().equals(Items.GLASS_BOTTLE)) return DrinkContainer.GLASS_BOTTLE;
        else if (item.getItem().equals(ThirstMod.getProxy().CUP) && item.getMetadata() == 0) return DrinkContainer.CUP;
        else if (item.getItem().equals(ThirstMod.getProxy().CANTEEN) && item.getMetadata() == 0) return DrinkContainer.CANTEEN;
        return DrinkContainer.UNKNOWN;
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
