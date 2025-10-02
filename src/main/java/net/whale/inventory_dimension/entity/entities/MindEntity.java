package net.whale.inventory_dimension.entity.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.acess.PlayerInterface;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MindEntity extends Mob {
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID =
            SynchedEntityData.defineId(MindEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    Minecraft mc = Minecraft.getInstance();
    public MindEntity(EntityType<MindEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        this.moveControl = new FlyingMoveControl(this,20,true);
        this.noPhysics = true;
        this.setNoGravity(true);
        this.setCanPickUpLoot(true);
        this.setNoAi(false);
        if(level().isClientSide) ((PlayerInterface) Minecraft.getInstance().player).setInventory_Dimension$controlledEntity(this);
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
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
            this.setDeltaMovement(new Vec3(0.0,1.0,0.0));
        }if(mc.options.keyShift.isDown()){
            this.setDeltaMovement(new Vec3(0.0,-1.0,0.0));
        }if(mc.options.keyLeft.isDown()){
            this.setDeltaMovement(new Vec3(Math.cos(Math.toRadians(getYRot())),0.0,Math.sin(Math.toRadians(getYRot()))));
        }if(mc.options.keyRight.isDown()){
            this.setDeltaMovement(new Vec3(-Math.cos(Math.toRadians(getYRot())),0.0,-Math.sin(Math.toRadians(getYRot()))));
        }if(mc.options.keyUp.isDown()){
            this.setDeltaMovement(new Vec3(-Math.sin(Math.toRadians(getYRot())),0.0,Math.cos(Math.toRadians(getYRot()))));
        }if(mc.options.keyDown.isDown()){
            this.setDeltaMovement(new Vec3(Math.sin(Math.toRadians(getYRot())),0.0,-Math.cos(Math.toRadians(getYRot()))));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
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
}
