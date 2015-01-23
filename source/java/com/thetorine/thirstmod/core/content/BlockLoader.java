package com.thetorine.thirstmod.core.content;

import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

import com.thetorine.thirstmod.core.content.blocks.*;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockLoader {
	public static Block drinksStore = new BlockDS().setBlockName("drinks_store");
	public static Block drinksBrewer = new BlockDB().setBlockName("drinks_brewer");
	public static Block rainCollector = new BlockRC().setBlockName("rain_collector");
	
	public BlockLoader() {
		GameRegistry.registerBlock(drinksStore, drinksStore.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityDS.class, drinksStore.getLocalizedName());
		GameRegistry.addRecipe(new ItemStack(drinksStore), new Object[] { 
			"***", "*#*", "*^*", '*', Items.quartz, '#', Blocks.glass_pane, '^', Blocks.piston 
		});
		
		GameRegistry.registerBlock(drinksBrewer, drinksBrewer.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityDB.class, drinksBrewer.getUnlocalizedName());
		GameRegistry.addRecipe(new ItemStack(drinksBrewer, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.glass_bottle, 
		});
		
		GameRegistry.registerBlock(rainCollector, rainCollector.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityRC.class, rainCollector.getUnlocalizedName());
		GameRegistry.addRecipe(new ItemStack(rainCollector, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.bucket, 
		});
	}
}
