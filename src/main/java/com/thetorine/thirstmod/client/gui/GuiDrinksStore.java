package com.thetorine.thirstmod.client.gui;

import com.thetorine.thirstmod.common.blocks.*;
import com.thetorine.thirstmod.network.NetworkManager;
import com.thetorine.thirstmod.network.PacketDrinksStore;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.FMLClientHandler;
import org.lwjgl.opengl.GL11;

import java.io.IOException;

public class GuiDrinksStore extends GuiContainer {
    private TileEntityDrinksStore tileEntity;
    private Minecraft minecraft = FMLClientHandler.instance().getClient();
    private GuiArrowButton prevItem;
    private GuiArrowButton nextItem;

    public GuiDrinksStore(Container container, InventoryPlayer inventoryPlayer) {
        super(container);
        tileEntity = (TileEntityDrinksStore) ((ContainerDrinksStore)container).tileEntity;
        mc = minecraft;
    }

    @Override
    public void initGui() {
        super.initGui();
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        this.prevItem = this.addButton(new GuiArrowButton(1, x + 81, y + 20, false));
        this.nextItem = this.addButton(new GuiArrowButton(2, x + 126, y + 20, true));
    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException {
        int drinkId = tileEntity.getField(0);
        boolean flag = false;
        if (button == prevItem) {
            drinkId--;
            flag = true;
        } else if (button == nextItem) {
            drinkId++;
            flag = true;
        }
        if (flag) {
            NetworkManager.getNetworkWrapper().sendToServer(new PacketDrinksStore(tileEntity, drinkId));
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y) {
        this.fontRenderer.drawString("Drinks Store", 55, 6, 0x404040);
        this.fontRenderer.drawString("Switch", 95, 25, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/drinks_store.png"));
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}

