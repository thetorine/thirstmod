package com.thetorine.thirstmod.common.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import java.util.Random;
import com.thetorine.thirstmod.client.player.StatsHolder;
import com.thetorine.thirstmod.common.player.PlayerHandler;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

public class PacketSendStat implements IMessage {

	private String username;
	private int replenish;
	private float saturation, amountPoison;
	private boolean poison;

	public PacketSendStat() {
	}

	public PacketSendStat(int replenish, float saturation, boolean poison, float amountPoison) {
		this.replenish = replenish;
		this.saturation = saturation;
		this.poison = poison;
		this.amountPoison = amountPoison;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		username = ByteBufUtils.readUTF8String(buffer);
		replenish = buffer.readInt();
		saturation = buffer.readFloat();
		poison = buffer.readBoolean();
		amountPoison = buffer.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		ByteBufUtils.writeUTF8String(buffer, StatsHolder.getInstance().username);
		buffer.writeInt(replenish);
		buffer.writeFloat(saturation);
		buffer.writeBoolean(poison);
		buffer.writeFloat(amountPoison);
	}

	public void handleServerSide(EntityPlayer player) {
		PlayerHandler.getPlayer(player.getDisplayName()).getStats().addStats(replenish, saturation);
		if (poison) {
			PlayerHandler.getPlayer(player.getDisplayName()).getStats().getPoison().startPoison(new Random(), amountPoison);
		}
	}
	
	public static class Handler implements IMessageHandler<PacketSendStat, IMessage> {
        @Override
        public IMessage onMessage(PacketSendStat message, MessageContext ctx) {
        	EntityPlayer player = ctx.getServerHandler().playerEntity;
        	message.handleServerSide(player);
            return null;
        }
    }
}
