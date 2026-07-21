package net.whale.inventory_dimension.mixin;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.whale.inventory_dimension.access.PlayerInterface;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public class MultiPlayerGameModeMixin {
    @Inject(method = "handleInventoryMouseClick", at = @At("HEAD"), cancellable = true)
    private void inventoryDimension$onClick(int p_171800_, int p_171801_, int p_171802_, ClickType p_171803_, Player player, CallbackInfo ci) {
        PlayerInterface pi = (PlayerInterface) player;
        if (pi.inventoryDimension$getEditingVirtual()) {
            AbstractContainerMenu menu = player.containerMenu;
            menu.clicked(p_171801_, p_171802_, p_171803_, player);
            ci.cancel();
        }
    }
}
