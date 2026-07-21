package net.whale.inventory_dimension.update;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;
import net.whale.inventory_dimension.mixin.LevelRendererMixin;


public class UpdateLevel {
    public static void updateSection(SectionPos sectionPos, Level level, LevelRenderer renderer) {
        BlockPos.betweenClosedStream(sectionPos.minBlockX(), sectionPos.minBlockY(), sectionPos.minBlockZ(),
                        sectionPos.maxBlockX(), sectionPos.maxBlockY(), sectionPos.maxBlockZ())
                .forEach(level.getLightEngine()::checkBlock);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 1) continue;
                    ((LevelRendererMixin.LevelRendererAccessor) renderer).invokeSetSectionDirty(
                            sectionPos.getX() + i,
                            sectionPos.getY() + j,
                            sectionPos.getZ() + k,
                            true);
                }
            }
        }
    }
    public static void updateBlock(BlockPos pos, Level level, LevelRenderer renderer) {
        level.getLightEngine().checkBlock(pos);
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                for (int k = -1; k <= 1; k++) {
                    if (Math.abs(i) + Math.abs(j) + Math.abs(k) > 1) continue;
                    ((LevelRendererMixin.LevelRendererAccessor) renderer).invokeSetSectionDirty(
                            SectionPos.blockToSectionCoord(pos.getX() + i),
                            SectionPos.blockToSectionCoord(pos.getY() + j),
                            SectionPos.blockToSectionCoord(pos.getZ() + k),
                            true);
                }
            }
        }
    }
}
