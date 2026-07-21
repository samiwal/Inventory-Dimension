package net.whale.inventory_dimension.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class ServerBoundOpenVirtualContainerPacket {
    private final Integer x;
    private final Integer y;
    private final Integer z;


    public ServerBoundOpenVirtualContainerPacket(BlockPos pos) {
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
    }

    public ServerBoundOpenVirtualContainerPacket(FriendlyByteBuf buf) {
        this.x = buf.readVarInt();
        this.y = buf.readVarInt();
        this.z = buf.readVarInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarInt(this.x);
        buffer.writeVarInt(this.y);
        buffer.writeVarInt(this.z);
    }

    public static void handle(ServerBoundOpenVirtualContainerPacket msg, CustomPayloadEvent.Context ctx) {
        ServerPlayer player = ctx.getSender();
        if (player == null) return;
        int virtualContainerId = 99;
        MenuProvider provider = null;
        if (provider == null) return;
        AbstractContainerMenu shadowMenu = provider.createMenu(virtualContainerId, player.getInventory(), player);
        if (shadowMenu == null) return;
        player.containerMenu = shadowMenu;
        player.initMenu(shadowMenu);
    }
}
