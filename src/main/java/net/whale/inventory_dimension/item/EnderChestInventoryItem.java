package net.whale.inventory_dimension.item;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;

public class EnderChestInventoryItem implements InventoryDimensionItem {

    @Override
    public void onEquip(PlayerInterface player, Slot slot, InventoryScreen screen) {
        Minecraft mc = Minecraft.getInstance();
        MindEntity mind = new MindEntity(ModEntities.MIND_ENTITY.get(), mc.player.clientLevel);
        player.inventoryDimension$setControlledEntity(mind);
        BlockPos playerPos = mc.player.blockPosition();
        int subX = Math.floorDiv(playerPos.getX(), 16) * 16;
        int subY = Math.floorDiv(playerPos.getY()+32, 16) * 16;
        int subZ = Math.floorDiv(playerPos.getZ(), 16) * 16;
        mind.setMindPosition(new BlockPos(subX, subY, subZ));
        mind.setPos(subX+8,subY+7,subZ+8);
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
            MindEntity entity = player.inventoryDimension$getControlledEntity();
            player.inventoryDimension$setControlledEntity(null);
            Minecraft.getInstance().player.clientLevel.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
            Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
        }
    }
}
