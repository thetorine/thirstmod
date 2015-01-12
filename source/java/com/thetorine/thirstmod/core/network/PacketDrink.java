package com.thetorine.thirstmod.core.network;

import com.thetorine.thirstmod.core.content.blocks.TileEntityDS;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;

public class PacketDrink implements IMessage {

	private int page, amount, buy, x, y, z;
	
	public PacketDrink() {
	}

	public PacketDrink(int page, int amount, int buy, int x, int y, int z) {
		this.page = page;
		this.amount = amount;
		this.buy = buy;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buffer) {
		page = buffer.readInt();
		amount = buffer.readInt();
		buy = buffer.readInt();
		x = buffer.readInt();
		y = buffer.readInt();
		z = buffer.readInt();
	}

	@Override
	public void toBytes(ByteBuf buffer) {
		buffer.writeInt(page);
		buffer.writeInt(amount);
		buffer.writeInt(buy);
		buffer.writeInt(x);
		buffer.writeInt(y);
		buffer.writeInt(z);
	}

	public void handleServerSide(EntityPlayer player) {
		TileEntityDS tile = (TileEntityDS) player.worldObj.getTileEntity(x, y, z);
		if (tile != null) {
			tile.page = page;
			tile.amountToBuy = amount;
			tile.canBuy = buy;
		}
	}
	
	public static class Handler implements IMessageHandler<PacketDrink, IMessage> {
        @Override
        public IMessage onMessage(PacketDrink message, MessageContext ctx) {
        	EntityPlayer player = ctx.getServerHandler().playerEntity;
        	message.handleServerSide(player);
            return null;
        }
    }
}
