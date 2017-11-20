package com.thetorine.thirstmod.common.content;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class ExternalDrink {

    public static String DIRECTORY = "thirstmod/";
    public static ArrayList<DrinkItem> EXTERNAL_DRINKS = new ArrayList<>();

    public static void load() {
        try {
            File f = new File(DIRECTORY + "external_drinks.json");
            if (!f.exists()) {
                f.mkdirs();
                try {
                    f.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Gson gson = new Gson();
            DrinkItem[] items = gson.fromJson(new FileReader(f), DrinkItem[].class);
            EXTERNAL_DRINKS.addAll(Arrays.asList(items));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
