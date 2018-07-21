package com.thetorine.thirstmod.network;

import com.thetorine.thirstmod.ThirstMod;
import com.thetorine.thirstmod.common.logic.ThirstStats;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketMovementSpeed implements IMessage {

    private UUID uuid;
    private int ms = 0;

    public PacketMovementSpeed() {}

    public PacketMovementSpeed(EntityPlayer player, ThirstStats stats) {
        uuid = player.getUniqueID();
        ms = stats.getMovementSpeed(player);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        uuid = UUID.fromString(ByteBufUtils.readUTF8String(buf));
        ms = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeUTF8String(buf, uuid.toString());
        buf.writeInt(ms);
    }

    public void handleServerSide() {
        ThirstMod.getProxy().getStatsByUUID(uuid).movementSpeed = ms;
    }

    public static class Handler implements IMessageHandler<PacketMovementSpeed, IMessage> {
        @Override
        public IMessage onMessage(PacketMovementSpeed message, MessageContext ctx) {
            message.handleServerSide();
            return null;
        }
    }
}
