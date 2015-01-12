package com.thetorine.thirstmod.common.main;

import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.player.ThirstStats;
import net.minecraft.entity.player.EntityPlayer;

public class ServerSide {

	public void onUpdate(EntityPlayer player) {
		PlayerHandler handler = PlayerHandler.getPlayer(player.getDisplayName());
		if (handler != null) {
			if (!player.capabilities.isCreativeMode) {
				handler.getStats().onTick(player);
				if (player.getHealth() <= 0) {
					handler.getStats().setDefaults();
				}
			}
		} else {
			PlayerHandler.addPlayer(player.getDisplayName(), new PlayerHandler(player, new ThirstStats(player)));
		}
	}
}
