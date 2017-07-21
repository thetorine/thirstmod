package com.thetorine.thirstmod;

/*
    Author: tarun1998 (http://www.minecraftforum.net/members/tarun1998)
    Date: 21/07/2017
    Main mod file for the Thirst Mod.
 */

import com.google.gson.Gson;
import com.thetorine.thirstmod.client.ClientProxy;
import com.thetorine.thirstmod.common.CommonProxy;
import com.thetorine.thirstmod.common.EventHook;
import com.thetorine.thirstmod.common.Items;
import com.thetorine.thirstmod.common.drinks.Drink;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.awt.*;

@Mod(modid = Constants.MOD_ID, version = Constants.MOD_VERSION)
public class ThirstMod {
    @Mod.Instance(Constants.MOD_ID)
    private static ThirstMod instance;

    public static ThirstMod getInstance() {
        return instance;
    }

    @SidedProxy(clientSide = Constants.CLIENT_SIDE_PROXY, serverSide = Constants.COMMON_SIDE_PROXY, modId = Constants.MOD_ID)
    private static CommonProxy commonProxy;

    public static CommonProxy getProxy() {
        return commonProxy;
    }

    @SideOnly(Side.CLIENT)
    public static ClientProxy getClientProxy() {
        return (ClientProxy) commonProxy;
    }

    public static Gson gsonInstance = new Gson();

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(EventHook.getInstance());

        Drink.registerDrink(new Drink("Test 1", 1, 5, Color.BLUE.getRGB()));
        Drink.registerDrink(new Drink("Test 2", 2, 5, 0x000000));
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        Items.initialise();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        getProxy().loadedPlayers.clear();
    }
}
