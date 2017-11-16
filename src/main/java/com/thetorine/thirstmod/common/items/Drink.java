package com.thetorine.thirstmod.common.items;

import net.minecraft.item.Item;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Drink implements Serializable {

    public static List<Drink> ALL_DRINKS = new ArrayList<>();

    public String drinkName;
    public int thirstReplenish;
    public float saturationReplenish;
    public int drinkColor;

    public boolean alwaysDrinkable;
    public boolean shiny;
    public String recipeItem;
    public int manufactureTime;

    public Drink(String name, int thirst, float sat, int color) {
        this.drinkName = name;
        this.thirstReplenish = thirst;
        this.saturationReplenish = sat;
        this.drinkColor = color;
    }

    public Item getItem() {
        return Item.getByNameOrId(recipeItem);
    }

    public static void registerDrink(Drink drink) {
        ALL_DRINKS.add(drink);
    }

    public static Drink getDrinkByName(String name) {
        for (int i = 0; i < ALL_DRINKS.size(); i++) {
            Drink d = ALL_DRINKS.get(i);
            if (d.drinkName.equals(name)) {
                return d;
            }
        }
        return null;
    }

    public static Drink getDrinkByIndex(int i) {
        return ALL_DRINKS.get(i);
    }
}
