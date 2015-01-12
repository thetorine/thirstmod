package com.thetorine.thirstmod.core.content;

import net.minecraft.block.*;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

import com.thetorine.thirstmod.core.content.blocks.*;

import cpw.mods.fml.common.registry.GameRegistry;

public class BlockLoader {
	public static Block drinks_store = new BlockDS().setBlockName("drinks_store");
	public static Block drinks_brewer = new BlockDB().setBlockName("drinks_brewer");
	public static Block rain_collector = new BlockRC().setBlockName("rain_collector");
	
	public BlockLoader() {
		GameRegistry.registerBlock(drinks_store, drinks_store.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityDS.class, drinks_store.getLocalizedName());
		GameRegistry.addRecipe(new ItemStack(drinks_store), new Object[] { 
			"***", "*#*", "*^*", '*', Items.quartz, '#', Blocks.glass_pane, '^', Blocks.piston 
		});
		
		GameRegistry.registerBlock(drinks_brewer, drinks_brewer.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityDB.class, drinks_brewer.getUnlocalizedName());
		GameRegistry.addRecipe(new ItemStack(drinks_brewer, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.glass_bottle, 
		});
		
		GameRegistry.registerBlock(rain_collector, rain_collector.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityRC.class, rain_collector.getUnlocalizedName());
		GameRegistry.addRecipe(new ItemStack(rain_collector, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.bucket, 
		});
	}
}
