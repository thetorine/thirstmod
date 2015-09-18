package com.thetorine.thirstmod.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.player.PlayerContainer;

public class PacketMovement implements IMessage {

	private String username;
	private int movementSpeed;
	
	public PacketMovement() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		username = ByteBufUtils.readUTF8String(buf);
		movementSpeed = buf.readInt();	
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, FMLClientHandler.instance().getClientPlayerEntity().getDisplayNameString());
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
