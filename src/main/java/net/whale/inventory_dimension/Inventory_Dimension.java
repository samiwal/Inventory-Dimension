package net.whale.inventory_dimension;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.block.ModBlocks;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.item.InventoryDimensionItems;
import net.whale.inventory_dimension.item.items.EnderChestInventoryItem;
import net.whale.inventory_dimension.keybinds.Keybinds;
import net.whale.inventory_dimension.network.EnderChestSyncPacket;
import net.whale.inventory_dimension.network.InventoryDimensionSyncPacket;
import net.whale.inventory_dimension.network.NetworkHandler;
import net.whale.inventory_dimension.render.BlockRenderState;
import net.whale.inventory_dimension.render.MindEntityRenderer;
import net.whale.inventory_dimension.save.SectionBlockStateDataProvider;

@Mod(Inventory_Dimension.MOD_ID)
public class Inventory_Dimension {
    public static final String MOD_ID = "inventory_dimension";

    public Inventory_Dimension() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        NetworkHandler.register();
        ModEntities.register(modEventBus);
        InventoryDimensionItems.register(new EnderChestInventoryItem());
        ModBlocks.BLOCKS.register(modEventBus);
    }
    //MOD
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class StaticModEvents {
        @SubscribeEvent
        public static void onEntityAttributeCreation(EntityAttributeCreationEvent event) {
            event.put(ModEntities.MIND_ENTITY.get(), MindEntity.createAttributes().build());
        }
    }
    //MOD,CLIENT
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.MIND_ENTITY.get(), MindEntityRenderer::new);
        }
        @SubscribeEvent
        public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
            event.register(Keybinds.CYCLEBLOCKSTATEPROPERTIES);
            event.register(Keybinds.CYCLEPROPERTYVALUE);
        }
    }
    //FORGE
    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonModEvents {
        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                syncClient(player);
            }
        }
        @SubscribeEvent
        public static void onDimensionChange(PlayerEvent.PlayerChangedDimensionEvent event) {
            // wichtig da der Spieler Clientseitig bei dimensionswechsel jedesmal komplett neu aufgebaut wird
            if (event.getEntity() instanceof ServerPlayer player) {
                syncClient(player);
            }
        }

        @SubscribeEvent
        public static void onContainerClosed(PlayerContainerEvent.Close event) {
            if (event.getContainer() instanceof ChestMenu menu &&
                    event.getEntity() instanceof ServerPlayer player &&
                    menu.getContainer() == player.getEnderChestInventory()) {
                NetworkHandler.INSTANCE.send(
                        new EnderChestSyncPacket(player.getEnderChestInventory()),
                        PacketDistributor.PLAYER.with(player)
                );
            }
        }
        private static void syncClient(ServerPlayer player){
            NetworkHandler.INSTANCE.send(
                    new EnderChestSyncPacket(player.getEnderChestInventory()),
                    PacketDistributor.PLAYER.with(player)
            );
            player.getCapability(SectionBlockStateDataProvider.DATA).ifPresent(data ->
                NetworkHandler.INSTANCE.send(
                        new InventoryDimensionSyncPacket(data.getSection()),
                        PacketDistributor.PLAYER.with(player)));
        }
    }
    //FORGE,CLIENT
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ScreenEvents {
        @SubscribeEvent
        public static void onScreenOpen(ScreenEvent.Opening event) {
            if (event.getScreen() instanceof PauseScreen) {
                LocalPlayer player = Minecraft.getInstance().player;
                if (player != null && ((PlayerInterface) player).inventoryDimension$hasControlledEntity()) {
                    NetworkHandler.INSTANCE.send(new EnderChestSyncPacket(player.getEnderChestInventory()), PacketDistributor.SERVER.noArg());
                    NetworkHandler.INSTANCE.send(new InventoryDimensionSyncPacket(((PlayerInterface) player).inventoryDimension$getSectionBlockStates()),PacketDistributor.SERVER.noArg());
                }
            }
        }
        @SubscribeEvent
        public static void onKeyInput(InputEvent.Key event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player == null || !((PlayerInterface) player).inventoryDimension$hasControlledEntity()) return;
            if (Keybinds.CYCLEBLOCKSTATEPROPERTIES.consumeClick()) {
                BlockRenderState.cycleThroughProperties(Screen.hasShiftDown());
            }
            if (Keybinds.CYCLEPROPERTYVALUE.consumeClick()) {
                BlockRenderState.cycleThroughPropertyValues(Screen.hasShiftDown());
            }
        }
    }
    //NONE?
    @Mod.EventBusSubscriber(modid = MOD_ID)
    public static class PlayerChunkDataEventHandler {
        private static final ResourceLocation CAPABILITY_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "data");
        @SubscribeEvent
        public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
            if (event.getObject() instanceof Player) {
                event.addCapability(CAPABILITY_ID, new SectionBlockStateDataProvider());
            }
        }
        @SubscribeEvent
        public static void onPlayerClone(PlayerEvent.Clone event) {
            Player oldPlayer = event.getOriginal();
            Player newPlayer = event.getEntity();
            oldPlayer.reviveCaps();

            oldPlayer.getCapability(SectionBlockStateDataProvider.DATA).ifPresent(oldData -> {
                newPlayer.getCapability(SectionBlockStateDataProvider.DATA).ifPresent(newData -> {
                    newData.setSection(oldData.getSection());
                });
            });
            oldPlayer.invalidateCaps();
        }
        @SubscribeEvent
        public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
            if (event.getEntity() instanceof ServerPlayer player) {
                CommonModEvents.syncClient(player);
            }
        }
    }
}