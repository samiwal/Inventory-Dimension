package net.whale.inventory_dimension.entity.entities;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.whale.inventory_dimension.access.PlayerInterface;

public class MindEntity extends Mob {
    public BlockPos renderBlockPos = null;
    private int echestitemnumber;
    private Item echestitem = Minecraft.getInstance().player.getEnderChestInventory().getItem(echestitemnumber).getItem();
    private BlockPos mind_position;
    private static final int WALL_NEAR    = 1;  // Wandposition nah (XZ)
    private static final int WALL_FAR     = 14; // Wandposition fern (XZ) = Inner-Max XZ
    private static final int INNER_NEAR   = 2;  // Innenraum-Start (XZ)
    private static final int INNER_NEAR_Y = 1;  // Innenraum-Start (Y)
    private static final int WALL_FAR_Y   = 9;  // Wandposition oben = Inner-Max Y



    public MindEntity(EntityType<MindEntity> p_21368_, Level p_21369_) {
        super(p_21368_, p_21369_);
    }

    public boolean isWallPos(BlockPos pos) {
        if (mind_position == null) return false;
        int x0 = mind_position.getX(), y0 = mind_position.getY(), z0 = mind_position.getZ();
        int dx = pos.getX() - x0, dy = pos.getY() - y0, dz = pos.getZ() - z0;

        // Hauptkörper: volle Außenhülle 1–14 in XZ, 0–9 in Y
        boolean inMainXZ = dx >= 1 && dx <= 14 && dz >= 1 && dz <= 14;
        boolean inMainY  = dy >= 0 && dy <= 9;
        boolean isMain   = inMainXZ && inMainY && (
                dx == 1 || dx == 14 || dz == 1 || dz == 14 || dy == 0 || dy == 9
        );

        // Deckel: 1–14 in XZ, 10–13 in Y (voller Block)
        boolean isLid = dx >= 1 && dx <= 14 && dz >= 1 && dz <= 14 && dy >= 10 && dy <= 13;

        // Schloss: X 7–8, Y 7–10, Z 0
        boolean isLock = dx >= 7 && dx <= 8 && dy >= 7 && dy <= 10 && dz == 0;

        return isMain || isLid || isLock;
    }

    public boolean isSpawnPos(BlockPos pos) {
        int dx = pos.getX() - mind_position.getX();
        int dz = pos.getZ() - mind_position.getZ();
        int dy = pos.getY() - mind_position.getY();
        return dx >= 7 && dx <= 8 && dz >= 7 && dz <= 8
                && dy >= WALL_FAR_Y - 2 && dy < WALL_FAR_Y;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    public void tick() {
        super.tick();
        Minecraft mc = Minecraft.getInstance();

        this.setXRot(mc.player.getXRot());
        this.setYRot(mc.player.getYRot());
        this.setYHeadRot(mc.player.getYHeadRot());

        this.setDeltaMovement(Vec3.ZERO);
        handleMovementInput(mc);
        this.move(MoverType.SELF, this.getDeltaMovement());

        updateRenderBlockPos();
    }

    private void handleMovementInput(Minecraft mc) {
        float yaw = getYRot();
        double sin = Math.sin(Math.toRadians(yaw));
        double cos = Math.cos(Math.toRadians(yaw));

        addMovementIfKeyDown(mc.options.keyJump,  0,    1,    0);
        addMovementIfKeyDown(mc.options.keyShift, 0,   -1,    0);
        addMovementIfKeyDown(mc.options.keyLeft,  cos,  0,    sin);
        addMovementIfKeyDown(mc.options.keyRight, -cos, 0,   -sin);
        addMovementIfKeyDown(mc.options.keyUp,    -sin, 0,    cos);
        addMovementIfKeyDown(mc.options.keyDown,  sin,  0,   -cos);
    }

    private void addMovementIfKeyDown(KeyMapping key, double dx, double dy, double dz) {
        if (key.isDown()) this.addDeltaMovement(new Vec3(dx, dy, dz));
    }

    private void updateRenderBlockPos() {
        Vec3 start = this.getEyePosition(1.0F);
        Vec3 end = start.add(this.getViewVector(1.0F).scale(5));

        BlockHitResult hitResult = level().clip(
                new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this)
        );

        if (hitResult.getType() == HitResult.Type.MISS) {
            renderBlockPos = BlockPos.containing(end);
        } else {
            renderBlockPos = hitResult.getBlockPos().relative(hitResult.getDirection());
        }

        if (!isInsideRoom(renderBlockPos) || !level().getBlockState(renderBlockPos).isAir() || isSpawnPos(renderBlockPos)) {
            renderBlockPos = null;
        }
    }

    private boolean isInsideRoom(BlockPos pos) {
        if (mind_position == null) return false;
        return pos.getX() >= mind_position.getX() + INNER_NEAR   && pos.getX() < mind_position.getX() + WALL_FAR
                && pos.getY() >= mind_position.getY() + INNER_NEAR_Y  && pos.getY() < mind_position.getY() + WALL_FAR_Y
                && pos.getZ() >= mind_position.getZ() + INNER_NEAR   && pos.getZ() < mind_position.getZ() + WALL_FAR;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public void deleteBlockIfPossible() {
        //TODO
    }
    public void placeBlockIfPossible() {
        //TODO
    }
    public void selectBlockIfPossible() {
        //TODO
    }

    public void onScroll(boolean next) {
        Minecraft mc = Minecraft.getInstance();
        int total = ((PlayerInterface) mc.player).inventoryDimension$getEntityItems() + 1;
        echestitemnumber = next
                ? (echestitemnumber + 1) % total
                : (echestitemnumber - 1 + total) % total;
        this.echestitem = mc.player.getEnderChestInventory().getItem(echestitemnumber).getItem();
        this.playSound(net.minecraft.sounds.SoundEvents.UI_STONECUTTER_SELECT_RECIPE, 0.5F, 1.0F);
    }

    public Item getEchestitem() {
        return echestitem;
    }

    public BlockPos getMindPosition() {
        return mind_position;
    }

    public void setMindPosition(BlockPos mind_position) {
        this.mind_position = mind_position;
    }
}