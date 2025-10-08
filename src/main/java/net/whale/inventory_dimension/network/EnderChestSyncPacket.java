package net.whale.inventory_dimension.network;

import com.mojang.serialization.DataResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.BedItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.whale.inventory_dimension.acess.PlayerInterface;

import java.util.ArrayList;
import java.util.List;

public class EnderChestSyncPacket {
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
            } else {
                ItemStack stack;
                try {
                    DataResult<ItemStack> res = ItemStack.OPTIONAL_CODEC.parse(NbtOps.INSTANCE, tag);
                    stack = res.resultOrPartial(err -> System.err.println("ItemStack decode error: " + err)).orElse(ItemStack.EMPTY);
                } catch (Exception e) {
                    e.printStackTrace();
                    stack = ItemStack.EMPTY;
                }
                this.items.add(stack);
            }
        }
    }
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(items.size());
        for (ItemStack stack : items) {
            try {
                DataResult<Tag> encoded = ItemStack.OPTIONAL_CODEC.encodeStart(NbtOps.INSTANCE, stack);
                CompoundTag tag = encoded.resultOrPartial(err -> System.err.println("ItemStack encode error: " + err))
                        .map(t -> (CompoundTag) t)
                        .orElse(new CompoundTag());
                buf.writeNbt(tag);
            } catch (Exception e) {
                e.printStackTrace();
                buf.writeNbt(new CompoundTag());
            }
        }
    }

    public static void handle(EnderChestSyncPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                List<ItemStack> items = msg.items;
                int j = 0;
                for (ItemStack item : items) {
                    if (item.getItem() instanceof BlockItem blockItem && !(blockItem instanceof BedItem)) {
                        player.getEnderChestInventory().setItem(j, item);
                        j++;
                    }
                }
                player.getEnderChestInventory().setItem(j,ItemStack.EMPTY);
                ((PlayerInterface) player).setInventoryDimension$entityItems(j);

            }
        });
        ctx.setPacketHandled(true);
    }
}