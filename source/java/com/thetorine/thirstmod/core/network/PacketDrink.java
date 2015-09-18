package com.thetorine.thirstmod.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import com.thetorine.thirstmod.core.content.blocks.TileEntityDS;

public class PacketDrink implements IMessage {

	private int page, amount, buy, x, y, z;
	
	public PacketDrink() {
	}

	public PacketDrink(TileEntityDS tile) {
		this.page = tile.page;
		this.amount = tile.amountToBuy;
		this.buy = tile.canBuy;
		this.x = tile.getPos().getX();
		this.y = tile.getPos().getY();
		this.z = tile.getPos().getZ();
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
		TileEntityDS tile = (TileEntityDS) player.worldObj.getTileEntity(new BlockPos(x, y, z));
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
