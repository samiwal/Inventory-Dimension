package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.whale.inventory_dimension.acess.PlayerInterface;
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
            if (mc.options.keyAttack.consumeClick()) {
                MindEntity entity = ((PlayerInterface) mc.player).getInventory_Dimension$controlledEntity();
                entity.deleteBlockIfPossible();
                ci.cancel();
            }
            if (mc.options.keyUse.consumeClick()) {
                MindEntity entity = ((PlayerInterface) mc.player).getInventory_Dimension$controlledEntity();
                entity.placeBlockIfPossible();
                ci.cancel();
            }
            if (mc.options.keyPickItem.consumeClick()) {
                MindEntity entity = ((PlayerInterface) mc.player).getInventory_Dimension$controlledEntity();
                entity.selectBlockIfPossible();
                ci.cancel();
            }
        }
    }
}
