package net.whale.inventory_dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.dimension.ModDimension;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.entity.util.MindEntityRenderer;
import net.whale.inventory_dimension.network.NetworkHandler;

@Mod(Inventory_Dimension.MOD_ID)
public class Inventory_Dimension
{
    public static final String MOD_ID = "inventory_dimension";

    public Inventory_Dimension()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);
        ModDimension.register();
        ModEntities.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);

        modEventBus.addListener(this::addCreative);

    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        NetworkHandler.register();
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            EntityRenderers.register(ModEntities.MIND_ENTITY.get(), MindEntityRenderer::new);
        }
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(ModEntities.MIND_ENTITY.get(), MindEntity.createAttributes().build());
        }
    }
    //@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = MOD_ID)
    //public class ClientEvents {
    //    @SubscribeEvent
    //    public static void onClientTick(TickEvent.ClientTickEvent event) {
    //        if (event.phase != TickEvent.Phase.END) return;
    //        Minecraft mc = Minecraft.getInstance();
//
    //        if (mc.player == null || mc.level == null || mc.isPaused()) return;
//
    //        float forward = mc.player.zza; // vor/zurück
    //        float strafe = mc.player.xxa;  // rechts/links
    //        boolean jumping = mc.player.input.jumping;
    //        float pitch = mc.player.getXRot();
    //        float yaw = mc.player.getYRot();
//
    //        NetworkHandler.INSTANCE.send(new MindControlMovePacket(forward, strafe, jumping, pitch, yaw), PacketDistributor.SERVER.noArg());
    //    }
    //}
}
