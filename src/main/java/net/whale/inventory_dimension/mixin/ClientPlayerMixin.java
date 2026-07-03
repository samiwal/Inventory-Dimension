package net.whale.inventory_dimension.mixin;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;
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
    @Unique private static boolean inventoryDimension$editingVirtual = false;
    @Unique private Item inventoryDimension$activeItem = null;
    @Unique private @Nullable MindEntity inventoryDimension$controlledEntity = null;
    @Unique private boolean inventoryDimension$isOnItemSlot;
    @Unique private int inventoryDimension$itemSlotNumber;
    @Unique private boolean inventoryDimension$isDraggingPlayerModel;
    @Unique private PalettedContainer<BlockState> inventoryDimension$sectionBlockStates = new PalettedContainer<>(
            net.minecraft.world.level.block.Block.BLOCK_STATE_REGISTRY,
            net.minecraft.world.level.block.Blocks.AIR.defaultBlockState(),
            PalettedContainer.Strategy.SECTION_STATES
    );

    @Unique public @Nullable MindEntity inventoryDimension$getControlledEntity(){return inventoryDimension$controlledEntity;}
    @Override public boolean inventoryDimension$hasControlledEntity() {return inventoryDimension$controlledEntity != null;}
    @Unique public void inventoryDimension$setControlledEntity(@Nullable MindEntity entity){this.inventoryDimension$controlledEntity = entity;}

    @Inject(method = "move",at = @At("HEAD"),cancellable = true)
    private void onMove(MoverType moverType, Vec3 travelVec, CallbackInfo ci){
        if(inventoryDimension$hasControlledEntity()){
            ci.cancel();
        }
    }

    @Override public void inventoryDimension$setSectionBlockStates(PalettedContainer<BlockState> states) {
        this.inventoryDimension$sectionBlockStates = states;
    }
    @Override public PalettedContainer<BlockState> inventoryDimension$getSectionBlockStates() {
        return this.inventoryDimension$sectionBlockStates;
    }

    @Override public void inventoryDimension$setEditingVirtual(boolean inventory_Dimension$editingVirtual) {
        ClientPlayerMixin.inventoryDimension$editingVirtual = inventory_Dimension$editingVirtual;
    }
    @Override public boolean inventoryDimension$getEditingVirtual() {
        return ClientPlayerMixin.inventoryDimension$editingVirtual;
    }

    @Override public void inventoryDimension$setActiveItem(Item item) { inventoryDimension$activeItem = item; }
    @Override public Item inventoryDimension$getActiveItem() { return inventoryDimension$activeItem; }

    @Override public boolean inventoryDimension$getIsOnItemSlot() {return inventoryDimension$isOnItemSlot;}
    @Override public void inventoryDimension$setIsOnItemSlot(boolean isOnItemSlot) {
        inventoryDimension$isOnItemSlot = isOnItemSlot;}

    @Override public int inventoryDimension$getItemSlotNumber() {return inventoryDimension$itemSlotNumber;}
    @Override public void inventoryDimension$setItemSlotNumber(int itemSlotNumber) {this.inventoryDimension$itemSlotNumber = itemSlotNumber;}

    @Override public void inventoryDimension$setIsDraggingPlayerModel(boolean dragging) {
        inventoryDimension$isDraggingPlayerModel = dragging;}
    @Override public boolean inventoryDimension$getIsDraggingPlayerModel() {return inventoryDimension$isDraggingPlayerModel;}
}
