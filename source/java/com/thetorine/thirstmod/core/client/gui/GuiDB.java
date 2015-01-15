package com.thetorine.thirstmod.core.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.thetorine.thirstmod.core.content.blocks.ContainerDB;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDB;

import cpw.mods.fml.client.FMLClientHandler;

public class GuiDB extends GuiContainer {
	private TileEntityDB tile;

	public GuiDB(InventoryPlayer inventoryplayer, TileEntityDB tile) {
		super(new ContainerDB(inventoryplayer, tile));
		this.tile = tile;
		this.mc = FMLClientHandler.instance().getClient();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int i, int j) {
		this.fontRendererObj.drawString("Drinks Brewer", 54, 10, 0x404040);
		this.fontRendererObj.drawString("Glass", 10, 42, 0x404040);
		this.fontRendererObj.drawString("Fuel", 75, 55, 0x404040);
		this.fontRendererObj.drawString("Item", 78, 26, 0x404040);
		this.fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
		this.fontRendererObj.drawString("Drink", 140, 40, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/drinksBrewer.png"));
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);

		if (tile.fuelLevel > 0) {
			int m = tile.getFuelLevelScaled(13);
			drawTexturedModalRect(188, 99-m, 176, 44-m, 9, m);
		}

		if (tile.brewTime > 0) {
			int k1 = tile.getBrewTimeScaled(24);
			drawTexturedModalRect(x + 79, y + 35, 176, 14, k1 + 1, 15);
		}
	}
}
