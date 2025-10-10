package net.whale.inventory_dimension.entity.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.acess.PlayerInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MindEntity extends Mob {
    Minecraft mc = Minecraft.getInstance();
    public BlockPos blockPos = new BlockPos(0,0,0);
    private int echestitemnumber;
    public Item echestitem = mc.player.getEnderChestInventory().getItem(echestitemnumber).getItem();
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID =
            SynchedEntityData.defineId(MindEntity.class, EntityDataSerializers.OPTIONAL_UUID); //Vlt unnötig
    public MindEntity(EntityType<MindEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        if(p_21369_.isClientSide) ((PlayerInterface) Minecraft.getInstance().player).setInventory_Dimension$controlledEntity(this);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D);
    }
    @Override
    public @NotNull HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }


    @Override
    public void tick() {
        super.tick();
        this.setXRot(mc.player.getXRot());
        this.setYRot(mc.player.getYRot());
        this.setYHeadRot(mc.player.getYHeadRot());
        this.setDeltaMovement(new Vec3(0.0,0.0,0.0));
        if(mc.options.keyJump.isDown()){
            this.addDeltaMovement(new Vec3(0.0,1.0,0.0));
        }if(mc.options.keyShift.isDown()){
            this.addDeltaMovement(new Vec3(0.0,-1.0,0.0));
        }if(mc.options.keyLeft.isDown()){
            this.addDeltaMovement(new Vec3(Math.cos(Math.toRadians(getYRot())),0.0,Math.sin(Math.toRadians(getYRot()))));
        }if(mc.options.keyRight.isDown()){
            this.addDeltaMovement(new Vec3(-Math.cos(Math.toRadians(getYRot())),0.0,-Math.sin(Math.toRadians(getYRot()))));
        }if(mc.options.keyUp.isDown()){
            this.addDeltaMovement(new Vec3(-Math.sin(Math.toRadians(getYRot())),0.0,Math.cos(Math.toRadians(getYRot()))));
        }if(mc.options.keyDown.isDown()){
            this.addDeltaMovement(new Vec3(Math.sin(Math.toRadians(getYRot())),0.0,-Math.cos(Math.toRadians(getYRot()))));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        Vec3 start = this.getEyePosition(1.0F);
        Vec3 look = this.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(5));
        BlockHitResult hitResult = Minecraft.getInstance().level.clip(new ClipContext(start, end,
                ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this));
        if(level().getBlockState(hitResult.getBlockPos()) == Blocks.AIR.defaultBlockState()) this.blockPos = hitResult.getBlockPos();
        else if (level().getBlockState(hitResult.getBlockPos().relative(hitResult.getDirection())) == Blocks.AIR.defaultBlockState()) {
            this.blockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        }
    }
    @Override
    public boolean shouldBeSaved() {
        return false;
    }
    @Nullable
    public UUID getPlayerUUID() {
        return this.entityData.get(PLAYER_UUID).orElse(UUID.randomUUID());
    }
    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(PLAYER_UUID, Optional.of(uuid));
    }

    @Override //Maybe unnötig
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYER_UUID,Optional.empty());
    }
    public void deleteBlockIfPossible() {

    }

    public void placeBlockIfPossible() {
    }

    public void selectBlockIfPossible() {
    }

    public void onScroll(boolean up) {
        if(!up) echestitemnumber = (echestitemnumber + 1) % (((PlayerInterface) mc.player).getInventory_Dimension$entityItems()+1);
        else echestitemnumber = (echestitemnumber - 1 + (((PlayerInterface) mc.player).getInventory_Dimension$entityItems()+1)) % (((PlayerInterface) mc.player).getInventory_Dimension$entityItems()+1);
        this.echestitem = mc.player.getEnderChestInventory().getItem(echestitemnumber).getItem();
        mc.player.playSound(net.minecraft.sounds.SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 0.5F, 1.0F);
    }
}
