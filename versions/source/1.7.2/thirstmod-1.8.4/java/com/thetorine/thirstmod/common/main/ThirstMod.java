package com.thetorine.thirstmod.common.main;

import com.thetorine.thirstmod.client.gui.GuiDrinkStore;
import com.thetorine.thirstmod.client.gui.GuiJM;
import com.thetorine.thirstmod.client.gui.GuiRC;
import com.thetorine.thirstmod.common.blocks.BlockDrinkShop;
import com.thetorine.thirstmod.common.blocks.BlockJM;
import com.thetorine.thirstmod.common.blocks.BlockRC;
import com.thetorine.thirstmod.common.blocks.ContainerDS;
import com.thetorine.thirstmod.common.blocks.ContainerJM;
import com.thetorine.thirstmod.common.blocks.ContainerRC;
import com.thetorine.thirstmod.common.blocks.TileEntityDS;
import com.thetorine.thirstmod.common.blocks.TileEntityJM;
import com.thetorine.thirstmod.common.blocks.TileEntityRC;
import com.thetorine.thirstmod.common.content.DrinkParser;
import com.thetorine.thirstmod.common.items.DrinkLoader;
import com.thetorine.thirstmod.common.items.ItemCCFilter;
import com.thetorine.thirstmod.common.items.ItemCanteen;
import com.thetorine.thirstmod.common.items.ItemThirst;
import com.thetorine.thirstmod.common.network.*;
import com.thetorine.thirstmod.common.player.EventHook;
import com.thetorine.thirstmod.common.utils.ConfigHelper;
import com.thetorine.thirstmod.common.utils.CreativeTabThirst;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import com.thetorine.thirstmod.common.utils.Version;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.IGuiHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;

@Mod(modid = ThirstUtils.ID, name = ThirstUtils.NAME, version = Version.VERSION)
public class ThirstMod implements IGuiHandler {
	public static ConfigHelper CONFIG = new ConfigHelper();
	public static CreativeTabs drinkTab = new CreativeTabThirst("drinks");

	public static final Block waterCollector = new BlockRC().setBlockName("rain_collector").setResistance(5F).setHardness(4F).setCreativeTab(drinkTab);
	public static final Block juiceMaker = new BlockJM().setBlockName("drinks_brewer").setResistance(5F).setHardness(4F).setCreativeTab(drinkTab);
	public static final Block drinkStore = new BlockDrinkShop().setBlockName("drink_shop");

	public static final Item dFilter = (new ItemThirst().setUnlocalizedName("dirty_filter").setMaxStackSize(1)).setCreativeTab(drinkTab).setTextureName("thirstmod:dirty_filter");
	public static final Item filter = (new ItemThirst().setUnlocalizedName("filter").setMaxStackSize(1)).setMaxDamage(4).setCreativeTab(drinkTab).setTextureName("thirstmod:filter");
	public static final Item currency = (new ItemThirst()).setUnlocalizedName("coins").setCreativeTab(drinkTab).setTextureName("thirstmod:coins");
	public static final Item ccFilter = (new ItemCCFilter().setUnlocalizedName("coal_filter")).setTextureName("thirstmod:coal_filter");
	public static final Item canteen = (new ItemCanteen()).setCreativeTab(drinkTab);
	
	//CHANGE BEFORE RELEASE
	public static final boolean DEBUG_MODE = true;

	@Instance(ThirstUtils.ID)
	public static ThirstMod INSTANCE;

	@SidedProxy(clientSide = "com.thetorine.thirstmod.client.main.ClientProxy", serverSide = "com.thetorine.thirstmod.common.main.CommonProxy")
	public static CommonProxy proxy;
	
	public static SimpleNetworkWrapper network = NetworkRegistry.INSTANCE.newSimpleChannel("thirstmod");

