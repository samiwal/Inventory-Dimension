package net.whale.inventory_dimension.item;

import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.whale.inventory_dimension.access.PlayerInterface;

public interface InventoryDimensionItem {
    void onEquip(PlayerInterface player, Slot slot, InventoryScreen screen);

    void onUnequip(PlayerInterface player);

    void onRemoved(PlayerInterface player);

    Item getTriggerItem();
}