package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements PlayerInterface {
    //@Unique
    //private static final EntityDataAccessor<Optional<UUID>> inventory_Dimension$MIND =
    //        SynchedEntityData.defineId(MindEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    // cannot define id twice
    @Unique
    private @Nullable MindEntity inventory_Dimension$controlledEntity;
    @Unique
    public @Nullable MindEntity getInventory_Dimension$ControlledEntity(){
        return inventory_Dimension$controlledEntity;
    }
    @Unique
    public void setInventory_Dimension$controlledEntity(@Nullable MindEntity entity){
        this.inventory_Dimension$controlledEntity = entity;
    }
    @Inject(method = "travel",at = @At("HEAD"),cancellable = true)
    private void onTravel(Vec3 travelVec , CallbackInfo ci){
        //if(inventory_Dimension$controlledEntity != null){
        //    System.out.println(travelVec.toString());
        //    inventory_Dimension$controlledEntity.travel(travelVec);
        //}
    }
}