	public static boolean MOD_OFF = false;
	public static int displayMessage;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		GameRegistry.registerBlock(waterCollector, "waterCollector");
		GameRegistry.registerBlock(juiceMaker, "juiceMaker");
		GameRegistry.registerBlock(drinkStore, "drinkStore");
		GameRegistry.registerTileEntity(TileEntityJM.class, "DrinksBrewer");
		GameRegistry.registerTileEntity(TileEntityRC.class, "WaterCollector");
		GameRegistry.registerTileEntity(TileEntityDS.class, "DrinksStore");

		GameRegistry.registerItem(ccFilter, "charcoal_filter");
		GameRegistry.registerItem(currency, "currency");
		GameRegistry.registerItem(dFilter, "damaged_filter");
		GameRegistry.registerItem(filter, "filter");

		GameRegistry.addRecipe(new ItemStack(waterCollector, 1), new Object[] { "***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.bucket, });
		GameRegistry.addRecipe(new ItemStack(juiceMaker, 1), new Object[] { "***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.glass_bottle, });
		GameRegistry.addRecipe(new ItemStack(drinkStore), new Object[] { "***", "*#*", "*^*", '*', Items.quartz, '#', Blocks.glass_pane, '^', Blocks.piston });
		GameRegistry.addRecipe(new ItemStack(filter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', Items.string });
		GameRegistry.addRecipe(new ItemStack(ccFilter), new Object[] { " * ", "*!*", " * ", '*', Items.stick, '!', new ItemStack(Items.coal, 0, 1) });
		GameRegistry.addShapelessRecipe(new ItemStack(filter), new Object[] { Items.string, dFilter });
		GameRegistry.addShapelessRecipe(new ItemStack(currency, 5), new Object[] { Items.gold_nugget });
		GameRegistry.addRecipe(new ItemStack(canteen, 1, 0), new Object[] { "* *", " * ", '*', Items.leather });
		
		network.registerMessage(PacketConfigs.Handler.class, PacketConfigs.class, 0, Side.CLIENT);
		network.registerMessage(PacketDrink.Handler.class, PacketDrink.class, 1, Side.SERVER);
		network.registerMessage(PacketMovement.Handler.class, PacketMovement.class, 2, Side.SERVER);
		network.registerMessage(PacketSendStat.Handler.class, PacketSendStat.class, 3, Side.SERVER);
		network.registerMessage(PacketUpdateClient.Handler.class, PacketUpdateClient.class, 4, Side.CLIENT);

		new DrinkLoader().loadDrinks();
		new DrinkParser();
		FMLCommonHandler.instance().bus().register(new EventHook());
		MinecraftForge.EVENT_BUS.register(new EventHook());
		NetworkRegistry.INSTANCE.registerGuiHandler(this, this);
		proxy.onInit();
	}

	/**
	 * Called once the mod has been loaded.
	 * 
	 * @param event
	 */
	@EventHandler
	public void onLoad(FMLInitializationEvent event) {
		proxy.onLoad();
		ThirstUtils.print("ThirstMod Loaded");
	}

	/**
	 * Called when 1 game loop is done.
	 * 
	 * @param minecraft
	 */
	public void onTickInGame() {
		if (MOD_OFF == false) {
			proxy.onTickInGame();
		}
	}

	public void onServerTick(EntityPlayer player) {
		if (MOD_OFF == false) {
			proxy.onServerTick(player);
		}
	}

	/**
	 * Gets the server container.
	 */
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch (ID) {
			case 90:
				return new ContainerJM(player.inventory, (TileEntityJM) tile);
			case 91:
				return new ContainerRC(player.inventory, (TileEntityRC) tile);
			case 93:
				return new ContainerDS(player.inventory, (TileEntityDS) tile);
		}
		return null;
	}

	/**
	 * Gets the client gui.
	 */
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tile = world.getTileEntity(x, y, z);
		switch (ID) {
			case 90:
				return new GuiJM(player.inventory, (TileEntityJM) tile);
			case 91:
				return new GuiRC(player.inventory, (TileEntityRC) tile);
			case 93:
				return new GuiDrinkStore(player.inventory, (TileEntityDS) tile);
		}
		return null;
	}
}