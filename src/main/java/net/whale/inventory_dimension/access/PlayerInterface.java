package net.whale.inventory_dimension.access;

import net.minecraft.world.item.Item;
import net.whale.inventory_dimension.entity.entities.MindEntity;

public interface PlayerInterface {
    void inventoryDimension$setControlledEntity(MindEntity entity);
    MindEntity inventoryDimension$getControlledEntity();
    boolean inventoryDimension$hasControlledEntity();

    void inventoryDimension$setEntityItems(int items);
    int inventoryDimension$getEntityItems();

    void inventoryDimension$setIsOnItemSlot(boolean isOnItemSlot);
    boolean inventoryDimension$getIsOnItemSlot();

    void inventoryDimension$setItemSlotNumber(int number);
    int inventoryDimension$getItemSlotNumber();

    void inventoryDimension$setIsDraggingPlayerModel(boolean dragging);
    boolean inventoryDimension$getIsDraggingPlayerModel();

    void inventoryDimension$setActiveItem(Item item);
    Item inventoryDimension$getActiveItem();
}
