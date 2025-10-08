package net.whale.inventory_dimension.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.acess.RenderInterface;
import net.whale.inventory_dimension.util.BlockStateSwitcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRenderMixin implements RenderInterface {
    @Unique
    private BlockState inventory_Dimension$state = Blocks.DIRT.defaultBlockState();
    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void replaceItemStack(LivingEntity p_270072_, ItemStack p_270793_, ItemDisplayContext p_270837_, boolean p_270203_, PoseStack p_270974_, MultiBufferSource p_270686_, int p_270103_, CallbackInfo ci) {
        PlayerInterface player = (PlayerInterface) Minecraft.getInstance().player;
        if(player.hasInventory_Dimension$controlledEntity()){
                inventory_Dimension$renderBlockOnGround(player,p_270686_);
                ci.cancel();
        }
    }
    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void renderPlayerHands(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_, CallbackInfo ci) {
        PlayerInterface player = (PlayerInterface) Minecraft.getInstance().player;
        if (player.hasInventory_Dimension$controlledEntity()) {
            inventory_Dimension$renderBlockOnGround(player,p_109348_);
            ci.cancel();
        }
    }
    @Unique
    private void inventory_Dimension$renderBlockOnGround(PlayerInterface player, MultiBufferSource buffer){
        BlockPos pos = player.getInventory_Dimension$controlledEntity().blockPos;
        if(player.getInventory_Dimension$controlledEntity().echestitem instanceof BlockItem blockItem) {
            if(this.inventory_Dimension$state.getBlock().asItem() != blockItem) this.inventory_Dimension$state = blockItem.getBlock().defaultBlockState();
            PoseStack pose = new PoseStack();
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            Block block = blockItem.getBlock();
            pose.translate(pos.getX() - camPos.x(), pos.getY() - camPos.y(), pos.getZ() - camPos.z());
            if (block instanceof DoorBlock) {
                if (inventory_Dimension$state.getValue(DoorBlock.HALF) == DoubleBlockHalf.LOWER) {
                    if(Minecraft.getInstance().level.getBlockState(pos.above()) != Blocks.AIR.defaultBlockState()) return;
                    BlockState upper = inventory_Dimension$state.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER);
                    pose.translate(0, 1, 0);
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(upper, pose, buffer, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
                    pose.translate(0, -1, 0);
                }
            }
            else if (block instanceof DoublePlantBlock || block instanceof TallFlowerBlock|| block instanceof TallSeagrassBlock) {
                if (inventory_Dimension$state.hasProperty(DoublePlantBlock.HALF) && inventory_Dimension$state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.LOWER) {
                    if(Minecraft.getInstance().level.getBlockState(pos.above()) != Blocks.AIR.defaultBlockState()) return;
                    BlockState upper = inventory_Dimension$state.setValue(DoublePlantBlock.HALF, DoubleBlockHalf.UPPER);
                    pose.translate(0, 1, 0);
                    Minecraft.getInstance().getBlockRenderer().renderSingleBlock(upper, pose, buffer, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
                    pose.translate(0, -1, 0);
                }
            }
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(inventory_Dimension$state, pose, buffer, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
            pose.popPose();
        }
    }
    @Override
    public void Inventory_Dimension$setDrop() {inventory_Dimension$state = new BlockStateSwitcher().togglePropertyUpDown(inventory_Dimension$state);}
    @Override
    public void Inventory_Dimension$setSwap() {inventory_Dimension$state = new BlockStateSwitcher().rotatePropertyY(inventory_Dimension$state);}
    @Override
    public void Inventory_Dimension$setSprint() {inventory_Dimension$state = new BlockStateSwitcher().toggleOtherVisualProperties(inventory_Dimension$state);}
}
