package net.whale.inventory_dimension.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.save.SectionBlockStateDataProvider;

public class InventoryDimensionSyncPacket {
    private final PalettedContainer<BlockState> blockStates;

    public InventoryDimensionSyncPacket(PalettedContainer<BlockState> blockStates) {
        this.blockStates = blockStates.copy();
    }

    public InventoryDimensionSyncPacket(FriendlyByteBuf buf) {
        this.blockStates = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        this.blockStates.read(buf);
    }

    public void encode(FriendlyByteBuf buf) {
        this.blockStates.write(buf);
    }

    public static void handle(InventoryDimensionSyncPacket msg, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            if (ctx.isClientSide()) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player == null) return;
                ((PlayerInterface) player).inventoryDimension$setSectionBlockStates(msg.blockStates);
            } else {
                var sender = ctx.getSender();
                if (sender == null) return;
                sender.getCapability(SectionBlockStateDataProvider.DATA).ifPresent(data -> {
                    data.setSection(msg.blockStates);
                });
            }
        });
        ctx.setPacketHandled(true);
    }
}
