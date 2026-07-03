package net.whale.inventory_dimension.item.items;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.SectionPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.item.InventoryDimensionItem;
import net.whale.inventory_dimension.level.VirtualLevelChunkSection;
import net.whale.inventory_dimension.mixin.ChunkAccessAccessor;
import net.whale.inventory_dimension.network.EnderChestSyncPacket;
import net.whale.inventory_dimension.network.InventoryDimensionSyncPacket;
import net.whale.inventory_dimension.network.NetworkHandler;
import net.whale.inventory_dimension.update.UpdateLevel;

public class EnderChestInventoryItem implements InventoryDimensionItem {

    @Override
    public void onEquip(PlayerInterface player, Slot slot, InventoryScreen screen) {
        Minecraft mc = Minecraft.getInstance();
        SectionPos sectionPos = SectionPos.of(mc.player.blockPosition().above(32));
        if(sectionPos.maxBlockY() >= mc.level.getMaxBuildHeight()) {
            sectionPos = SectionPos.of(mc.player.blockPosition().atY(mc.level.getMaxBuildHeight() - 1));
        }
        MindEntity mind = new MindEntity(ModEntities.MIND_ENTITY.get(), mc.player.clientLevel, sectionPos);
        player.inventoryDimension$setControlledEntity(mind);
        mind.setPos(sectionPos.origin().getX() + 8, sectionPos.origin().getY() + 5, sectionPos.origin().getZ() + 3);
        mc.player.clientLevel.addEntity(mind);
        mc.setCameraEntity(mind);
    }

    @Override
    public void onUnequip(PlayerInterface player) {
        removeMind(player);
    }

    @Override
    public void onRemoved(PlayerInterface player) {
        removeMind(player);
    }

    @Override
    public Item getTriggerItem() {
        return Items.ENDER_CHEST;
    }

    private void removeMind(PlayerInterface player) {
        if (player.inventoryDimension$hasControlledEntity()) {
            Minecraft mc = Minecraft.getInstance();
            MindEntity entity = player.inventoryDimension$getControlledEntity();
            resetSection(mc,entity);
            NetworkHandler.INSTANCE.send(new EnderChestSyncPacket(((LocalPlayer) player).getEnderChestInventory()), PacketDistributor.SERVER.noArg());
            NetworkHandler.INSTANCE.send(new InventoryDimensionSyncPacket(player.inventoryDimension$getSectionBlockStates()),PacketDistributor.SERVER.noArg());
            player.inventoryDimension$setControlledEntity(null);
            mc.player.clientLevel.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
            mc.setCameraEntity(mc.player);
        }
    }
    private void resetSection(Minecraft mc,MindEntity entity) {
        ChunkAccess chunk = mc.level.getChunk(entity.sectionPos.getX(), entity.sectionPos.getZ());
        int i = chunk.getSectionIndexFromSectionY(entity.sectionPos.getY());
        LevelChunkSection[] sections = ((ChunkAccessAccessor) chunk).getSections();
        sections[i] = ((VirtualLevelChunkSection) sections[i]).getRealSection();
        UpdateLevel.updateSection(entity.sectionPos,mc.level,mc.levelRenderer);
    }
}
