package net.whale.inventory_dimension.entity.entities;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.SectionPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.PlayerEnderChestContainer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import net.whale.inventory_dimension.access.PlayerInterface;
import net.whale.inventory_dimension.level.VirtualLevelChunkSection;
import net.whale.inventory_dimension.mixin.ChunkAccessAccessor;
import net.whale.inventory_dimension.network.NetworkHandler;
import net.whale.inventory_dimension.network.PlayerInventorySyncPacket;
import net.whale.inventory_dimension.render.BlockRenderState;
import net.whale.inventory_dimension.update.UpdateLevel;

public class MindEntity extends Mob {
    public BlockPos renderBlockPos = null;
    private BlockPos hitBlockPos = null;
    private int eCSlot = -1;
    private static final int ECSLOTEMPTY = -1;
    public final SectionPos sectionPos;
    private final BlockPos subChunkStart;
    private static final int WALL_NEAR    = 1;  // Wandposition nah (XZ)
    private static final int WALL_FAR     = 14; // Wandposition fern (XZ) = Inner-Max XZ
    private static final int INNER_NEAR   = 2;  // Innenraum-Start (XZ)
    private static final int INNER_NEAR_Y = 1;  // Innenraum-Start (Y)
    private static final int WALL_FAR_Y   = 9;  // Wandposition oben = Inner-Max Y
    public final VirtualLevelChunkSection custumSection;
    private final PlayerEnderChestContainer eC;

