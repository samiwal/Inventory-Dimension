package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public abstract class LevelMixin {
    @Inject(method = "getBlockState", at = @At("HEAD"), cancellable = true)
    private void onGetBlockState(BlockPos pos, CallbackInfoReturnable<BlockState> cir) {
        if (!((Level)(Object)this).isClientSide()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerInterface player = (PlayerInterface) mc.player;
        if (!player.inventoryDimension$hasControlledEntity()) return;

        MindEntity mind = player.inventoryDimension$getControlledEntity();
        if (mind.isWallPos(pos)) {
            cir.setReturnValue(Blocks.STONE.defaultBlockState());
        }
    }
}
