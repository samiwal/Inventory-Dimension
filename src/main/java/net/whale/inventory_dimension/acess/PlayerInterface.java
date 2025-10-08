package net.whale.inventory_dimension.acess;

import net.whale.inventory_dimension.entity.entities.MindEntity;

public interface PlayerInterface {
    void setInventory_Dimension$controlledEntity(MindEntity entity);
    MindEntity getInventory_Dimension$controlledEntity();
    boolean hasInventory_Dimension$controlledEntity();
    void setInventoryDimension$entityItems(int items);
    int getInventory_Dimension$entityItems();
}
