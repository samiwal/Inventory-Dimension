package net.whale.inventory_dimension.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.whale.inventory_dimension.Inventory_Dimension;

public class NetworkHandler {

    private static final Integer PROTOCOL_VERSION = 1;
    public static final SimpleChannel INSTANCE = ChannelBuilder
            .named(ResourceLocation.fromNamespaceAndPath(Inventory_Dimension.MOD_ID,"main_channel"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .simpleChannel();
    public static void register() {
        INSTANCE.messageBuilder(EnderChestSyncPacket.class)
                .encoder(EnderChestSyncPacket::encode)
                .decoder(EnderChestSyncPacket::new)
                .consumerMainThread(EnderChestSyncPacket::handle)
                .add();
    }
}