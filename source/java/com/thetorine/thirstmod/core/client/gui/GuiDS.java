package com.thetorine.thirstmod.core.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;

import com.thetorine.thirstmod.core.content.blocks.ContainerDS;
import com.thetorine.thirstmod.core.content.blocks.TileEntityDS;
import com.thetorine.thirstmod.core.content.packs.DrinkLists;
import com.thetorine.thirstmod.core.network.NetworkHandler;
import com.thetorine.thirstmod.core.network.PacketDrink;

public class GuiDS extends GuiContainer {
	private TileEntityDS tile;

	public GuiDS(InventoryPlayer player, TileEntityDS tile) {
		super(new ContainerDS(player, tile));
		this.tile = tile;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void drawGuiContainerForegroundLayer(int par1, int par2) {
		this.fontRendererObj.drawString("Drinks Store", 55, 6, 0x404040);
		int var5 = (width - xSize) / 2;
		int var6 = (height - ySize) / 2;

		this.buttonList.clear();

		// switch
		this.fontRendererObj.drawString("Switch", 95, 25, 4210752);
		buttonList.add(new GuiThirstButton(0, var5 + 81, var6 + 20, false));
		buttonList.add(new GuiThirstButton(1, var5 + 126, var6 + 20, true));

		// quantity
		this.fontRendererObj.drawString((tile.amountToBuy > 9 ? "Amount: " + tile.amountToBuy : "Amount: 0" + tile.amountToBuy), 84, 45, 4210752);
		buttonList.add(new GuiThirstButton(2, var5 + 70, var6 + 40, false));
		buttonList.add(new GuiThirstButton(3, var5 + 137, var6 + 40, true));

		// buy
		buttonList.add(new GuiButton(4, var5 + 65, var6 + 60, 90, 20, "Buy! " + (DrinkLists.LOADED_DRINKS.get(tile.page).storeRecipe * tile.amountToBuy) + " Coins!"));
	}

	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		switch (par1GuiButton.id) {
			//change page left
			case 0: {
				changePage(false);
				checkPurchaseAmount();
				break;
			}
			//change page right
			case 1: {
				changePage(true);
				checkPurchaseAmount();
				break;
			}
			//move amount counter left
			case 2: {
				changeAmountToBuy(false);
				break;
			}
			//move amount counter right
			case 3: {
				changeAmountToBuy(true);
				break;
			}
			//buy drink
			case 4: {
				attemptPurchase();
				break;
			}
		}
		updateServer();
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		mc.getTextureManager().bindTexture(new ResourceLocation("thirstmod:textures/gui/drinksStore.png"));
		int x = (width - xSize) / 2;
		int y = (height - ySize) / 2;
		drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
	}
	
	private void attemptPurchase() {
		tile.canBuy = 1;
	}
	
	private void changePage(boolean direction) {
		if(tile.page == (!direction ? 0: DrinkLists.LOADED_DRINKS.size()-1)) {
			tile.page = (!direction ? DrinkLists.LOADED_DRINKS.size()-1 : 0);
		} else {
			tile.page = (!direction ? tile.page-1 : tile.page+1);
		}
	}
	
	private void changeAmountToBuy(boolean direction) {
		int max = calculateMaxStack();
		if(tile.amountToBuy == (!direction ? 1: max)) {
			tile.amountToBuy = (!direction ? max : 1);
		} else {
			tile.amountToBuy = (!direction ? tile.amountToBuy-1 : tile.amountToBuy+1);
		}
	}
	
	private int calculateMaxStack() {
		int max = 1;
		for (int i = 1; i <= 64; i++) {
			if(DrinkLists.LOADED_DRINKS.get(tile.page).storeRecipe * i <= 64) {
				max = i;
			} else {
				break;
			}
		}
		if (max > DrinkLists.LOADED_DRINKS.get(tile.page).item.getMaxStackSize()) {
			max = DrinkLists.LOADED_DRINKS.get(tile.page).item.getMaxStackSize();
		}
		return max;
	}
	
	private void checkPurchaseAmount() {
		int max = calculateMaxStack();
		if(tile.amountToBuy > max) {
			tile.amountToBuy = max;
		}
	}
	
	private void updateServer() {
		NetworkHandler.networkWrapper.sendToServer(new PacketDrink(tile));
	}
}