    public MindEntity(EntityType<MindEntity> entityType, Level level, SectionPos sectionPos) {
        super(entityType, level);
        this.sectionPos = sectionPos;
        this.subChunkStart = sectionPos.origin();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) throw new UnsupportedOperationException("A player is needed to instance a MindEntity");
        this.eC = mc.player.getEnderChestInventory();
        ChunkAccess chunk = level.getChunk(sectionPos.getX(), sectionPos.getZ());
        LevelChunkSection[] sections = ((ChunkAccessAccessor) chunk).getSections();
        LevelChunkSection section = sections[chunk.getSectionIndexFromSectionY(sectionPos.getY())];
        PalettedContainerRO<Holder<Biome>> biomeHolder = section.getBiomes();
        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
        ////Warning dass blockentities dort nicht ticken können
        LevelChunkSection virtualSection = new LevelChunkSection(((PlayerInterface) mc.player).inventoryDimension$getSectionBlockStates(),biomeHolder);
        this.custumSection = new VirtualLevelChunkSection(registry, section, virtualSection);
        sections[chunk.getSectionIndexFromSectionY(sectionPos.getY())] = this.custumSection;
        UpdateLevel.updateSection(sectionPos,level,mc.levelRenderer);
        updateActiveItem(false, true);
    }

    public MindEntity(EntityType<MindEntity> entityType, Level level){
        super(entityType, level);
        this.sectionPos = null;
        this.subChunkStart = null;
        this.custumSection = null;
        this.eC = null;
    }

    private boolean isSpawnPos(BlockPos pos) {
        int dx = pos.getX() - subChunkStart.getX();
        int dz = pos.getZ() - subChunkStart.getZ();
        int dy = pos.getY() - subChunkStart.getY();
        return dx >= 7 && dx <= 8 && dz >= 2 && dz <= 3
                && dy >= 4 && dy <= 5;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes();
    }

    @Override
    public void tick() {
        updateActiveItem(false, true);
        super.tick();
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

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
        float pitch = getXRot();
        double psin = Math.sin(Math.toRadians(pitch));
        double pcos = Math.cos(Math.toRadians(pitch));
        double ysin = Math.sin(Math.toRadians(yaw));
        double ycos = Math.cos(Math.toRadians(yaw));
        addMovementIfKeyDown(mc.options.keyLeft, ycos,  0,    ysin);
        addMovementIfKeyDown(mc.options.keyRight, -ycos, 0,   -ysin);
        addMovementIfKeyDown(mc.options.keyUp,    -ysin * pcos, - psin, ycos * pcos);
        addMovementIfKeyDown(mc.options.keyDown,  ysin * pcos,  psin,   -ycos * pcos);
    }

    private void addMovementIfKeyDown(KeyMapping key, double dx, double dy, double dz) {
        if (key.isDown()) this.addDeltaMovement(new Vec3(dx, dy, dz));
    }

    private void updateRenderBlockPos() {
        Vec3 start = this.getEyePosition(1.0F);
        Vec3 end = start.add(this.getViewVector(1.0F).scale(5));
        BlockPos hitPos = null;
        BlockPos renderPos;
        BlockHitResult hitResult = level().clip(
                new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, this)
        );

        if (hitResult.getType() == HitResult.Type.MISS) {
            renderPos = BlockPos.containing(end);
        } else {
            hitPos = hitResult.getBlockPos();
            renderPos = hitPos.relative(hitResult.getDirection());
        }
        renderBlockPos = validatePos(renderPos,true);
        hitBlockPos = validatePos(hitPos,false);
    }
    BlockPos validatePos(BlockPos pos,boolean shouldBeAir){
        if (!isInsideRoom(pos) || (level().getBlockState(pos).isAir() != shouldBeAir) || isSpawnPos(pos)) {
            pos = null;
        }
        return pos;
    }

    public boolean isInsideRoom(BlockPos pos) {
        if (subChunkStart == null) return false;
        if (pos == null) return false;
        return pos.getX() >= subChunkStart.getX() + INNER_NEAR   && pos.getX() < subChunkStart.getX() + WALL_FAR
                && pos.getY() >= subChunkStart.getY() + INNER_NEAR_Y  && pos.getY() < subChunkStart.getY() + WALL_FAR_Y
                && pos.getZ() >= subChunkStart.getZ() + INNER_NEAR   && pos.getZ() < subChunkStart.getZ() + WALL_FAR;
    }

    @Override
    public boolean shouldBeSaved() {
        return false;
    }

    public void destroy() {
        if (hitBlockPos == null) return;
        if (Screen.hasShiftDown()) {

        }
        ItemStack stack = getItemStackFormBlockPos(hitBlockPos);
        if (Screen.hasAltDown()){
            NetworkHandler.INSTANCE.send(new PlayerInventorySyncPacket(stack), PacketDistributor.SERVER.noArg());
        } else {
            if (!eC.canAddItem(stack)) return;
            eC.addItem(stack);
        }
        level().getChunkAt(hitBlockPos).removeBlockEntity(hitBlockPos);
        level().setBlock(hitBlockPos, Blocks.AIR.defaultBlockState(), 2);
    }

    private ItemStack getItemStackFormBlockPos(BlockPos pos){
        Item item = custumSection.getVirtualSection().getBlockState(
                pos.getX() & 15,
                pos.getY() & 15,
                pos.getZ() & 15).getBlock().asItem();
        ItemStack stack = new ItemStack(item);
        BlockEntity blockEntity = level().getBlockEntity(pos);
        if (blockEntity != null) {
            blockEntity.saveToItem(stack, level().registryAccess());
        }
        return stack;
    }

    public void interact() {
        if (Screen.hasShiftDown() || hitBlockPos == null) {
            place();
        } else {
            BlockState state = level().getBlockState(hitBlockPos);
            if (state.getBlock() instanceof EntityBlock) {
                BlockEntity blockEntity = level().getBlockEntity(hitBlockPos);
                if (blockEntity instanceof MenuProvider) {

                }
            }

        }

    }
    private void place() {
        if (renderBlockPos == null) return;
        if (eCSlot <= ECSLOTEMPTY) return;
        Minecraft mc = Minecraft.getInstance();
        ItemStack stack = eC.getItem(eCSlot);
        if (!stack.getItem().equals(Items.AIR)) {
            BlockState stateToPlace = BlockRenderState.state;
            if (stateToPlace.getBlock() != ((BlockItem) stack.getItem()).getBlock()) {
                return;
            }
            try {
                ((PlayerInterface) mc.player).inventoryDimension$setEditingVirtual(true);
                level().setBlock(renderBlockPos,stateToPlace,2);
            } finally {
                ((PlayerInterface) mc.player).inventoryDimension$setEditingVirtual(false);
            }
            BlockEntity be = level().getBlockEntity(renderBlockPos);
            if (be != null) {
                CustomData data = stack.get(DataComponents.BLOCK_ENTITY_DATA);
                if (data != null) { data.loadInto(be,level().registryAccess()); }
                be.setChanged();
            }
            stack.shrink(1);
            updateActiveItem(false,true);
        }
    }

    public void select() {
        ItemStack stack = getItemStackFormBlockPos(hitBlockPos);
        if (Screen.hasAltDown()){
             Minecraft mc = Minecraft.getInstance();
             if (mc.player == null) return;
             Inventory inventory = mc.player.getInventory();
             inventory.setPickedItem(stack);
        } else {
            for (int i = 0; i < eC.getContainerSize(); i++) {
                ItemStack itemstack = eC.getItem(i);
                if (ItemStack.isSameItem(itemstack, stack)) {
                    eCSlot = i;
                }
            }
        }
    }

    public void updateActiveItem(boolean scroll, boolean direction) {
        Minecraft mc = Minecraft.getInstance();
        int eCLastSlot = eC.getContainerSize() - 1;
        if (mc.player == null) return;
        int slotFindHelper = eCSlot;
        if(!scroll){ if (eCSlot == ECSLOTEMPTY) return; }
        else slotFindHelper += direction ? 1 : -1;
        if(slotFindHelper < ECSLOTEMPTY) slotFindHelper = eCLastSlot;
        if(slotFindHelper > eCLastSlot) slotFindHelper = ECSLOTEMPTY;
        for (; slotFindHelper <= eCLastSlot && slotFindHelper > ECSLOTEMPTY; slotFindHelper += direction ? 1 : -1) {
            if (eC.getItem(slotFindHelper).getItem() instanceof BlockItem) {
                eCSlot = slotFindHelper;
                return;
            }
        }
        eCSlot = ECSLOTEMPTY;
    }

    public Item getEchestitem() {
        if (eCSlot < 0 || eCSlot > eC.getContainerSize()) return Items.AIR;
        return eC.getItem(eCSlot).getItem();
    }

    public BlockPos getMindChunkPosition() {
        return subChunkStart;
    }
}