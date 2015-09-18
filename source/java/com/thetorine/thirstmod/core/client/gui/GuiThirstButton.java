package com.thetorine.thirstmod.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

public class GuiThirstButton extends GuiButton {
	private final boolean field_146157_o;
	private static final ResourceLocation resource = new ResourceLocation("textures/gui/container/villager.png");

	public GuiThirstButton(int par1, int par2, int par3, boolean par4) {
		super(par1, par2, par3, 12, 19, "");
		this.field_146157_o = par4;
	}

	/**
	 * Draws this button to the screen.
	 */
	@Override
	public void drawButton(Minecraft p_146112_1_, int p_146112_2_, int p_146112_3_) {
		if (this.visible) {
			p_146112_1_.getTextureManager().bindTexture(resource);
			GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
			boolean flag = (p_146112_2_ >= this.xPosition) && (p_146112_3_ >= this.yPosition) && (p_146112_2_ < (this.xPosition + this.width)) && (p_146112_3_ < (this.yPosition + this.height));
			int k = 0;
			int l = 176;

			if (!this.enabled) {
				l += this.width * 2;
			} else if (flag) {
				l += this.width;
			}

			if (!this.field_146157_o) {
				k += this.height;
			}

			this.drawTexturedModalRect(this.xPosition, this.yPosition, l, k, this.width, this.height);
		}
	}
}
