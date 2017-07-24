package com.thetorine.thirstmod.client.gui;

import com.thetorine.thirstmod.ThirstMod;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;

public class GuiThirstBar {

    public static ResourceLocation THIRST_BAR_ICONS = new ResourceLocation("thirstmod:textures/gui/thirst_bar.png");

    public static void onRenderGameOverlayEvent(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
            GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
            ScaledResolution scaledRes = event.getResolution();

            Minecraft.getMinecraft().getTextureManager().bindTexture(THIRST_BAR_ICONS);

            EntityPlayerSP player = Minecraft.getMinecraft().player;

            if (!player.isRidingHorse() && ThirstMod.getClientProxy().clientStats != null) {
                int thirstLevel = ThirstMod.getClientProxy().clientStats.thirstLevel;
                int xStart = scaledRes.getScaledWidth()/2 + 10;
                int yStart = scaledRes.getScaledHeight() - 49;

                for (int i = 0; i < 10; i++) {
                    gui.drawTexturedModalRect(xStart + i*8, yStart, 1, 1, 7, 9); //empty thirst droplet
                    if (thirstLevel % 2 != 0 && 10 - i - 1 == thirstLevel/2) {
                        gui.drawTexturedModalRect(xStart + i*8, yStart, 17, 1, 7, 9); //half full thirst droplet
                    } else if (thirstLevel/2 >= 10 - i) {
                        gui.drawTexturedModalRect(xStart + i*8, yStart, 9, 1, 7, 9); //full thirst droplet
                    }
                }
            }

            Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
        } else if (event.getType() == RenderGameOverlayEvent.ElementType.AIR) {
            event.setCanceled(true);

            GuiIngame gui = Minecraft.getMinecraft().ingameGUI;
            ScaledResolution scaledRes = event.getResolution();

            int xStart = (scaledRes.getScaledWidth() / 2) + 91;
            int yStart = scaledRes.getScaledHeight() - 49;
            int xModifier = 0;
            int yModifier = 0;

            int armorLevel = ForgeHooks.getTotalArmorValue(Minecraft.getMinecraft().player);

            if (!Minecraft.getMinecraft().player.isRidingHorse()) {
                if (armorLevel > 0) {
                    yModifier = -10;
                } else {
                    xModifier = -101;
                }
            }

            if (Minecraft.getMinecraft().player.isInsideOfMaterial(Material.WATER)) {
                int air = Minecraft.getMinecraft().player.getAir();
                int full = MathHelper.ceil(((air - 2) * 10.0D) / 300.0D);
                int partial = MathHelper.ceil((air * 10.0D) / 300.0D) - full;

                for (int i = 0; i < (full + partial); ++i) {
                    gui.drawTexturedModalRect((xStart - (i * 8) - 9) + xModifier, yStart + yModifier, (i < full ? 16 : 25), 18, 9, 9);
                }
            }
        }
    }
}
