package com.thetorine.thirstmod.client.gui;

import com.thetorine.thirstmod.common.blocks.ContainerDrinksBrewer;
import com.thetorine.thirstmod.common.blocks.TileEntityDrinksBrewer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class GuiDrinksBrewer extends GuiContainer {

    private TileEntityDrinksBrewer tileEntity;
    private Minecraft minecraft = FMLClientHandler.instance().getClient();

    public GuiDrinksBrewer(Container container, InventoryPlayer inventoryPlayer) {
        super(container);
        tileEntity = (TileEntityDrinksBrewer) ((ContainerDrinksBrewer)container).tileEntity;
        mc = minecraft;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRenderer.drawString("Drinks Brewer", 55, 6, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        World world = minecraft.world;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/drinks_brewer.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

        if (tileEntity.getField(0) > 0) {
            int remaining = tileEntity.getFuelRemainingScaled(13);
            drawTexturedModalRect(x + 63, y + 49 + 13 - remaining, 176, 31 + (13 - remaining), 9, remaining);
        }

        if (tileEntity.getField(1) > 0) {
            int completed = tileEntity.getFillTimeScaled(23);
            drawTexturedModalRect(x + 80, y + 35, 177, 14, completed, 15);
        }
    }
}
