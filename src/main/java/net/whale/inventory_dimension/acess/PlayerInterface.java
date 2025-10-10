package net.whale.inventory_dimension.acess;

import net.whale.inventory_dimension.entity.entities.MindEntity;

public interface PlayerInterface {
    void setInventory_Dimension$controlledEntity(MindEntity entity);
    MindEntity getInventory_Dimension$controlledEntity();
    boolean hasInventory_Dimension$controlledEntity();
    void setInventoryDimension$entityItems(int items);
    int getInventory_Dimension$entityItems();
    void setInventoryDimension$isOnItemSlot(boolean isOnItemSlot);
    boolean getInventoryDimension$isOnItemSlot();
    void setInventory_Dimension$itemSlotNumber(int number);
    int getInventory_Dimension$itemSlotNumber();
    void setInventoryDimension$isDraggingPlayerModel(boolean dragging);
    boolean getInventoryDimension$isDraggingPlayerModel();
}
