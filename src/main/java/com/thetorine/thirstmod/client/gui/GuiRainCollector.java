package com.thetorine.thirstmod.client.gui;

import com.thetorine.thirstmod.common.blocks.ContainerRainCollector;
import com.thetorine.thirstmod.common.blocks.TileEntityRainCollector;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

public class GuiRainCollector extends GuiContainer {
    private TileEntityRainCollector tileEntity;
    private Minecraft minecraft = FMLClientHandler.instance().getClient();

    public GuiRainCollector(Container container, InventoryPlayer inventoryPlayer) {
        super(container);
        tileEntity = (TileEntityRainCollector) ((ContainerRainCollector)container).tileEntity;
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
        this.fontRenderer.drawString("Rain Collector", 55, 6, 0x404040);
        this.fontRenderer.drawString("Item to Fill", 78, 60, 0x404040);
        this.fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        World world = minecraft.world;
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/rain_collector.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        int bucketScaled = tileEntity.getTankCapacityScaled(16);
        int meterScaled = tileEntity.getProgressBarScaled(24);
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
        drawTexturedModalRect(x + 79, y + 34, 176, 16, meterScaled + 1, 17);

        if(tileEntity.getField(0) > 0) {
            drawTexturedModalRect(x + 60, y + 51 - bucketScaled, 179, 16-bucketScaled, 8, bucketScaled);
        }

        if (tileEntity.canRainOn(tileEntity.getPos(), world)) {
            drawTexturedModalRect(x + 55, y + 14, 176, 31, 18, 20);
        }
    }
}