package net.whale.inventory_dimension.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
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
    @Shadow private float xMouse;
    @Shadow private float yMouse;

    @Unique private boolean inventory_Dimension$draggingPlayerModel = false;
    @Unique private int inventory_Dimension$modelCenterX = 75;
    @Unique private int inventory_Dimension$modelCenterY = 78;

    @Inject(method = "mouseClicked", at = @At("HEAD"), cancellable = true)
    private void onMouseClicked(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        int modelX = screen.getGuiLeft() + 26;
        int modelY = screen.getGuiTop() + 8;
        int modelW = 49;
        int modelH = 70;

        if (mouseX >= modelX && mouseX < modelX + modelW && mouseY >= modelY && mouseY < modelY + modelH) {
            inventory_Dimension$modelCenterY = 78;
            inventory_Dimension$modelCenterX = 75;
            this.inventory_Dimension$draggingPlayerModel = true;
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mouseReleased", at = @At("HEAD"))
    private void onMouseReleased(double mouseX, double mouseY, int button, CallbackInfoReturnable<Boolean> cir) {
        InventoryScreen screen = (InventoryScreen) (Object) this;
        for(Slot slot : screen.getMenu().slots){
            int left  = screen.getGuiLeft() + slot.x;
            int top   = screen.getGuiTop()  + slot.y;
            if (mouseX >= left -1 && mouseX < left + 17 && mouseY >= top -1 && mouseY < top + 17) {
                if(slot != null){
                    System.out.println("slot:"+slot.toString()+"item:"+slot.getItem().toString());
                    if(slot.getItem().getItem() == Blocks.ENDER_CHEST.asItem()){
                        ClientLevel level = Minecraft.getInstance().level;
                        MindEntity mind = new MindEntity(ModEntities.MIND_ENTITY.get(),level);
                        LocalPlayer player = Minecraft.getInstance().player;
                        mind.setPlayerUUID(player.getUUID());
                        ((PlayerInterface) player).setInventory_Dimension$controlledEntity(mind);
                        mind.setPos(player.position());
                        level.addEntity(mind);
                        Minecraft.getInstance().setCameraEntity(mind);
                    } else{

                    }
                }
            }
        }
        this.inventory_Dimension$draggingPlayerModel = false;
    }

    @Inject(method = "renderBg", at = @At("HEAD"), cancellable = true)
    private void onRenderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY, CallbackInfo ci) {
        InventoryScreen screen = (InventoryScreen) (Object) this;

        int i = screen.getGuiLeft();
        int j = screen.getGuiTop();

        graphics.blit(InventoryScreen.INVENTORY_LOCATION, i, j, 0, 0, screen.getXSize(), screen.getYSize());

        float scale = inventory_Dimension$draggingPlayerModel ? 8f : 30f;
        float verticalOffset = 0.0625f;
        float fx = inventory_Dimension$draggingPlayerModel ? (float) i + 50 : this.xMouse;
        float fy = inventory_Dimension$draggingPlayerModel ? (float) j + 43 : this.yMouse;
        if (inventory_Dimension$draggingPlayerModel) {
            inventory_Dimension$modelCenterX = mouseX*2;
            inventory_Dimension$modelCenterY = mouseY*2;
        } else {
            inventory_Dimension$modelCenterX = i*2 + 101;
            inventory_Dimension$modelCenterY = j*2 + 86;
        }

        InventoryScreen.renderEntityInInventoryFollowsMouse(
                graphics,
                0, // left
                0,  // top
                inventory_Dimension$modelCenterX,
                inventory_Dimension$modelCenterY,
                (int) scale,
                verticalOffset,
                fx,fy,
                ((InventoryScreen)(Object) this).getMinecraft().player
        );

        ci.cancel();
    }
}
