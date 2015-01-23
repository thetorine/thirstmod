package com.thetorine.thirstmod.core.network;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.player.PlayerContainer;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;

public class PacketMovement implements IMessage {
	private String username;
	private int movementSpeed;
	
	public PacketMovement() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		username = ByteBufUtils.readUTF8String(buf);
		movementSpeed = buf.readInt();	
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, FMLClientHandler.instance().getClientPlayerEntity().getDisplayName());
		buf.writeInt(ClientStats.getInstance().movementSpeed);	
	}
	
	public void handleServerSide() {
		PlayerContainer handler = PlayerContainer.getPlayer(username);
		if (handler != null) {
			handler.getStats().movementSpeed = movementSpeed;
		}
	}
	
	public static class Handler implements IMessageHandler<PacketMovement, IMessage> {
        @Override
        public IMessage onMessage(PacketMovement message, MessageContext ctx) {
        	message.handleServerSide();
            return null;
        }
    }
}
