package com.thetorine.thirstmod.client.logic;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.client.gui.GuiHandler;
import com.thetorine.thirstmod.common.items.ItemDrink;
import com.thetorine.thirstmod.common.logic.CommonProxy;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import net.minecraft.client.Minecraft;
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
        Minecraft.getMinecraft().getItemColors().registerItemColorHandler(new ItemDrink.ColorHandler(), DRINKS);
    }
}
