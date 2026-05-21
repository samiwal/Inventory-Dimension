package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BlockBehaviour.BlockStateBase.class)
public abstract class BlockStateBaseMixin {
    @Inject(method = "getCollisionShape*", at = @At("HEAD"), cancellable = true)
    private void onGetCollisionShape(BlockGetter level, BlockPos pos, CollisionContext context, CallbackInfoReturnable<VoxelShape> cir) {
        if (!Minecraft.getInstance().isSameThread()) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        PlayerInterface player = (PlayerInterface) mc.player;
        if (!player.inventoryDimension$hasControlledEntity()) return;
        Optional<VoxelShape> shape = Optional.empty();
        MindEntity mind = player.inventoryDimension$getControlledEntity();
        if (mind.isSubChunkPos(pos))
            shape = Optional.of(Shapes.empty());
        if (mind.isWallPos(pos)) {
            shape = Optional.of(Shapes.block());
        }
        shape.ifPresent(cir::setReturnValue);
    }
}