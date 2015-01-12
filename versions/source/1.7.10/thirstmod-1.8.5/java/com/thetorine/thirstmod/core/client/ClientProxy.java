package com.thetorine.thirstmod.core.client;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import com.thetorine.thirstmod.core.*;
import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.main.CommonProxy;
import com.thetorine.thirstmod.core.network.NetworkHandler;
import com.thetorine.thirstmod.core.network.PacketMovement;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void clientTick(EntityPlayer player) {
		ClientStats.getInstance().movementSpeed = getMovementStat(player);
		NetworkHandler.networkWrapper.sendToServer(new PacketMovement());
	}
	
	public static int getMovementStat(EntityPlayer player) {
		double d = player.posX - player.prevPosX;
		double d1 = player.posY - player.prevPosY;
		double d2 = player.posZ - player.prevPosZ;
		return Math.round(MathHelper.sqrt_double((d * d) + (d1 * d1) + (d2 * d2)) * 100F);
	}
}
