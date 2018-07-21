package com.thetorine.thirstmod.network;

import com.thetorine.thirstmod.common.blocks.TileEntityDrinksStore;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDrinksStore implements IMessage {

    private int drinkId;
    private BlockPos pos;

    public PacketDrinksStore() {}

    public PacketDrinksStore(TileEntityDrinksStore tile, int drinkId) {
        this.drinkId = drinkId;
        pos = tile.getPos();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        drinkId = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(drinkId);
        buf.writeInt(pos.getX());
        buf.writeInt(pos.getY());
        buf.writeInt(pos.getZ());
    }

    public void handleServerSide(EntityPlayer player) {
        World worldObj = player.getEntityWorld();
        TileEntityDrinksStore tile = (TileEntityDrinksStore) worldObj.getTileEntity(pos);
        if (tile != null) {
            tile.setField(0, drinkId);
        }
    }

    public static class Handler implements IMessageHandler<PacketDrinksStore, IMessage> {
        @Override
        public IMessage onMessage(PacketDrinksStore message, MessageContext ctx) {
            EntityPlayer player = ctx.getServerHandler().player;
            message.handleServerSide(player);
            return null;
        }
    }
}
