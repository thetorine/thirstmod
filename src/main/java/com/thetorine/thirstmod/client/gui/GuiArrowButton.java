package com.thetorine.thirstmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiArrowButton extends GuiButton {

    private static final ResourceLocation GUI_TEXTURE = new ResourceLocation("textures/gui/container/villager.png");

    private boolean arrowDirection;

    public GuiArrowButton(int buttonID, int x, int y, boolean direction) {
        super(buttonID, x, y, 12, 19, "");
        this.arrowDirection = direction;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            mc.getTextureManager().bindTexture(GUI_TEXTURE);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            boolean highlighted = mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height;
            int bWidth = 176;
            int bHeight = 0;
            if (!enabled) {
                bWidth += width * 2;
            } else if (highlighted) {
                bWidth += width;
            }
            if (!arrowDirection) {
                bHeight += height;
            }
            drawTexturedModalRect(x, y, bWidth, bHeight, width, height);
        }
    }
}
