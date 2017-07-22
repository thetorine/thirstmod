package com.thetorine.thirstmod;

import com.google.gson.Gson;
import com.thetorine.thirstmod.client.logic.ClientProxy;
import com.thetorine.thirstmod.common.logic.CommonProxy;
import com.thetorine.thirstmod.common.logic.EventHook;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
        getProxy().preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        getProxy().init();
    }

    @Mod.EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        getProxy().loadedPlayers.clear();
    }
}
