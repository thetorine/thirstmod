package com.thetorine.thirstmod.client.gui;

import java.util.Random;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.main.ThirstMod;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.common.ForgeHooks;

public class GuiThirst {
	public Minecraft mc = Minecraft.getMinecraft();
	public Random rand = new Random();
	public int updateCounter;
	
	public int left_height = 39;
    public int right_height = 39;

	public void renderArmor(int width, int height) {
		mc.mcProfiler.startSection("armor");

		GuiIngame ingameGUI = mc.ingameGUI;
		int left = (width / 2) - 91;
		int top = height - left_height;

		if (!ThirstMod.MOD_OFF && !mc.thePlayer.isRidingHorse()) {
			if (ThirstMod.CONFIG.METER_ON_LEFT) {
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

	public void renderAir(int width, int height) {
		mc.mcProfiler.startSection("air");
		int left = (width / 2) + 91;
		int top = height - right_height;
		GuiIngame ingameGUI = mc.ingameGUI;

		int y = 0;
		int x = 0;

		int level = ForgeHooks.getTotalArmorValue(mc.thePlayer);

		if (!ThirstMod.MOD_OFF && !mc.thePlayer.isRidingHorse()) {
			if (ThirstMod.CONFIG.METER_ON_LEFT) {
				if (level > 0) {
					y = -10;
				}
			} else if (!ThirstMod.CONFIG.METER_ON_LEFT) {
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

	public void renderThirst(int rwidth, int rheight) {
		bind(new ResourceLocation("thirstmod:textures/gui/thirstBar.png"));

		StatsHolder thirststats = StatsHolder.getInstance();
		GuiIngame ingameGUI = mc.ingameGUI;
		int thirstLvl = thirststats.level;
		updateCounter = ingameGUI.getUpdateCounter();

		for (int i13 = 0; i13 < 10; i13++) {
			int width = ((rwidth / 2) + 91) - (i13 * 8) - 9;
			int height = rheight - 49;
			int textureXStart = 1;
			int textureYStart = 24;
			int textureYStart1 = 24;
			int textureEndY = 9;
			int textureEndX = 7;

			if ((thirststats.saturation <= 0.0F) && ((updateCounter % ((thirstLvl * 3) + 1)) == 0)) {
				height += rand.nextInt(3) - 1;
			}

			if (ThirstMod.CONFIG.METER_ON_LEFT) {
				width = ((rwidth / 2) - 91) + (i13 * 8);
			}

			if (ThirstMod.CONFIG.LIGHT_BLUE_COLOR) {
				textureYStart = 34;
			}

			ingameGUI.drawTexturedModalRect(width, height, textureXStart, textureYStart1, textureEndX, textureEndY);

			if (!thirststats.isPoisoned) {
				if (((i13 * 2) + 1) < thirstLvl) {
					ingameGUI.drawTexturedModalRect(width, height, textureXStart + 8, textureYStart, textureEndX, textureEndY);
				} else if (((i13 * 2) + 1) == thirstLvl) {
					ingameGUI.drawTexturedModalRect(width, height, textureXStart + 16, textureYStart, textureEndX, textureEndY);
				}
			} else if (thirststats.isPoisoned) {
				if (((i13 * 2) + 1) < thirstLvl) {
					ingameGUI.drawTexturedModalRect(width, height, textureXStart + 24, textureYStart, textureEndX, textureEndY);
				} else if (((i13 * 2) + 1) == thirstLvl) {
					ingameGUI.drawTexturedModalRect(width, height, textureXStart + 32, textureYStart, textureEndX, textureEndY);
				}
			}
		}
		bind(Gui.icons);
	}

	private void bind(ResourceLocation res) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(res);
	}
}
