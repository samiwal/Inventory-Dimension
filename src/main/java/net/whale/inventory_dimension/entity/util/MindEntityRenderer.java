package net.whale.inventory_dimension.entity.util;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.resources.ResourceLocation;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class MindEntityRenderer extends HumanoidMobRenderer<MindEntity, PlayerModel<MindEntity>> {
    private final PlayerModel<MindEntity> steveModel;
    private final PlayerModel<MindEntity> alexModel;
    private final SkinManager skinManager = Minecraft.getInstance().getSkinManager();
    private final ConcurrentMap<UUID, ResourceLocation> skinCache = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Boolean> slimCache = new ConcurrentHashMap<>();
    public MindEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), true), 0.5F);
        this.steveModel = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER), false);
        this.alexModel = new PlayerModel<>(ctx.bakeLayer(ModelLayers.PLAYER_SLIM), true);
    }
    @Override
    public void render(MindEntity entity, float entityYaw, float partialTicks, PoseStack poseStack,
                       MultiBufferSource bufferSource, int packedLight) {

        UUID uuid = entity.getPlayerUUID();
        if (uuid != null) {
            boolean slim = slimCache.getOrDefault(uuid, false);
            this.model = slim ? alexModel : steveModel;
        }

        super.render(entity, entityYaw, partialTicks, poseStack, bufferSource, packedLight);
    }
    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull MindEntity entity) {
        UUID uuid = entity.getPlayerUUID();
        if(uuid == null)return DefaultPlayerSkin.getDefaultTexture();
        return this.getOrLoadSkin(uuid);
    }
    private ResourceLocation getOrLoadSkin(UUID uuid) {
        if (skinCache.containsKey(uuid)) {
            return skinCache.get(uuid);
        }
        GameProfile profile = new GameProfile(uuid, "mind");

        CompletableFuture<PlayerSkin> future = skinManager.getOrLoad(profile);
        future.thenAccept(playerSkin -> {
            ResourceLocation skinLocation = playerSkin.texture();
            slimCache.put(uuid, PlayerSkin.Model.SLIM.equals(playerSkin.model()));
            skinCache.put(uuid, skinLocation);
        });
        return DefaultPlayerSkin.getDefaultTexture();
    }
}
