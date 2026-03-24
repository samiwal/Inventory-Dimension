package net.whale.inventory_dimension.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.block.ModBlocks;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.render.BlockRenderState;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Inject(method = "renderLevel", at = @At("TAIL"))
    private void onRenderLevel(
            DeltaTracker p_342180_,
            boolean p_109603_,
            Camera p_109604_,
            GameRenderer p_109605_,
            LightTexture p_109606_,
            Matrix4f p_254120_,
            Matrix4f p_330527_,
            CallbackInfo ci
    ) {
        Vec3 camPos = p_109604_.getPosition();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        PlayerInterface player = (PlayerInterface) mc.player;
        if (!player.inventoryDimension$hasControlledEntity()) return;

        MindEntity entity = player.inventoryDimension$getControlledEntity();

        MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
        BlockPos startPos = entity.getMindPosition();

        PoseStack roomPose = new PoseStack();
        roomPose.last().pose().mul(p_254120_);
        roomPose.pushPose();
        roomPose.translate(
                startPos.getX() - camPos.x(),
                startPos.getY() - camPos.y(),
                startPos.getZ() - camPos.z()
        );
        roomPose.scale(16f, 16f, 16f);

        mc.getBlockRenderer().renderSingleBlock(
                ModBlocks.ENDER_CHEST_ROOM.get().defaultBlockState(),
                roomPose,
                buffer,
                15728880/2,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.cutout()
        );
        roomPose.popPose();
        buffer.endBatch();

        if (entity.getEchestitem() instanceof BlockItem blockItem) {

            BlockPos pos = entity.renderBlockPos;
            if (pos == null) {return;} ;

            if (BlockRenderState.state.getBlock().asItem() != blockItem) {
                BlockRenderState.state = blockItem.getBlock().defaultBlockState();
                BlockRenderState.selectedPropertyIndex = 0;
                BlockRenderState.selectedProperty = null;
            }

            PoseStack pose = new PoseStack();
            pose.last().pose().mul(p_254120_);
            pose.pushPose();
            pose.translate(
                    pos.getX() - camPos.x(),
                    pos.getY() - camPos.y(),
                    pos.getZ() - camPos.z()
            );

            buffer = mc.renderBuffers().bufferSource();

            Block block = blockItem.getBlock();
            if (block instanceof EntityBlock entityBlock) {
                BlockEntity be = entityBlock.newBlockEntity(pos, BlockRenderState.state);
                if (be != null) {
                    be.setLevel(entity.level());
                    mc.getBlockEntityRenderDispatcher().render(
                            be, p_342180_.getGameTimeDeltaPartialTick(true), pose, buffer
                    );
                    pose.popPose();
                    buffer.endBatch();
                    return;
                }
            }

            mc.getBlockRenderer().renderSingleBlock(
                    BlockRenderState.state, pose, buffer,
                    15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent()
            );
            pose.popPose();
            buffer.endBatch();
        }
    }
}