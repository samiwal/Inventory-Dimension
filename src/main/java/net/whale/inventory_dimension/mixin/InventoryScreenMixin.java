package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.item.InventoryDimensionItem;
import net.whale.inventory_dimension.item.InventoryDimensionItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {

    @Unique
    private PlayerInterface inventory_Dimension$getPlayer() {
        return (PlayerInterface) Minecraft.getInstance().player;
    }

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        PlayerInterface player = inventory_Dimension$getPlayer();

        if (player.inventoryDimension$getIsOnItemSlot()) {
            Slot slot = screen.getMenu().slots.get(player.inventoryDimension$getItemSlotNumber());
            int slotLeft = slot.x + screen.getGuiLeft();
            int slotTop = slot.y + screen.getGuiTop();
            if (mouseX >= slotLeft && mouseX < slotLeft + 16 && mouseY >= slotTop && mouseY < slotTop + 16) {
                player.inventoryDimension$setIsOnItemSlot(false);
                InventoryDimensionItems.get(slot.getItem().getItem()).onUnequip(player);
                player.inventoryDimension$setIsDraggingPlayerModel(true);
                cir.setReturnValue(true);
            }
        } else if (!screen.getMenu().getCarried().isEmpty()) {
            // MC trägt bereits ein Item - kein Drag starten
        } else {
            int modelX = screen.getGuiLeft() + 26;
            int modelY = screen.getGuiTop() + 8;
            if (mouseX >= modelX && mouseX < modelX + 49 && mouseY >= modelY && mouseY < modelY + 70) {
                player.inventoryDimension$setIsDraggingPlayerModel(true);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"), cancellable = true)
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        PlayerInterface player = inventory_Dimension$getPlayer();
        if (!player.inventoryDimension$getIsDraggingPlayerModel()) return;

        for (Slot slot : screen.getMenu().slots) {
            int left = screen.getGuiLeft() + slot.x;
            int top = screen.getGuiTop() + slot.y;
            if (mouseX >= left && mouseX < left + 16 && mouseY >= top && mouseY < top + 16) {
                InventoryDimensionItem handler = InventoryDimensionItems.get(slot.getItem().getItem());
                if (handler != null) {
                    player.inventoryDimension$setActiveItem(slot.getItem().getItem());
                    player.inventoryDimension$setIsOnItemSlot(true);
                    player.inventoryDimension$setItemSlotNumber(screen.getMenu().slots.indexOf(slot));
                    handler.onEquip(player, slot, screen);
                    player.inventoryDimension$setIsDraggingPlayerModel(false);
                    cir.setReturnValue(true);
                    return;
                }
            }
        }
        player.inventoryDimension$setIsDraggingPlayerModel(false);
    }

    @Inject(method = "init", at = @At("TAIL"))
    private void onInit(CallbackInfo ci) {
        inventory_Dimension$getPlayer().inventoryDimension$setIsDraggingPlayerModel(false);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, int xMouse, int yMouse, float p_98878_, CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        PlayerInterface player = inventory_Dimension$getPlayer();

        if (player.inventoryDimension$getIsOnItemSlot()) {
            Slot slot = screen.getMenu().slots.get(player.inventoryDimension$getItemSlotNumber());
            if (!InventoryDimensionItems.has(slot.getItem().getItem())) {
                player.inventoryDimension$setIsOnItemSlot(false);
                InventoryDimensionItem handler = InventoryDimensionItems.get(player.inventoryDimension$getActiveItem());
                if (handler != null) handler.onRemoved(player);
                player.inventoryDimension$setActiveItem(null);
                return;
            }
            int modelX = slot.x + screen.getGuiLeft() + 8;
            int modelY = slot.y + screen.getGuiTop() + 8;
            player.inventoryDimension$setIsOnItemSlot(false);
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics, 0, 0, modelX * 2, modelY * 2,
                    8, 0.0625f, xMouse, yMouse, Minecraft.getInstance().player
            );
            player.inventoryDimension$setIsOnItemSlot(true);
        } else if (player.inventoryDimension$getIsDraggingPlayerModel()) {
            player.inventoryDimension$setIsDraggingPlayerModel(false);
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics, 0, 0, xMouse * 2, yMouse * 2,
                    8, 0.0625f, xMouse, yMouse, Minecraft.getInstance().player
            );
            player.inventoryDimension$setIsDraggingPlayerModel(true);
        }
    }

    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At("HEAD"), cancellable = true)
    private static void onRenderEntity(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_,
                                       int p_301381_, int p_299741_, float p_275604_, float p_275546_, float p_300682_,
                                       LivingEntity p_275689_, CallbackInfo ci) {
        PlayerInterface player = (PlayerInterface) Minecraft.getInstance().player;
        if (player.inventoryDimension$getIsOnItemSlot() || player.inventoryDimension$getIsDraggingPlayerModel())
            ci.cancel();
    }
}