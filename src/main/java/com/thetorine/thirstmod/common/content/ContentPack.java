package com.thetorine.thirstmod.common.content;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.thetorine.thirstmod.common.logic.Recipes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class ContentPack {

    public static String DIRECTORY = "thirstmod/packs/";

    public static void load() {
        File packsDir = getPacksDirectory();
        Gson gsonInstance = new Gson();
        loadDrinks(gsonInstance, packsDir);
    }

    private static void loadDrinks(Gson gsonInstance, File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory()) loadDrinks(gsonInstance, f);
            else {
                if (f.getName().endsWith(".json")) {
                    try {
                        JsonReader reader = new JsonReader(new FileReader(f));
                        Drink drink = gsonInstance.fromJson(reader, Drink.class);
                        Drink.registerDrink(drink);
                        Recipes.addDrinksBrewerRecipe(drink.getItem(), drink, drink.manufactureTime);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private static File getPacksDirectory() {
        File s = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? Minecraft.getMinecraft().mcDataDir : FMLCommonHandler.instance().getMinecraftServerInstance().getDataDirectory();
        File dir = new File(s, DIRECTORY);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        return dir;
    }
}
