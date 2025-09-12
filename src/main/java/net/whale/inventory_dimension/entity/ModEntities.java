package net.whale.inventory_dimension.entity;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.whale.inventory_dimension.Inventory_Dimension;
import net.whale.inventory_dimension.entity.entities.MindEntity;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Inventory_Dimension.MOD_ID);
    public static final RegistryObject<EntityType<MindEntity>> MIND_ENTITY = ENTITY_TYPES.register("mind_entity",()-> EntityType.Builder.of(MindEntity::new, MobCategory.MISC)
            .sized(0.6F,1.8F)
            .build("mind_entity"));
    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
