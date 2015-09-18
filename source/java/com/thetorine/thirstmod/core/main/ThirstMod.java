package com.thetorine.thirstmod.core.main;

import java.io.File;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

import com.thetorine.thirstmod.core.content.BlockLoader;
import com.thetorine.thirstmod.core.content.ItemDrink;
import com.thetorine.thirstmod.core.content.ItemLoader;
import com.thetorine.thirstmod.core.content.packs.ContentLoader;
import com.thetorine.thirstmod.core.content.packs.DrinkRegistry;
import com.thetorine.thirstmod.core.network.NetworkHandler;
import com.thetorine.thirstmod.core.player.PlayerContainer;
import com.thetorine.thirstmod.core.utils.Config;
import com.thetorine.thirstmod.core.utils.Constants;

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
		renderIcons();
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
	
	private void renderIcons() {
		if(FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) {
			for(Item item : ItemLoader.ALL_ITEMS) {
				if(item.getHasSubtypes()) {
					for(int i = 0; i < 11; i++) {
						Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, i, new ModelResourceLocation("thirstmod:canteen", "inventory"));
					}
				} else {
					if(item instanceof ItemDrink) {
						Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("thirstmod:content_drink", "inventory"));
					} else {
						Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation("thirstmod:" + item.getUnlocalizedName().replaceAll("item.", "").replaceAll("tile.", ""), "inventory"));
					}
				}
			}
		}
	}
	
	public static String getMinecraftDir() {
		File s = FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT ? Minecraft.getMinecraft().mcDataDir : MinecraftServer.getServer().getDataDirectory();
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