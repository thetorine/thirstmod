package com.thetorine.thirstmod.core.client.gui;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.ThirstMod;

public class GuiRenderBar {
	public static Minecraft mc = Minecraft.getMinecraft();
	public static Random rand = new Random();
	public static int updateCounter;
	
	public static int left_height = 39;
    public static int right_height = 39;
    
    public static void renderArmor(int width, int height) {
		mc.mcProfiler.startSection("armor");

		GuiIngame ingameGUI = mc.ingameGUI;
		int left = (width / 2) - 91;
		int top = height - left_height;

		if (!mc.thePlayer.isRidingHorse()) {
			if (ThirstMod.config.METER_ON_LEFT) {
				left = ((width / 2) - 91) + 100;
			}
		}

		int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);
		for (int i = 1; (level > 0) && (i < 20); i += 2) {
			if (i < level) {
				ingameGUI.drawTexturedModalRect(left, top, 34, 9, 9, 9);
			} else if (i == level) {
				ingameGUI.drawTexturedModalRect(left, top, 25, 9, 9, 9);
			} else if (i > level) {
				ingameGUI.drawTexturedModalRect(left, top, 16, 9, 9, 9);
			}
			left += 8;
		}
		left_height += 10;

		mc.mcProfiler.endSection();
	}
    
    public static void renderAir(int width, int height) {
		mc.mcProfiler.startSection("air");
		int left = (width / 2) + 91;
		int top = height - right_height;
		GuiIngame ingameGUI = mc.ingameGUI;

		int y = 0;
		int x = 0;

		int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);

		if (!mc.thePlayer.isRidingHorse()) {
			if (ThirstMod.config.METER_ON_LEFT) {
				if (level > 0) {
					y = -10;
				}
			} else if (!ThirstMod.config.METER_ON_LEFT) {
				if (level > 0) {
					y = -10;
				} else {
					x = -101;
				}
			}
		}

		if (mc.thePlayer.isInsideOfMaterial(Material.water)) {
			int air = mc.thePlayer.getAir();
			int full = MathHelper.ceiling_double_int(((air - 2) * 10.0D) / 300.0D);
			int partial = MathHelper.ceiling_double_int((air * 10.0D) / 300.0D) - full;

			for (int i = 0; i < (full + partial); ++i) {
				ingameGUI.drawTexturedModalRect((left - (i * 8) - 9) + x, top + y, (i < full ? 16 : 25), 18, 9, 9);
			}
			right_height += 10;
		}
	}

    public static void renderThirst(int rwidth, int rheight) {
		bind(new ResourceLocation("thirstmod:textures/gui/thirstBar-new.png"));

		ClientStats stats = ClientStats.getInstance();
		GuiIngame ingameGUI = mc.ingameGUI;
		updateCounter = ingameGUI.getUpdateCounter();
	
		for (int i = 0; i < 10; i++) {
			int width = ThirstMod.config.METER_ON_LEFT ? ((rwidth / 2) - 91) + (i * 8) : ((rwidth / 2) + 91) - (i * 8) - 9;
			int height = rheight - 49;
			int xStart = 1;
			int yStart = 1;
			int yEnd = 9;
			int xEnd = 7;

			if (stats.saturation <= 0.0F && updateCounter % (stats.level * 3 + 1) == 0) {
				height += rand.nextInt(3) - 1;
			}
			
			ingameGUI.drawTexturedModalRect(width, height, xStart, yStart, xEnd, yEnd);
			if(!stats.isPoisoned) {
				if(i * 2 + 1 < stats.level) {
					ingameGUI.drawTexturedModalRect(width, height, xStart + 8, yStart, xEnd, yEnd);
				} else if(i * 2 + 1 == stats.level) {
					ingameGUI.drawTexturedModalRect(width, height, xStart + 16, yStart, xEnd, yEnd);
				}
			} else {
				if (i * 2 + 1 < stats.level) {
					ingameGUI.drawTexturedModalRect(width, height, xStart + 24, yStart, xEnd, yEnd);
				} else if (i * 2 + 1 == stats.level) {
					ingameGUI.drawTexturedModalRect(width, height, xStart + 32, yStart, xEnd, yEnd);
				}
			}
		}
		bind(Gui.icons);
	}
    
    public static void drawTemperature(int w, int h) {
    	float temp = ClientStats.getInstance().temperature;
    	GuiIngame ingameGUI = mc.ingameGUI;
    	ingameGUI.drawCenteredString(mc.fontRendererObj, Float.toString(temp), 200, 100, 0xffffff);
    }

	private static void bind(ResourceLocation res) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
	}
}
