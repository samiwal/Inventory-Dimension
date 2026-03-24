package net.whale.inventory_dimension.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.whale.inventory_dimension.access.PlayerInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRenderMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void replaceItemStack(LivingEntity p_270072_, ItemStack p_270793_, ItemDisplayContext p_270837_,
                                  boolean p_270203_, PoseStack p_270974_, MultiBufferSource p_270686_, int p_270103_, CallbackInfo ci) {
        if (((PlayerInterface) Minecraft.getInstance().player).inventoryDimension$hasControlledEntity()) {
            ci.cancel();
        }
    }

    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void renderPlayerHands(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_,
                                   float p_109350_, float p_109351_, HumanoidArm p_109352_, CallbackInfo ci) {
        if (((PlayerInterface) Minecraft.getInstance().player).inventoryDimension$hasControlledEntity()) {
            ci.cancel();
        }
    }
}