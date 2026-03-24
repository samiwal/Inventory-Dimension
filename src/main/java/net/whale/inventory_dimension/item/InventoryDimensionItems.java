package net.whale.inventory_dimension.item;

import net.minecraft.world.item.Item;
import java.util.HashMap;
import java.util.Map;

public class InventoryDimensionItems {
    private static final Map<Item, InventoryDimensionItem> REGISTRY = new HashMap<>();

    public static void register(InventoryDimensionItem handler) {
        REGISTRY.put(handler.getTriggerItem(), handler);
    }

    public static InventoryDimensionItem get(Item item) {
        return REGISTRY.get(item);
    }

    public static boolean has(Item item) {
        return REGISTRY.containsKey(item);
    }
}
