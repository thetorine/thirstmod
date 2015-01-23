package com.thetorine.thirstmod.core.main;

import com.thetorine.thirstmod.core.player.PlayerContainer;

import net.minecraft.entity.player.EntityPlayer;

public class CommonProxy {
	
	public void serverTick(EntityPlayer player) {
		PlayerContainer handler = PlayerContainer.getPlayer(player);
		if (handler != null) {
			if (!player.capabilities.isCreativeMode) {
				handler.getStats().onTick();
			}
		} else {
			PlayerContainer.addPlayer(player);
		}
	}
	
	public void clientTick(EntityPlayer player) {
	}
}
