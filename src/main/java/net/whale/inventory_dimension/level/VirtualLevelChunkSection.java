package net.whale.inventory_dimension.level;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeResolver;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.PalettedContainer;
import net.minecraft.world.level.chunk.PalettedContainerRO;
import net.minecraft.world.level.material.FluidState;
import net.whale.inventory_dimension.access.PlayerInterface;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

public class VirtualLevelChunkSection extends LevelChunkSection {
    private final LevelChunkSection realSection;
    private final LevelChunkSection virtualSection;

    public VirtualLevelChunkSection(Registry<Biome> p_282873_, LevelChunkSection realSection, LevelChunkSection virtualSection) {
        super(p_282873_);
        this.realSection = realSection;
        this.virtualSection = virtualSection;
    }

    public LevelChunkSection getRealSection() {
        return realSection;
    }

    @Override
    public boolean hasOnlyAir() {
        return false;
    }

    @Override
    public boolean isRandomlyTicking() {
        return false;
    }

    @Override
    public boolean isRandomlyTickingBlocks() {
        return false;
    }

    @Override
    public boolean isRandomlyTickingFluids() {
        return false;
    }

    @Override
    public @NotNull Holder<Biome> getNoiseBiome(int p_204434_, int p_204435_, int p_204436_) {
        return realSection.getNoiseBiome(p_204434_, p_204435_, p_204436_);
    }

    @Override
    public @NotNull PalettedContainerRO<Holder<Biome>> getBiomes() {
        return realSection.getBiomes();
    }

    @Override
    public int getSerializedSize() {
        return realSection.getSerializedSize();
    }

    @Override
    public void fillBiomesFromNoise(BiomeResolver p_282075_, Climate.Sampler p_283084_, int p_282310_, int p_281510_, int p_283057_) {
        realSection.fillBiomesFromNoise(p_282075_, p_283084_, p_282310_, p_281510_, p_283057_);
    }

    @Override
    public void read(FriendlyByteBuf p_63005_) {
        realSection.read(p_63005_);
    }

    @Override
    public void readBiomes(FriendlyByteBuf p_275669_) {
        realSection.readBiomes(p_275669_);
    }

    @Override
    public void write(FriendlyByteBuf p_63012_) {
        realSection.write(p_63012_);
    }

    @Override
    public BlockState setBlockState(int p_62987_, int p_62988_, int p_62989_, BlockState p_62990_) {
        Player player = Minecraft.getInstance().player;
        if (player != null && ((PlayerInterface) player).inventoryDimension$getEditingVirtual()){
            return virtualSection.setBlockState(p_62987_,p_62988_,p_62989_,p_62990_);
        }
        return realSection.setBlockState(p_62987_, p_62988_, p_62989_, p_62990_);
    }

    @Override
    public @NotNull BlockState setBlockState(int p_62992_, int p_62993_, int p_62994_, BlockState p_62995_, boolean p_62996_) {
        Player player = Minecraft.getInstance().player;
        if (player != null && ((PlayerInterface) player).inventoryDimension$getEditingVirtual()){
            return virtualSection.setBlockState(p_62992_,p_62993_,p_62994_,p_62995_,p_62996_);
        }
        return realSection.setBlockState(p_62992_, p_62993_, p_62994_, p_62995_, p_62996_);
    }

    public LevelChunkSection getVirtualSection() {
        return virtualSection;
    }

    @Override
    public @NotNull BlockState getBlockState(int p_62983_, int p_62984_, int p_62985_) {
        return virtualSection.getBlockState(p_62983_, p_62984_, p_62985_);
    }

    @Override
    public @NotNull FluidState getFluidState(int p_63008_, int p_63009_, int p_63010_) {
        return virtualSection.getFluidState(p_63008_, p_63009_, p_63010_); ////Sollte vllt noch implementiert werden, ka
    }

    @Override
    public @NotNull PalettedContainer<BlockState> getStates() {
        return virtualSection.getStates();
    }

    @Override
    public boolean maybeHas(Predicate<BlockState> p_63003_) {
        return virtualSection.maybeHas(p_63003_);
    }

    @Override
    public void recalcBlockCounts() {
        virtualSection.recalcBlockCounts();
    }

    @Override
    public void acquire() {
        Player player = Minecraft.getInstance().player;
        if (player != null && ((PlayerInterface) player).inventoryDimension$getEditingVirtual()){
            virtualSection.acquire();
            return;
        }
        realSection.acquire();
    }

    @Override
    public void release() {
        Player player = Minecraft.getInstance().player;
        if (player != null && ((PlayerInterface) player).inventoryDimension$getEditingVirtual()){
            virtualSection.release();
            return;
        }
        realSection.release();
    }
}
