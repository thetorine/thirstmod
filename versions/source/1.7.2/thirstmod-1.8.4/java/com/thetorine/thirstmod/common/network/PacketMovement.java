package com.thetorine.thirstmod.common.network;

import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.relauncher.Side;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

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
		ByteBufUtils.writeUTF8String(buf, StatsHolder.getInstance().username);
		buf.writeInt(StatsHolder.getInstance().movementSpeed);	
	}
	
	public void handleServerSide() {
		PlayerHandler handler = PlayerHandler.getPlayer(username);
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
