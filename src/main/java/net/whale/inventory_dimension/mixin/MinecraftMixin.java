package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import net.whale.inventory_dimension.render.BlockRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void onHandleKeybinds(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player == null || !((PlayerInterface) mc.player).inventoryDimension$hasControlledEntity()) return;

        MindEntity entity = ((PlayerInterface) mc.player).inventoryDimension$getControlledEntity();
        boolean sprint = mc.options.keySprint.isDown();

        if (mc.options.keyAttack.consumeClick() || mc.options.keyAttack.isDown()) { entity.deleteBlockIfPossible(); ci.cancel(); }
        if (mc.options.keyUse.consumeClick() || mc.options.keyUse.isDown()) { entity.placeBlockIfPossible(); ci.cancel(); }
        if (mc.options.keyPickItem.consumeClick()) { entity.selectBlockIfPossible(); ci.cancel(); }
        if (mc.options.keySwapOffhand.consumeClick()) {
            if (mc.options.keySwapOffhand.isDown()) BlockRenderState.swapSelectedProperty(sprint);
            ci.cancel();
        }
        if (mc.options.keyDrop.consumeClick()) {
            if (mc.options.keyDrop.isDown()) BlockRenderState.cycleSelectedProperty(sprint);
            ci.cancel();
        }
        if (mc.options.keySprint.consumeClick()) ci.cancel();
        if (mc.options.keyJump.consumeClick()) ci.cancel();
    }
}
