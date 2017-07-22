package com.thetorine.thirstmod.client.logic;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.client.gui.GuiHandler;
import com.thetorine.thirstmod.common.items.Drink;
import com.thetorine.thirstmod.common.items.ItemDrink;
import com.thetorine.thirstmod.common.logic.CommonProxy;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ClientProxy extends CommonProxy {

    public ThirstStats clientStats = new ThirstStats();

    @Override
    public void preInit() {
        super.preInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(ThirstMod.getInstance(), new GuiHandler());
    }

    @Override
    public void init() {
        super.init();
        for (int i = 0; i < Drink.ALL_DRINKS.size(); i++) {
            Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(DRINKS, i, new ModelResourceLocation("thirstmod:drink_item", "inventory"));
        }
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemDrink.ColorHandler(), DRINKS);
    }
}
