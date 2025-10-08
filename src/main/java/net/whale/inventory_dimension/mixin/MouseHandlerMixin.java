package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public abstract class MouseHandlerMixin {

    @Inject(method = "onScroll", at = @At("HEAD"), cancellable = true)
    private void onScroll(long window, double xOffset, double yOffset, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (((PlayerInterface) mc.player).hasInventory_Dimension$controlledEntity()) {
            MindEntity entity = ((PlayerInterface) mc.player).getInventory_Dimension$controlledEntity();

            if (yOffset > 0) {
                entity.onScroll(true);
            } else if (yOffset < 0) {
                entity.onScroll(false);
            }
            ci.cancel();
        }
    }
}
