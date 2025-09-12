package net.whale.inventory_dimension.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.acess.PlayerInterface;

import java.util.UUID;

public class MyPacket {
    private final UUID msg;

    public MyPacket(UUID player) {
        this.msg = player;
    }
    public MyPacket(){
        this.msg = UUID.randomUUID();
    }

    public MyPacket(FriendlyByteBuf buf) {
        this.msg = buf.readUUID();
    }
    public static MyPacket decode(FriendlyByteBuf buf) {
        return new MyPacket(buf.readUUID());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(msg);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.getSender() == null) {
                System.out.println("Sender ist null!");
                return;
            }
            MindEntity mindentity = new MindEntity(ModEntities.MIND_ENTITY.get(), ctx.getSender().serverLevel());
            mindentity.setPlayerUUID(msg);
            ServerPlayer serverPlayer = ctx.getSender();
            mindentity.moveTo(serverPlayer.getX(), serverPlayer.getY() + 1, serverPlayer.getZ(), serverPlayer.getYRot(), serverPlayer.getXRot());
            ((PlayerInterface) serverPlayer).setInventory_Dimension$controlledEntity(mindentity);
            ctx.getSender().serverLevel().addFreshEntity(mindentity);
        });
        ctx.setPacketHandled(true);
    }
}