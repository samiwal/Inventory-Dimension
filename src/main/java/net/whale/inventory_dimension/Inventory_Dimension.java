package net.whale.inventory_dimension;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.dimension.ModDimension;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.entity.util.MindEntityRenderer;
import net.whale.inventory_dimension.network.EnderChestSyncPacket;
import net.whale.inventory_dimension.network.NetworkHandler;

@Mod(Inventory_Dimension.MOD_ID)
public class Inventory_Dimension
{
    public static final String MOD_ID = "inventory_dimension";

    public Inventory_Dimension()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        ModDimension.register();
        NetworkHandler.register();
        ModEntities.register(modEventBus);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new CommonModEvents());
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
    public class CommonModEvents {
        @SubscribeEvent
        public void onPlayerJoin(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
            if(event.getEntity() instanceof ServerPlayer player) {
                NetworkHandler.INSTANCE.send(new EnderChestSyncPacket(player.getEnderChestInventory().getItems()),PacketDistributor.PLAYER.with(player));
            }
        }
        @SubscribeEvent
        public void onContainerClosed(PlayerContainerEvent.Close event) {
            if (event.getContainer() instanceof ChestMenu menu &&
                    event.getEntity() instanceof ServerPlayer player) {

                if (menu.getContainer() == player.getEnderChestInventory()) {
                    NetworkHandler.INSTANCE.send(new EnderChestSyncPacket(player.getEnderChestInventory().getItems()),PacketDistributor.PLAYER.with(player));
                }
            }
        }
    }
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class StaticModEvents {

        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(ModEntities.MIND_ENTITY.get(), MindEntity.createAttributes().build());
        }
    }
}
