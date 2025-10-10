package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.acess.RenderInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {

    @Inject(method = "handleKeybinds", at = @At("HEAD"), cancellable = true)
    private void onHandleKeybinds(CallbackInfo ci) {
        Minecraft mc = (Minecraft) (Object) this;
        if(((PlayerInterface) mc.player).hasInventory_Dimension$controlledEntity()) {
            MindEntity entity = ((PlayerInterface) mc.player).getInventory_Dimension$controlledEntity();
            if (mc.options.keyAttack.consumeClick() || mc.options.keyAttack.isDown()) {
                entity.deleteBlockIfPossible();
                ci.cancel();
            }
            if (mc.options.keyUse.consumeClick()|| mc.options.keyUse.isDown()) {
                entity.placeBlockIfPossible();
                ci.cancel();
            }
            if (mc.options.keyPickItem.consumeClick() || mc.options.keyPickItem.isDown()) {
                entity.selectBlockIfPossible();
                ci.cancel();
            }
            if (mc.options.keySwapOffhand.consumeClick()) {
                (((RenderInterface) Minecraft.getInstance().gameRenderer.itemInHandRenderer)).setInventory_Dimension$Swap();
                ci.cancel();
            }if(mc.options.keySwapOffhand.isDown()) ci.cancel();
            if (mc.options.keyDrop.consumeClick()) {
                ((RenderInterface) Minecraft.getInstance().gameRenderer.itemInHandRenderer).setInventory_Dimension$Drop();
                ci.cancel();
            }if(mc.options.keyDrop.isDown()) ci.cancel();
            if (mc.options.keySprint.consumeClick()) ci.cancel();
            if(mc.options.keySprint.isDown()) ci.cancel();
        }
    }
}
