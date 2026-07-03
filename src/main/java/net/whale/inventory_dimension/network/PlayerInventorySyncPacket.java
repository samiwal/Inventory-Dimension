package net.whale.inventory_dimension.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class PlayerInventorySyncPacket {
    private final List<ItemStack> items;

    public PlayerInventorySyncPacket(Inventory inventory) {
        this.items = new ArrayList<>(inventory.getContainerSize());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            this.items.add(inventory.getItem(i).copy());
        }
    }

    public PlayerInventorySyncPacket(FriendlyByteBuf buf) {
        this.items = buf.readCollection(ArrayList::new,
                b -> ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) b));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(this.items,
                (b, stack) -> ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) b, stack));
    }

    public static void handle(PlayerInventorySyncPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null) return;
            for (int i = 0; i < msg.items.size(); i++) player.getInventory().setItem(i, msg.items.get(i));
        });
        ctx.setPacketHandled(true);
    }
}
