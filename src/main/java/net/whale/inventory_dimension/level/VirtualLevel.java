package net.whale.inventory_dimension.level;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.SectionPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.TickRateManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkSource;
import net.minecraft.world.level.entity.LevelEntityGetter;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraft.world.level.storage.WritableLevelData;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.ticks.LevelTickAccess;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VirtualLevel extends Level {
    private final Level realLevel;
    private final VirtualLevelChunkSection section;
    private final SectionPos startPos;
    private final Map<BlockPos, BlockEntity> blockEntities = new HashMap<>();


    public VirtualLevel(Level level, VirtualLevelChunkSection section, SectionPos startPos) {
        super((WritableLevelData) level.getLevelData(), level.dimension(), level.registryAccess(),
                level.dimensionTypeRegistration(), level.getProfilerSupplier(),
                level.isClientSide(), level.isDebug(), 0L, 0);
        this.realLevel = level;
        this.section = section;
        this.startPos = startPos;
    }

    @Override
    public @NotNull BlockState getBlockState(@NotNull BlockPos blockPos) {
        if (SectionPos.of(blockPos).equals(startPos)) {
            return section.getBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15);
        }
        return realLevel.getBlockState(blockPos);
    }

    @Override
    public void playSeededSound(@Nullable Player p_262953_, double p_263004_, double p_263398_, double p_263376_, Holder<SoundEvent> p_263359_, SoundSource p_263020_, float p_263055_, float p_262914_, long p_262991_) {

    }

    @Override
    public void playSeededSound(@Nullable Player p_220372_, Entity p_220373_, Holder<SoundEvent> p_263500_, SoundSource p_220375_, float p_220376_, float p_220377_, long p_220378_) {

    }

    @Override
    public String gatherChunkSourceStats() {
        return "";
    }

    @Override
    public boolean setBlock(BlockPos blockPos, BlockState state, int flags) {
        if (SectionPos.of(blockPos).equals(startPos)) {
            section.setBlockState(blockPos.getX() & 15, blockPos.getY() & 15, blockPos.getZ() & 15, state, false);
            if (state.hasBlockEntity()) {
                BlockEntity be = ((EntityBlock) state.getBlock()).newBlockEntity(blockPos, state);
                if (be != null) {
                    be.setLevel(this);
                    blockEntities.put(blockPos, be);
                }
            } else {
                blockEntities.remove(blockPos);
            }
            return true;
        }
        return realLevel.setBlock(blockPos, state, flags);
    }

    @Override
    public void sendBlockUpdated(BlockPos p_46612_, BlockState p_46613_, BlockState p_46614_, int p_46615_) {

    }

    @Override
    public @Nullable BlockEntity getBlockEntity(BlockPos blockPos) {
        if (SectionPos.of(blockPos).equals(startPos)) {
            return blockEntities.get(blockPos);
        }
        return realLevel.getBlockEntity(blockPos);
    }

    @Override
    public @Nullable Entity getEntity(int p_46492_) {
        return null;
    }

    @Override
    public TickRateManager tickRateManager() {
        return null;
    }

    @Override
    public @Nullable MapItemSavedData getMapData(MapId p_335212_) {
        return null;
    }

    @Override
    public void setMapData(MapId p_332598_, MapItemSavedData p_151534_) {

    }

    @Override
    public MapId getFreeMapId() {
        return null;
    }

    @Override
    public void destroyBlockProgress(int p_46506_, BlockPos p_46507_, int p_46508_) {

    }

    @Override
    public Scoreboard getScoreboard() {
        return null;
    }

    @Override
    public LevelTickAccess<Block> getBlockTicks() {
        return null;
    }

    @Override
    public LevelTickAccess<Fluid> getFluidTicks() {
        return null;
    }

    @Override
    public ChunkSource getChunkSource() {
        return realLevel.getChunkSource();
    }

    @Override
    public void levelEvent(@Nullable Player p_46771_, int p_46772_, BlockPos p_46773_, int p_46774_) {

    }

    @Override
    public void gameEvent(Holder<GameEvent> p_330236_, Vec3 p_220405_, GameEvent.Context p_220406_) {

    }

    @Override
    public List<? extends Player> players() {
        return realLevel.players();
    }

    @Override
    public RecipeManager getRecipeManager() {
        return realLevel.getRecipeManager();
    }

    @Override
    protected LevelEntityGetter<Entity> getEntities() {
        return null;
    }

    @Override
    public PotionBrewing potionBrewing() {
        return realLevel.potionBrewing();
    }

    @Override
    public FeatureFlagSet enabledFeatures() {
        return realLevel.enabledFeatures();
    }

    @Override
    public Holder<Biome> getUncachedNoiseBiome(int x, int y, int z) {
        return realLevel.getUncachedNoiseBiome(x, y, z);
    }

    @Override
    public float getShade(Direction p_45522_, boolean p_45523_) {
        return 0;
    }
}
