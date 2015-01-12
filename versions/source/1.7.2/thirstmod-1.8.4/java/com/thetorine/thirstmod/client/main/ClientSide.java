package com.thetorine.thirstmod.client.main;

import com.thetorine.thirstmod.client.gui.GuiThirst;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.network.PacketMovement;
import com.thetorine.thirstmod.common.network.PacketSendStat;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.utils.ConfigHelper;
import com.thetorine.thirstmod.common.utils.DrinkLists;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ClientSide {
	private boolean loadedMod;
	private boolean drinkDelay;

	public void onLoad() {

	}

	public void onUpdate() {
		Minecraft minecraft = FMLClientHandler.instance().getClient();
		if (minecraft.currentScreen != null) {
			onGuiUpdate(minecraft.currentScreen);
		}

		if (minecraft.thePlayer != null) {
			EntityPlayer player = minecraft.thePlayer;

			if (!player.capabilities.isCreativeMode) {
				if (!loadedMod) {
					if (Loader.isModLoaded("tukmc_Vz")) {
						ThirstUtils.print("TukMC detected. Using maxpowa's method of drawing the Thirst Bar");
					}
					ThirstMod.CONFIG = new ConfigHelper();
					loadedMod = true;
				}
				checkDrinks(minecraft.thePlayer);
				StatsHolder.getInstance().movementSpeed = ThirstUtils.getMovementStat(Minecraft.getMinecraft().thePlayer);
				StatsHolder.getInstance().username = minecraft.thePlayer.getDisplayName();
				ThirstMod.network.sendToServer(new PacketMovement());
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public void checkDrinks(EntityPlayer player) {
		if (player.getItemInUse() != null) {
			ItemStack item = player.getItemInUse();
			if (player.getItemInUseDuration() == item.getMaxItemUseDuration()) {
				for (int i = 0; i < DrinkLists.extraList.size(); i++) {
					DrinkLists drink = DrinkLists.extraList.get(i);
					if (item.getUnlocalizedName().equals(drink.item.getUnlocalizedName()) && item.getItemDamage() == drink.item.getItemDamage()) {
						if(!drinkDelay) {
							ThirstMod.network.sendToServer(new PacketSendStat(drink.replenish, drink.saturation, drink.poison, drink.poisonChance));
						}
						drinkDelay = !drinkDelay;
					}
				}
				if (item.getItem() instanceof ItemFood) {
					ThirstMod.network.sendToServer(new PacketSendStat(0, -3f, false, 0f));
				}
			}
		}
	}

	public void onGuiUpdate(GuiScreen gui) {
		if (gui instanceof GuiMainMenu) {
			PlayerHandler.ALL_PLAYERS.clear();
			loadedMod = false;
		}
	}
}
