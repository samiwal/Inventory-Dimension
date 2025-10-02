package net.whale.inventory_dimension.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin implements PlayerInterface {
    @Unique
    private @Nullable MindEntity inventory_Dimension$controlledEntity = null;
    @Unique
    public @Nullable MindEntity getInventory_Dimension$ControlledEntity(){
        return inventory_Dimension$controlledEntity;
    }
    @Override
    public boolean hasInventory_Dimension$controlledEntity() {
        return inventory_Dimension$controlledEntity != null;
    }
    @Unique
    public void setInventory_Dimension$controlledEntity(@Nullable MindEntity entity){
        this.inventory_Dimension$controlledEntity = entity;
    }
    @Inject(method = "move",at = @At("HEAD"),cancellable = true)
    private void onMove(MoverType moverType, Vec3 travelVec, CallbackInfo ci){
        if(inventory_Dimension$controlledEntity != null){
            ci.cancel();
        }
    }
}
