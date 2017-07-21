package com.thetorine.thirstmod.common;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Contains all items in the mod.
 */

import com.thetorine.thirstmod.common.drinks.Drink;
import com.thetorine.thirstmod.common.drinks.ItemDrink;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class Items {

    public static ItemDrink DRINKS = new ItemDrink("drink_item");

    public static void initialise() {
        if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
            for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
                Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(DRINKS, i, new ModelResourceLocation("thirstmod:drink_item", "inventory"));
            }
            Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemDrink.ColorHandler(), DRINKS);
        }
    }

    public static void registerDrinkItems(RegistryEvent.Register<Item> event) {
        event.getRegistry().register(DRINKS);
    }

}
