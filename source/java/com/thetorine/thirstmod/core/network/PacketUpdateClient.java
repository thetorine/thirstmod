package com.thetorine.thirstmod.core.network;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.player.ThirstLogic;

import cpw.mods.fml.common.network.simpleimpl.*;
import io.netty.buffer.ByteBuf;

public class PacketUpdateClient implements IMessage {
	private int level;
	private float saturation;
	private boolean poisoned;
	
	public PacketUpdateClient() {
	}

	public PacketUpdateClient(ThirstLogic stats) {
		this.level = stats.thirstLevel;
		this.saturation = stats.thirstSaturation;
		this.poisoned = stats.poisonLogic.isPlayerPoisoned();
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		level = buffer.readInt();
		saturation = buffer.readFloat();
		poisoned = buffer.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(level);
		buffer.writeFloat(saturation);
		buffer.writeBoolean(poisoned);		
	}

	public void handleClientSide() {
		ClientStats.getInstance().level = level;
		ClientStats.getInstance().saturation = saturation;
		ClientStats.getInstance().isPoisoned = poisoned;
	}
	
	public static class Handler implements IMessageHandler<PacketUpdateClient, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateClient message, MessageContext ctx) {
        	message.handleClientSide();
            return null;
        }
    }
}
