package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.Blocks;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.entity.ModEntities;
import net.whale.inventory_dimension.entity.entities.MindEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin {
    @Unique private PlayerInterface inventory_Dimension$p = ((PlayerInterface) Minecraft.getInstance().player);

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        int modelX = screen.getGuiLeft() + 26;
        int modelY = screen.getGuiTop() + 8;
        int modelW = 49;
        int modelH = 70;
        if (inventory_Dimension$p.getInventoryDimension$isOnItemSlot()) {
            if (mouseX >= screen.getMenu().slots.get(inventory_Dimension$p.getInventory_Dimension$itemSlotNumber()).x + screen.getGuiLeft() && mouseX < screen.getMenu().slots.get(inventory_Dimension$p.getInventory_Dimension$itemSlotNumber()).x + screen.getGuiLeft() + 16 && mouseY >= screen.getMenu().slots.get(inventory_Dimension$p.getInventory_Dimension$itemSlotNumber()).y + screen.getGuiTop() && mouseY < screen.getMenu().slots.get(inventory_Dimension$p.getInventory_Dimension$itemSlotNumber()).y + screen.getGuiTop() + 16) {
                inventory_Dimension$p.setInventoryDimension$isOnItemSlot(false);
                if(inventory_Dimension$p.hasInventory_Dimension$controlledEntity()){
                    MindEntity entity = inventory_Dimension$p.getInventory_Dimension$controlledEntity();
                    inventory_Dimension$p.setInventory_Dimension$controlledEntity(null);
                    Minecraft.getInstance().level.removeEntity(entity.getId(), Entity.RemovalReason.DISCARDED);
                    Minecraft.getInstance().setCameraEntity(Minecraft.getInstance().player);
                }
                inventory_Dimension$p.setInventoryDimension$isDraggingPlayerModel(true);
                cir.setReturnValue(true);
            }
        }
        else {
            if (mouseX >= modelX && mouseX < modelX + modelW && mouseY >= modelY && mouseY < modelY + modelH) {
                this.inventory_Dimension$p.setInventoryDimension$isDraggingPlayerModel(true);
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        if(inventory_Dimension$p.getInventoryDimension$isDraggingPlayerModel()) {
            for (Slot slot : screen.getMenu().slots) {
                int left = screen.getGuiLeft() + slot.x;
                int top = screen.getGuiTop() + slot.y;
                if (mouseX >= left && mouseX < left + 16 && mouseY >= top && mouseY < top + 16) {
                    if (slot != null) {
                        if (slot.getItem().getItem() == Blocks.ENDER_CHEST.asItem()) {
                            inventory_Dimension$p.setInventoryDimension$isOnItemSlot(true);
                            inventory_Dimension$p.setInventory_Dimension$itemSlotNumber(screen.getMenu().slots.indexOf(slot));
                            ClientLevel level = Minecraft.getInstance().level;
                            MindEntity mind = new MindEntity(ModEntities.MIND_ENTITY.get(), level);
                            LocalPlayer player = Minecraft.getInstance().player;
                            mind.setPlayerUUID(player.getUUID());
                            ((PlayerInterface) player).setInventory_Dimension$controlledEntity(mind);
                            mind.setPos(player.position());
                            level.addEntity(mind);
                            Minecraft.getInstance().setCameraEntity(mind);
                        } else {

                        }
                    }
                }
            }
            inventory_Dimension$p.setInventoryDimension$isDraggingPlayerModel(false);
        }
    }
    @Inject(method = "init", at = @At("TAIL")) private void onInit(CallbackInfo ci) {((PlayerInterface) Minecraft.getInstance().player).setInventoryDimension$isDraggingPlayerModel(false);}
    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(GuiGraphics graphics, int xMouse, int yMouse, float p_98878_, CallbackInfo ci){
        if(!inventory_Dimension$p.getInventoryDimension$isDraggingPlayerModel() && !inventory_Dimension$p.getInventoryDimension$isOnItemSlot()) return;
        InventoryScreen screen = (InventoryScreen) (Object) this;
        if (inventory_Dimension$p.getInventoryDimension$isOnItemSlot()) {
            inventory_Dimension$p.setInventoryDimension$isOnItemSlot(false);
            Slot slot = screen.getMenu().slots.get(inventory_Dimension$p.getInventory_Dimension$itemSlotNumber());
            int modelX = slot.x + screen.getGuiLeft() + 8;
            int modelY = slot.y + screen.getGuiTop() + 8;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    0,0,
                    modelX*2,modelY*2,
                    8,0.0625f,
                    xMouse,yMouse,
                    Minecraft.getInstance().player
            );
            inventory_Dimension$p.setInventoryDimension$isOnItemSlot(true);
        } else {
            inventory_Dimension$p.setInventoryDimension$isDraggingPlayerModel(false);
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    graphics,
                    0,0,
                    xMouse*2,yMouse*2,
                    8,0.0625f,
                    xMouse,yMouse,
                    Minecraft.getInstance().player
            );
            inventory_Dimension$p.setInventoryDimension$isDraggingPlayerModel(true);
        }
    }
    @Inject(method = "renderEntityInInventoryFollowsMouse", at = @At(value = "HEAD"), cancellable = true)
    private static void onRenderEntity(GuiGraphics p_282802_, int p_275688_, int p_275245_, int p_275535_, int p_301381_, int p_299741_, float p_275604_, float p_275546_, float p_300682_, LivingEntity p_275689_, CallbackInfo ci){
        if(((PlayerInterface) Minecraft.getInstance().player).getInventoryDimension$isOnItemSlot() || ((PlayerInterface) Minecraft.getInstance().player).getInventoryDimension$isDraggingPlayerModel()) ci.cancel();
    }
}
