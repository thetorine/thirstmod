package com.thetorine.thirstmod.common.network;

import com.thetorine.thirstmod.common.main.ThirstMod;
import com.thetorine.thirstmod.common.utils.ThirstUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.entity.player.EntityPlayer;

public class PacketConfigs implements IMessage {

	private boolean b1, b2, b3, b4;
	private float f1, f2, f3, f4, f5, f6;
	
	public PacketConfigs() {
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		b1 = buffer.readBoolean();
		b2 = buffer.readBoolean();
		b3 = buffer.readBoolean();
		b4 = buffer.readBoolean();

		f1 = buffer.readFloat();
		f2 = buffer.readFloat();
		f3 = buffer.readFloat();
		f4 = buffer.readFloat();
		f5 = buffer.readFloat();
		f6 = buffer.readFloat();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeBoolean(ThirstMod.CONFIG.POISON_ON);
		buffer.writeBoolean(ThirstMod.CONFIG.PEACEFUL_ON);
		buffer.writeBoolean(ThirstMod.CONFIG.PERMIT_MOD_ON);
		buffer.writeBoolean(ThirstMod.CONFIG.HEALTH_REGEN_OFF);

		buffer.writeFloat(ThirstMod.CONFIG.NIGHT_RATE);
		buffer.writeFloat(ThirstMod.CONFIG.IN_WATER_RATE);
		buffer.writeFloat(ThirstMod.CONFIG.DESERT_RATE);
		buffer.writeFloat(ThirstMod.CONFIG.WALKING_RATE);
		buffer.writeFloat(ThirstMod.CONFIG.RUNNING_RATE);
		buffer.writeFloat(ThirstMod.CONFIG.JUMP_RATE);
	}

	public void handleClientSide() {
		ThirstMod.CONFIG.POISON_ON = b1;
		ThirstMod.CONFIG.PEACEFUL_ON = b2;
		ThirstMod.CONFIG.PERMIT_MOD_ON = b3;
		ThirstMod.CONFIG.HEALTH_REGEN_OFF = b4;

		ThirstMod.CONFIG.NIGHT_RATE = f1;
		ThirstMod.CONFIG.IN_WATER_RATE = f2;
		ThirstMod.CONFIG.DESERT_RATE = f3;
		ThirstMod.CONFIG.WALKING_RATE = f4;
		ThirstMod.CONFIG.RUNNING_RATE = f5;
		ThirstMod.CONFIG.JUMP_RATE = f6;
	}
	
	public static class Handler implements IMessageHandler<PacketConfigs, IMessage> {
        @Override
        public IMessage onMessage(PacketConfigs message, MessageContext ctx) {
        	message.handleClientSide();
            return null;
        }
    }
}
