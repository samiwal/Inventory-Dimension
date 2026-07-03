package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.whale.inventory_dimension.access.PlayerInterface;
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
        PlayerInterface player = (PlayerInterface) mc.player;
        if (player == null || !player.inventoryDimension$hasControlledEntity()) return;
        Options o = mc.options;
        MindEntity entity = player.inventoryDimension$getControlledEntity();
        if(o.keyAttack.isDown() || o.keyAttack.consumeClick()) {
            entity.destroy();
            ci.cancel();
        }
        if (o.keyUse.isDown() || o.keyUse.consumeClick()) {
            entity.interact();
            ci.cancel();
        }
        if (o.keyPickItem.isDown() || o.keyPickItem.consumeClick()) {
            entity.select();
            ci.cancel();
        }

    }
}
