package com.thetorine.thirstmod.core.main;

import java.io.File;

import com.thetorine.thirstmod.core.content.BlockLoader;
import com.thetorine.thirstmod.core.content.ItemLoader;
import com.thetorine.thirstmod.core.content.packs.ContentLoader;
import com.thetorine.thirstmod.core.content.packs.DrinkRegistry;
import com.thetorine.thirstmod.core.network.*;
import com.thetorine.thirstmod.core.player.PlayerContainer;
import com.thetorine.thirstmod.core.utils.Config;
import com.thetorine.thirstmod.core.utils.Constants;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.network.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.common.*;

@Mod(modid = Constants.MODID, version = Constants.VERSION, name = Constants.NAME)
public class ThirstMod {
	@Instance(Constants.MODID)
	public static ThirstMod instance;
	public static EventSystem eventHook = new EventSystem();
	public static Config config = new Config();
	
	@SidedProxy(clientSide = Constants.PACKAGE + ".core.client.ClientProxy", serverSide = Constants.PACKAGE + ".core.main.CommonProxy")
	public static CommonProxy commonProxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(eventHook);
		MinecraftForge.EVENT_BUS.register(eventHook);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, eventHook);
		loadMain();
	}
	
	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		new DrinkRegistry();
	}
	
	@EventHandler
	public void serverClosed(FMLServerStoppedEvent event) {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			PlayerContainer.ALL_PLAYERS.clear();
		}
	}
	
	public void loadMain() {
		new NetworkHandler();
		new BlockLoader();
		new ItemLoader(); 
		new ContentLoader();
	}
	
	public static String getMinecraftDir() {
		File s = Minecraft.getMinecraft().mcDataDir;
		return s.getAbsolutePath();
	}
	
	public static void print(String s) {
		System.out.println("[ThirstMod] " + s);
	}
	
	public static CreativeTabs thirst = new CreativeTabs("drinks") {
		@Override
		public Item getTabIconItem() {
			return Items.potionitem;
		}
	};
}