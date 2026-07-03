package net.whale.inventory_dimension.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class EnderChestSyncPacket {
    private final List<ItemStack> items;

    public EnderChestSyncPacket(PlayerEnderChestContainer container) {
        this.items = new ArrayList<>(container.getContainerSize());
        for (int i = 0; i < container.getContainerSize(); i++) {
            this.items.add(container.getItem(i).copy());
        }
    }

    public EnderChestSyncPacket(FriendlyByteBuf buf) {
        this.items = buf.readCollection(ArrayList::new,
                b -> ItemStack.OPTIONAL_STREAM_CODEC.decode((RegistryFriendlyByteBuf) b));
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeCollection(this.items,
                (b, stack) -> ItemStack.OPTIONAL_STREAM_CODEC.encode((RegistryFriendlyByteBuf) b, stack));
    }

    public static void handle(EnderChestSyncPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            Player player;
            if (ctx.isClientSide()) {
                player = Minecraft.getInstance().player;
            } else {
                player = ctx.getSender();
            }
            if (player == null) return;
            for (int i = 0; i < msg.items.size(); i++) player.getEnderChestInventory().setItem(i, msg.items.get(i));
        });
        ctx.setPacketHandled(true);
    }
}