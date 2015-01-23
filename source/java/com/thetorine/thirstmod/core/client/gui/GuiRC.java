package com.thetorine.thirstmod.core.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

import com.thetorine.thirstmod.core.content.blocks.ContainerRC;
import com.thetorine.thirstmod.core.content.blocks.TileEntityRC;

public class GuiRC extends GuiContainer {
	private TileEntityRC tileEntity;

	public GuiRC(InventoryPlayer var1, TileEntityRC var2) {
		super(new ContainerRC(var1, var2));
		tileEntity = var2;
		mc = Minecraft.getMinecraft();
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRendererObj.drawString("Rain Collector", 55, 6, 0x404040);
		this.fontRendererObj.drawString("Item to Fill", 78, 60, 0x404040);
		this.fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		World world = mc.theWorld;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/waterCollector.png"));
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		int bucketScaled = tileEntity.getInternalBucketScaled(24);
		int meterScaled = tileEntity.getRainMeterScaled(24);
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		drawTexturedModalRect(x + 79, y + 34, 176, 16, meterScaled + 1, 17);
		
		if(tileEntity.internalBucket > 0) {
			drawTexturedModalRect(x + 60, y + 51 - bucketScaled, 179, 16-bucketScaled, 8, bucketScaled);
		}

		if (world.isRaining() && tileEntity.canRainOn(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord, world)) {
			drawTexturedModalRect(x + 55, y + 14, 176, 31, 18, 20);
		}
	}
}
