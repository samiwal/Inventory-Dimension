package net.whale.inventory_dimension.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.whale.inventory_dimension.access.PlayerInterface;

import java.util.ArrayList;
import java.util.List;

public class EnderChestSyncPacket {
    private static final org.slf4j.Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EnderChestSyncPacket.class);
    private final List<ItemStack> items;

    public EnderChestSyncPacket(List<ItemStack> items) {
        this.items = items;
    }

    public EnderChestSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.items = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            CompoundTag tag = buf.readNbt();
            if (tag == null) {
                this.items.add(ItemStack.EMPTY);
                continue;
            }
            ItemStack stack = ItemStack.OPTIONAL_CODEC
                    .parse(NbtOps.INSTANCE, tag)
                    .resultOrPartial(err -> LOGGER.error("ItemStack decode error: {}", err))
                    .orElse(ItemStack.EMPTY);
            this.items.add(stack);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(items.size());
        for (ItemStack stack : items) {
            CompoundTag tag = ItemStack.OPTIONAL_CODEC
                    .encodeStart(NbtOps.INSTANCE, stack)
                    .resultOrPartial(err -> LOGGER.error("ItemStack encode error: {}", err))
                    .map(t -> (CompoundTag) t)
                    .orElse(new CompoundTag());
            buf.writeNbt(tag);
        }
    }

    public static void handle(EnderChestSyncPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null) return;
            int j = 0;
            for (ItemStack item : msg.items) {
                if (item.getItem() instanceof BlockItem) {
                    player.getEnderChestInventory().setItem(j, item);
                    j++;
                }
            }
            player.getEnderChestInventory().setItem(j, ItemStack.EMPTY);
            ((PlayerInterface) player).inventoryDimension$setEntityItems(j);
        });
        ctx.setPacketHandled(true);
    }
}