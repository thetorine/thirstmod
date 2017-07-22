package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.common.blocks.BlockRainCollector;
import com.thetorine.thirstmod.common.items.Drink;
import com.thetorine.thirstmod.common.items.ItemDrink;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CommonProxy {

    public HashMap<UUID, ThirstStats> loadedPlayers = new HashMap<>();

    public static BlockRainCollector RAIN_COLLECTOR = new BlockRainCollector();

    public static ItemDrink DRINKS = new ItemDrink("drink_item");

    public void preInit() {
        Drink.registerDrink(new Drink("Fresh Water", 7, 2.0f, 0x11DEF5));
        Drink.registerDrink(new Drink("Milk Bottle", 6, 1.8f, 0xF0E8DF));
        Drink.registerDrink(new Drink("Chocolate Milk", 7, 2.0f, 0x6E440D));
        // TODO buckets

        Recipes.addRainCollectorRecipe(Items.GLASS_BOTTLE, new ItemStack(DRINKS, 1, 0), 80);
        Recipes.addRainCollectorRecipe(Items.BUCKET, new ItemStack(Items.WATER_BUCKET, 1), 160);
    }

    public void init() {}

    public ThirstStats getStatsByUUID(UUID uuid) {
        ThirstStats stats = loadedPlayers.get(uuid);
        if (stats == null) {
            System.out.println("Error: Attempted to access non-existent player with UUID: " + uuid);
            return null;
        }
        return stats;
    }

    public void registerPlayer(EntityPlayer player, ThirstStats stats) {
        UUID uuid = player.getUniqueID();
        if (loadedPlayers.containsKey(uuid)) {
            // Player already loaded from previous login session where the
            // server was not closed since the players last login.
            return;
        }
        loadedPlayers.put(uuid, stats);
    }
}
