package net.whale.inventory_dimension.block;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "inventory_dimension");

    public static final RegistryObject<Block> ENDER_CHEST_ROOM =
            BLOCKS.register("ender_chest_room", () -> new Block(BlockBehaviour.Properties.of()));
}
