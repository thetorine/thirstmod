package com.thetorine.thirstmod.client.gui;

import com.thetorine.thirstmod.Constants;
import com.thetorine.thirstmod.common.blocks.ContainerDrinksBrewer;
import com.thetorine.thirstmod.common.blocks.ContainerRainCollector;
import com.thetorine.thirstmod.common.blocks.TileEntityDrinksBrewer;
import com.thetorine.thirstmod.common.blocks.TileEntityRainCollector;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

import javax.annotation.Nullable;

public class GuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case Constants.RAIN_COLLECTOR_ID:
                return new ContainerRainCollector(player.inventory, (TileEntityRainCollector)world.getTileEntity(new BlockPos(x, y, z)));
            case Constants.DRINKS_BREWER_ID:
                return new ContainerDrinksBrewer(player.inventory, (TileEntityDrinksBrewer)world.getTileEntity(new BlockPos(x, y, z)));
            default: return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case Constants.RAIN_COLLECTOR_ID:
                return new GuiRainCollector((Container) getServerGuiElement(ID, player, world, x, y, z), player.inventory);
            case Constants.DRINKS_BREWER_ID:
                return new GuiDrinksBrewer((Container) getServerGuiElement(ID, player, world, x, y, z), player.inventory);
            default: return null;
        }
    }
}
