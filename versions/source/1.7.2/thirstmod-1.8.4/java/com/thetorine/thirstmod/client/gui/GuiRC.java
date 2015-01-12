package com.thetorine.thirstmod.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.lwjgl.opengl.GL11;
import com.thetorine.thirstmod.common.blocks.ContainerRC;
import com.thetorine.thirstmod.common.blocks.TileEntityRC;
import cpw.mods.fml.client.FMLClientHandler;

public class GuiRC extends GuiContainer {
	private TileEntityRC rcInv;
	private Minecraft minecraft = FMLClientHandler.instance().getClient();

	public GuiRC(InventoryPlayer var1, TileEntityRC var2) {
		super(new ContainerRC(var1, var2));
		rcInv = var2;
		mc = minecraft;
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int x, int y) {
		this.fontRendererObj.drawString("Rain Collector", 55, 6, 0x404040);
		this.fontRendererObj.drawString("Item to Fill", 78, 60, 0x404040);
		this.fontRendererObj.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
		World world = minecraft.theWorld;
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		minecraft.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/waterCollector.png"));
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		int bucketScaled = rcInv.getInternalBucketScaled(12);
		int meterScaled = rcInv.getRainMeterScaled(24);
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
		drawTexturedModalRect(x + 57, (y + 36 + 12) - bucketScaled, 176, 12 - bucketScaled, 14, bucketScaled + 2);
		drawTexturedModalRect(x + 79, y + 34, 176, 14, meterScaled + 1, 16);

		if (world.isRaining() && rcInv.canRainOn(rcInv.xCoord, rcInv.yCoord, rcInv.zCoord, world)) {
			drawTexturedModalRect(x + 55, y + 16, 176, 31, 18, 18);
		}
	}
}
