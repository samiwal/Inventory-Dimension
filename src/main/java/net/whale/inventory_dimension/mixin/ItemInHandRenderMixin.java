package net.whale.inventory_dimension.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.acess.RenderInterface;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.List;

@Mixin(ItemInHandRenderer.class)
public abstract class ItemInHandRenderMixin implements RenderInterface {
    @Shadow @Final private ItemRenderer itemRenderer;
    @Unique
    private BlockState inventory_Dimension$state = Blocks.DIRT.defaultBlockState();
    @Unique
    private int inventory_Dimension$selectedPropertyIndex;
    @Unique
    private Property<?> inventory_Dimension$selectedProperty;
    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void replaceItemStack(LivingEntity p_270072_, ItemStack p_270793_, ItemDisplayContext p_270837_, boolean p_270203_, PoseStack p_270974_, MultiBufferSource p_270686_, int p_270103_, CallbackInfo ci) {
        PlayerInterface player = (PlayerInterface) Minecraft.getInstance().player;
        if(player.hasInventory_Dimension$controlledEntity()){
                inventory_Dimension$renderBlockOnGround(player,p_270686_);
                ci.cancel();
        }
    }
    @Inject(method = "renderPlayerArm", at = @At("HEAD"), cancellable = true)
    private void renderPlayerHands(PoseStack p_109347_, MultiBufferSource p_109348_, int p_109349_, float p_109350_, float p_109351_, HumanoidArm p_109352_, CallbackInfo ci) {
        PlayerInterface player = (PlayerInterface) Minecraft.getInstance().player;
        if (player.hasInventory_Dimension$controlledEntity()) {
            inventory_Dimension$renderBlockOnGround(player,p_109348_);
            ci.cancel();
        }
    }
    @Unique
    private void inventory_Dimension$renderBlockOnGround(PlayerInterface player, MultiBufferSource buffer){
        BlockPos pos = player.getInventory_Dimension$controlledEntity().blockPos;
        if(player.getInventory_Dimension$controlledEntity().echestitem instanceof BlockItem blockItem) {
            if(this.inventory_Dimension$state.getBlock().asItem() != blockItem) {
                this.inventory_Dimension$state = blockItem.getBlock().defaultBlockState();
                inventory_Dimension$selectedPropertyIndex = 0;
                setInventory_Dimension$Drop();
            }
            PoseStack pose = new PoseStack();
            pose.pushPose();
            Vec3 camPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
            pose.translate(pos.getX() - camPos.x(), pos.getY() - camPos.y(), pos.getZ() - camPos.z());
            Block block = blockItem.getBlock();
            var dispatcher = Minecraft.getInstance().getBlockEntityRenderDispatcher();
            if (block instanceof EntityBlock entityBlock) {
                var be = entityBlock.newBlockEntity(pos, inventory_Dimension$state);
                if (be != null) {be.setLevel(Minecraft.getInstance().level);
                    dispatcher.render(be, 0, pose, buffer);
                    pose.popPose();
                    return;}}
            Minecraft.getInstance().getBlockRenderer().renderSingleBlock(inventory_Dimension$state, pose, buffer, 15728880, OverlayTexture.NO_OVERLAY, ModelData.EMPTY, RenderType.translucent());
            pose.popPose();
        }
    }
    @Override
    public void setInventory_Dimension$Drop() {
        List<Property<?>> props = new ArrayList<>(inventory_Dimension$state.getProperties());
        if(props.isEmpty()) return;
        if(Minecraft.getInstance().options.keySprint.isDown()) inventory_Dimension$selectedPropertyIndex = (inventory_Dimension$selectedPropertyIndex - 1 + props.size()) % props.size();
        else inventory_Dimension$selectedPropertyIndex = (inventory_Dimension$selectedPropertyIndex + 1) % props.size();
        inventory_Dimension$selectedProperty = props.get(inventory_Dimension$selectedPropertyIndex);
        Minecraft.getInstance().gui.setOverlayMessage(net.minecraft.network.chat.Component.literal("§e" + inventory_Dimension$selectedProperty.getName() + " §7: §b" + inventory_Dimension$state.getValue(inventory_Dimension$selectedProperty).toString().toUpperCase()), false);
    }
    @Override
    public void setInventory_Dimension$Swap() {
        if (inventory_Dimension$selectedProperty == null || !inventory_Dimension$state.getProperties().contains(inventory_Dimension$selectedProperty))
            return;
        inventory_Dimension$cycleProperty(inventory_Dimension$state, inventory_Dimension$selectedProperty);
    }
    @Unique
    private <T extends Comparable<T>> void inventory_Dimension$cycleProperty(BlockState state, Property<T> prop) {
        List<T> values = new ArrayList<>(prop.getPossibleValues());
        T current = state.getValue(prop);
        int index = values.indexOf(current);
        if(Minecraft.getInstance().options.keySprint.isDown()) index = (index - 1 + values.size()) % values.size();
        else index = (index + 1) % values.size();
        inventory_Dimension$state = state.setValue(prop, values.get(index));
        Minecraft.getInstance().player.playSound(net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, 1.2F);
        Minecraft.getInstance().gui.setOverlayMessage(net.minecraft.network.chat.Component.literal("§e" + inventory_Dimension$selectedProperty.getName() + " §7: §b" + inventory_Dimension$state.getValue(inventory_Dimension$selectedProperty).toString().toUpperCase()), false);
    }
}
