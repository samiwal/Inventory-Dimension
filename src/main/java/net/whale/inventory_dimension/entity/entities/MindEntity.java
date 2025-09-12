package net.whale.inventory_dimension.entity.entities;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.FlyingMoveControl;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.acess.PlayerInterface;
import net.whale.inventory_dimension.mixin.ClientPlayerMixin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Optional;
import java.util.UUID;

public class MindEntity extends Mob {
    private static final EntityDataAccessor<Optional<UUID>> PLAYER_UUID =
            SynchedEntityData.defineId(MindEntity.class, EntityDataSerializers.OPTIONAL_UUID);
    public MindEntity(EntityType<MindEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
        this.moveControl = new FlyingMoveControl(this,20,true);
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
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }
    //@Override
    //public void addAdditionalSaveData(CompoundTag tag) {
    //    super.addAdditionalSaveData(tag);
    //    tag.putUUID("PlayerUUID", getPlayerUUID());
    //}
    //@Override
    //public void readAdditionalSaveData(CompoundTag tag) {
    //    super.readAdditionalSaveData(tag);
    //    if (tag.hasUUID("PlayerUUID")) {
    //        setPlayerUUID(tag.getUUID("PlayerUUID"));
    //    }
    //}

    @Nullable
    public UUID getPlayerUUID() {
        return this.entityData.get(PLAYER_UUID).orElse(UUID.randomUUID());
    }
    public void setPlayerUUID(UUID uuid) {
        this.entityData.set(PLAYER_UUID, Optional.of(uuid));
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.@NotNull Builder builder) {
        super.defineSynchedData(builder);
        builder.define(PLAYER_UUID,Optional.empty());
    }
}
