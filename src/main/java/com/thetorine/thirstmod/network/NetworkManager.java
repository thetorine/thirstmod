package com.thetorine.thirstmod.network;

import com.thetorine.thirstmod.Constants;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class NetworkManager {

    private static SimpleNetworkWrapper networkWrapper;
    private static int registerCount = -1;

    public static SimpleNetworkWrapper getNetworkWrapper() {
        if(networkWrapper == null) {
            networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(Constants.MOD_ID);
            registerPacket(PacketThirstStats.class, PacketThirstStats.Handler.class, Side.CLIENT);
            registerPacket(PacketMovementSpeed.class, PacketMovementSpeed.Handler.class, Side.SERVER);
        }
        return networkWrapper;
    }

    private static void registerPacket(Class<? extends IMessage> c1, Class c2, Side side) {
        registerCount++;
        networkWrapper.registerMessage(c2, c1, registerCount, side);
    }
}
