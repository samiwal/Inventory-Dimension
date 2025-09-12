package net.whale.inventory_dimension.dimension;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.dimension.LevelStem;
import net.whale.inventory_dimension.Inventory_Dimension;

public class ModDimension {

    public static final ResourceKey<LevelStem> INVENTORY_LEVEL = ResourceKey.create(Registries.LEVEL_STEM,
            ResourceLocation.fromNamespaceAndPath(Inventory_Dimension.MOD_ID, "inventory_dimension"));
    public static final ResourceKey<Level> INVENTORY_DIMENSION = ResourceKey.create(
            Registries.DIMENSION, ResourceLocation.fromNamespaceAndPath(Inventory_Dimension.MOD_ID, "inventory_dimension"));

    public static void register() {}
}