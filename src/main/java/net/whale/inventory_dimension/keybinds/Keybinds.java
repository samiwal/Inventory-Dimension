package net.whale.inventory_dimension.keybinds;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.whale.inventory_dimension.Inventory_Dimension;

public class Keybinds {
    public static final String KEY_CATEGORY_INVENTORY_DIMENSION = "key.categories." + Inventory_Dimension.MOD_ID;
    public static final KeyMapping CYCLEBLOCKSTATEPROPERTIES = new KeyMapping(
            "key." + Inventory_Dimension.MOD_ID + ".cycleblockstateproperties",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_F,
            KEY_CATEGORY_INVENTORY_DIMENSION
    );
    public static final KeyMapping CYCLEPROPERTYVALUE = new KeyMapping(
            "key." + Inventory_Dimension.MOD_ID + ".cyclepropertyvalue",
            InputConstants.Type.KEYSYM,
            InputConstants.KEY_R,
            KEY_CATEGORY_INVENTORY_DIMENSION
    );
}
