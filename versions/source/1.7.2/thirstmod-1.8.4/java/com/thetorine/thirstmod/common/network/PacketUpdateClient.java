package com.thetorine.thirstmod.common.network;

import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.player.ThirstStats;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class PacketUpdateClient implements IMessage {

	private String username;
	private int level;
	private float saturation;
	private boolean poisoned;
	
	public PacketUpdateClient() {
	}

	public PacketUpdateClient(int level, float saturation, boolean poisoned) {
		this.level = level;
		this.saturation = saturation;
		this.poisoned = poisoned;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		username = ByteBufUtils.readUTF8String(buffer);
		level = buffer.readInt();
		saturation = buffer.readFloat();
		poisoned = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, StatsHolder.getInstance().username);
		buffer.writeInt(level);
		buffer.writeFloat(saturation);
		buffer.writeBoolean(poisoned);		
	}

	public void handleClientSide() {
		StatsHolder.getInstance().level = level;
		StatsHolder.getInstance().saturation = saturation;
		StatsHolder.getInstance().isPoisoned = poisoned;
	}
	
	public static class Handler implements IMessageHandler<PacketUpdateClient, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateClient message, MessageContext ctx) {
        	message.handleClientSide();
            return null;
        }
    }
}
