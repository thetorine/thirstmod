package com.thetorine.thirstmod.core.network;

import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkHandler {
	public static SimpleNetworkWrapper networkWrapper;
	private int register = -1;
	
	public NetworkHandler() {
		networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("thirstmod");
		registerPacket(PacketUpdateClient.class, PacketUpdateClient.Handler.class, Side.CLIENT);
		registerPacket(PacketMovement.class, PacketMovement.Handler.class, Side.SERVER);
		registerPacket(PacketDrink.class, PacketDrink.Handler.class, Side.SERVER);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void registerPacket(Class<? extends IMessage> c1, Class c2, Side side) {
		register++;
		networkWrapper.registerMessage(c2, c1, register, side);
	}
}
