package com.thetorine.thirstmod.core.content;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import com.thetorine.thirstmod.core.content.blocks.BlockDB;
import com.thetorine.thirstmod.core.content.blocks.BlockDS;
import com.thetorine.thirstmod.core.content.blocks.BlockRC;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDB;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDS;
import com.thetorine.thirstmod.core.content.blocks.TileEntityRC;

public class BlockLoader {
	public static Block drinks_store = new BlockDS().setUnlocalizedName("drinks_store");
	public static Block drinks_brewer = new BlockDB().setUnlocalizedName("drinks_brewer");
	public static Block rain_collector = new BlockRC().setUnlocalizedName("rain_collector");
	
	public BlockLoader() {
		registerBlock(drinks_store);
		registerBlock(drinks_brewer);
		registerBlock(rain_collector);
		
		GameRegistry.registerTileEntity(TileEntityDS.class, drinks_store.getLocalizedName());
		GameRegistry.registerTileEntity(TileEntityDB.class, drinks_brewer.getUnlocalizedName());
		GameRegistry.registerTileEntity(TileEntityRC.class, rain_collector.getUnlocalizedName());
		
		GameRegistry.addRecipe(new ItemStack(drinks_store), new Object[] { 
			"***", "*#*", "*^*", '*', Items.quartz, '#', Blocks.glass_pane, '^', Blocks.piston 
		});
		
		GameRegistry.addRecipe(new ItemStack(drinks_brewer, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.glass_bottle, 
		});
		
		GameRegistry.addRecipe(new ItemStack(rain_collector, 1), new Object[] { 
			"***", "*#*", "***", '*', Blocks.cobblestone, '#', Items.bucket, 
		});
	}
	
	private void registerBlock(Block b) {
		String name = b.getUnlocalizedName().replace("tile.", "");
		GameRegistry.registerBlock(b, name);
		ItemLoader.ALL_ITEMS.add(Item.getItemFromBlock(b));
	}
}
