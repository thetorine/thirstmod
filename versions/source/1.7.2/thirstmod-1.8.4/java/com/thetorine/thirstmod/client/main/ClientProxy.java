package com.thetorine.thirstmod.client.main;

import com.thetorine.thirstmod.common.content.ContentLoader;
import com.thetorine.thirstmod.common.main.CommonProxy;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	public ClientSide client = new ClientSide();

	@Override
	public void onInit() {
		new ContentLoader(Side.CLIENT);
	}

	@Override
	public void onLoad() {
		client.onLoad();
	}

	@Override
	public void onTickInGame() {
		switch (FMLCommonHandler.instance().getEffectiveSide()) {
			case CLIENT:
				client.onUpdate();
				break;
			default:
		}
	}

	@Override
	public void onServerTick(EntityPlayer player) {
		server.onUpdate(player);
	}

	/**
	 * Gets an EntityPlayer.class instance.
	 * 
	 * @return EntityPlayer.class instance.
	 */
	public static EntityPlayer getPlayer() {
		return FMLClientHandler.instance().getClient().thePlayer;
	}

	/**
	 * Gets an EntityPlayerMP.class instance. Only works client server.
	 * 
	 * @return EntityPlayerMP.class instance.
	 */
	public static EntityPlayerMP getPlayerMp(String username) {
		MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
		EntityPlayerMP player = server.getConfigurationManager().getPlayerForUsername(username);
		return player;
	}
}