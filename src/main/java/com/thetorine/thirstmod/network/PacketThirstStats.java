package com.thetorine.thirstmod.network;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketThirstStats implements IMessage {

    public int thirstLevel;
    public float saturation, exhaustion;

    public PacketThirstStats() {}

    public PacketThirstStats(ThirstStats stats) {
        this.thirstLevel = stats.thirstLevel;
        this.saturation = stats.saturation;
        this.exhaustion = stats.exhaustion;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        thirstLevel = buf.readInt();
        saturation = buf.readFloat();
        exhaustion = buf.readFloat();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.thirstLevel);
        buf.writeFloat(this.saturation);
        buf.writeFloat(this.exhaustion);
    }

    public void handleClientSide() {
        ThirstStats stats = ThirstMod.getClientProxy().clientStats;
        stats.thirstLevel = this.thirstLevel;
        stats.saturation = this.saturation;
        stats.exhaustion = this.exhaustion;
    }

    public static class Handler implements IMessageHandler<PacketThirstStats, IMessage> {
        @Override
        public IMessage onMessage(PacketThirstStats message, MessageContext ctx) {
            message.handleClientSide();
            return null;
        }
    }
}
