package net.whale.inventory_dimension.save;

import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.PalettedContainer;

public class SectionBlockStateData {
    public static final Codec<PalettedContainer<BlockState>> BLOCK_STATES_CODEC =
            PalettedContainer.codecRW(
                    Block.BLOCK_STATE_REGISTRY,
                    BlockState.CODEC,
                    PalettedContainer.Strategy.SECTION_STATES,
                    Blocks.AIR.defaultBlockState()
            );
    private PalettedContainer<BlockState> states;

    public SectionBlockStateData() {
        this.states = new PalettedContainer<>(Block.BLOCK_STATE_REGISTRY, Blocks.AIR.defaultBlockState(), PalettedContainer.Strategy.SECTION_STATES);
        setBlockStates(states);
    }
    public boolean isWallPos(int x, int y, int z) {
        // Hauptkörper: volle Außenhülle 1–14 in XZ, 0/9 in Y
        boolean inMainRange = x >= 1 && x <= 14 && z >= 1 && z <= 14 && y >= 0 && y <= 9;
        //
        boolean notMainPart = x >= 7 && x <= 8 && y >= 4 && y <= 5 && z == 1;
        boolean isMain   = inMainRange && (x == 1 || x == 14 || z == 1 || z == 14 || y == 0 || y == 9) && !notMainPart;

        // Deckel: 1–14 in XZ, 10–13 in Y (voller Block)
        boolean isLid = x >= 1 && x <= 14 && z >= 1 && z <= 14 && y >= 10 && y <= 13;

        // Schloss: X 7–8, Y 7–10, Z 0
        boolean isLock = x >= 7 && x <= 8 && y >= 7 && y <= 10 && z == 0;

        return isMain || isLid || isLock;
    }
    private void setBlockStates(PalettedContainer<BlockState> palettedContainer) {
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                for (int k = 0; k < 16; k++) {
                    if (isWallPos(i,j,k)) {
                        palettedContainer.set(i,j,k,Blocks.BARRIER.defaultBlockState());
                    }
                }
            }
        }
    }

    public void setSection(PalettedContainer<BlockState> states) {
        this.states = states;
    }

    public PalettedContainer<BlockState> getSection() {
        return this.states;
    }

    public void save(CompoundTag tag) {
        if (states != null) {
            BLOCK_STATES_CODEC.encodeStart(NbtOps.INSTANCE, states)
                    .result()
                    .ifPresent(containerTag -> tag.put("BlockStatesContainer", containerTag));
        }
    }

    public void load(CompoundTag tag) {
        if (tag.contains("BlockStatesContainer")) {
            Tag containerTag = tag.get("BlockStatesContainer");

            BLOCK_STATES_CODEC.parse(NbtOps.INSTANCE, containerTag)
                    .result()
                    .ifPresent(parsedContainer -> {
                        states = parsedContainer;
                    });
        }
    }
}
