package com.thetorine.thirstmod.common.logic;

import com.thetorine.thirstmod.common.blocks.BlockDrinksBrewer;
import com.thetorine.thirstmod.common.blocks.BlockDrinksStore;
import com.thetorine.thirstmod.common.blocks.BlockRainCollector;
import com.thetorine.thirstmod.common.content.Drink;
import com.thetorine.thirstmod.common.items.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class CommonProxy {

    public HashMap<UUID, ThirstStats> loadedPlayers = new HashMap<>();

    public static final BlockRainCollector RAIN_COLLECTOR = new BlockRainCollector();
    public static final BlockDrinksBrewer  DRINKS_BREWER  = new BlockDrinksBrewer();
    public static final BlockDrinksStore   DRINKS_STORE   = new BlockDrinksStore();

    public static final ItemDrink   DRINKS          = new ItemDrink("drink_item");
    public static final ItemCanteen CANTEEN         = new ItemCanteen("canteen");
    public static final ItemCup     CUP             = new ItemCup("cup");
    public static final Item        FILTER          = new ItemFilter("filter", 0);
    public static final Item        CHARCOAL_FILTER = new ItemFilter("charcoal_filter", 1);
    public static final Item        DIRTY_FILTER    = new ItemFilter("dirty_filter", 2);

    public void preInit() {
        Drink.registerDrink(new Drink("Fresh Water", 7, 2.0f, 0x11DEF5));
        Drink.registerDrink(new Drink("Milk", 5, 1.8f, 0xF0E8DF));
        Drink.registerDrink(new Drink("Chocolate Milk", 7, 2.0f, 0x6E440D));

        Recipes.addRainCollectorRecipe(Items.GLASS_BOTTLE, new ItemStack(DRINKS, 1, 0), 120);
        Recipes.addRainCollectorRecipe(CANTEEN, new ItemStack(CANTEEN, 1, CANTEEN.getMetadataForDrink(Drink.getDrinkByName("Fresh Water"))), 200);
        Recipes.addRainCollectorRecipe(CUP, new ItemStack(CUP, 1, 1), 120);
        Recipes.addRainCollectorRecipe(Items.BUCKET, new ItemStack(Items.WATER_BUCKET, 1), 160);
    }

    public void init() {}

    public ThirstStats getStatsByUUID(UUID uuid) {
        ThirstStats stats = loadedPlayers.get(uuid);
        if (stats == null) {
            System.out.println("[Thirst Mod] Error: Attempted to access non-existent player with UUID: " + uuid);
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
