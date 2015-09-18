package com.thetorine.thirstmod.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.thetorine.thirstmod.core.client.player.ClientStats;
import com.thetorine.thirstmod.core.player.ThirstLogic;
import com.thetorine.thirstmod.core.utils.Constants;

public class PacketUpdateClient implements IMessage {

	private int level;
	private float saturation;
	private boolean poisoned;
	private float temperature;
	
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
		
		if(Constants.ECLIPSE_ENVIRONMENT) {
			temperature = buffer.readFloat();
		}
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(level);
		buffer.writeFloat(saturation);
		buffer.writeBoolean(poisoned);		
		
		if(Constants.ECLIPSE_ENVIRONMENT) {
			buffer.writeFloat(temperature);
		}
	}

	public void handleClientSide() {
		ClientStats.getInstance().level = level;
		ClientStats.getInstance().saturation = saturation;
		ClientStats.getInstance().isPoisoned = poisoned;
		
		if(Constants.ECLIPSE_ENVIRONMENT) {
			ClientStats.getInstance().temperature = temperature;
		}
	}
	
	public static class Handler implements IMessageHandler<PacketUpdateClient, IMessage> {
        @Override
        public IMessage onMessage(PacketUpdateClient message, MessageContext ctx) {
        	message.handleClientSide();
            return null;
        }
    }
}
