package com.thetorine.thirstmod.common.main;

import com.thetorine.thirstmod.common.content.ContentLoader;
import net.minecraft.entity.player.EntityPlayer;
import cpw.mods.fml.relauncher.Side;

public class CommonProxy {
	public boolean loadedMod = false;

	public ServerSide server = new ServerSide();

	public void onInit() {
		new ContentLoader(Side.SERVER);
	}

	public void onLoad() {
	}

	public void onTickInGame() {

	}

	public void onServerTick(EntityPlayer player) {
		server.onUpdate(player);
	}
}
