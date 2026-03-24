package net.whale.inventory_dimension.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@Mixin(LocalPlayer.class)
public abstract class ClientPlayerMixin implements PlayerInterface {
    @Unique private Item inventoryDimension$activeItem = null;
    @Unique private @Nullable MindEntity inventoryDimension$controlledEntity = null;
    @Unique private int inventoryDimension$entityItems;
    @Unique private boolean inventoryDimension$isOnItemSlot;
    @Unique private int inventoryDimension$itemSlotNumber;
    @Unique private boolean inventoryDimension$isDraggingPlayerModel;
    @Unique private Vec3 inventoryDimension$playerPos;

    @Unique public @Nullable MindEntity inventoryDimension$getControlledEntity(){return inventoryDimension$controlledEntity;}
    @Override public boolean inventoryDimension$hasControlledEntity() {return inventoryDimension$controlledEntity != null;}
    @Unique public void inventoryDimension$setControlledEntity(@Nullable MindEntity entity){this.inventoryDimension$controlledEntity = entity;}

    @Inject(method = "move",at = @At("HEAD"),cancellable = true)
    private void onMove(MoverType moverType, Vec3 travelVec, CallbackInfo ci){
        if(inventoryDimension$hasControlledEntity()){
            ci.cancel();
        }
    }

    @Override public void inventoryDimension$setActiveItem(Item item) { inventoryDimension$activeItem = item; }
    @Override public Item inventoryDimension$getActiveItem() { return inventoryDimension$activeItem; }

    @Override public void inventoryDimension$setEntityItems(int items) {
        inventoryDimension$entityItems = items;}
    @Override public int inventoryDimension$getEntityItems() {return inventoryDimension$entityItems;}

    @Override public boolean inventoryDimension$getIsOnItemSlot() {return inventoryDimension$isOnItemSlot;}
    @Override public void inventoryDimension$setIsOnItemSlot(boolean isOnItemSlot) {
        inventoryDimension$isOnItemSlot = isOnItemSlot;}

    @Override public int inventoryDimension$getItemSlotNumber() {return inventoryDimension$itemSlotNumber;}
    @Override public void inventoryDimension$setItemSlotNumber(int itemSlotNumber) {this.inventoryDimension$itemSlotNumber = itemSlotNumber;}

    @Override public void inventoryDimension$setIsDraggingPlayerModel(boolean dragging) {
        inventoryDimension$isDraggingPlayerModel = dragging;}
    @Override public boolean inventoryDimension$getIsDraggingPlayerModel() {return inventoryDimension$isDraggingPlayerModel;}
}
