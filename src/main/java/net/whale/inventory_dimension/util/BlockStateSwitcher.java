package net.whale.inventory_dimension.util;

import net.minecraft.core.Direction;
import net.minecraft.core.Direction.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.*;

public class BlockStateSwitcher {
    public BlockStateSwitcher(){

    }
    public BlockState rotatePropertyY(BlockState state) {
        for(Property<?> prop : state.getProperties()) {
            if(prop instanceof DirectionProperty dirProp) {
                Direction current = state.getValue(dirProp);
                state = state.setValue(dirProp, current.getClockWise());
            } else if(prop instanceof EnumProperty<?> enumProp) {
                if(enumProp.getValueClass() == Direction.class) {
                    Direction current = state.getValue((EnumProperty<Direction>) enumProp);
                    state = state.setValue((EnumProperty<Direction>) enumProp, current.getClockWise());
                }
            }
        }
        return state;
    }

    public BlockState togglePropertyUpDown(BlockState state) {
        for(Property<?> prop : state.getProperties()) {
            if(prop instanceof EnumProperty<?> enumProp) {
                Class<?> type = enumProp.getValueClass();
                if(type == Half.class) {
                    Half half = state.getValue((EnumProperty<Half>) enumProp);
                    state = state.setValue((EnumProperty<Half>) enumProp, half == Half.TOP ? Half.BOTTOM : Half.TOP);
                } else if(type == AttachFace.class) {
                    AttachFace face = state.getValue((EnumProperty<AttachFace>) enumProp);
                    state = state.setValue((EnumProperty<AttachFace>) enumProp, nextAttachFace(face));
                } else if(type == SlabType.class) {
                    SlabType slab = state.getValue((EnumProperty<SlabType>) enumProp);
                    state = state.setValue((EnumProperty<SlabType>) enumProp, slab == SlabType.TOP ? SlabType.BOTTOM : SlabType.TOP);
                }
            }
        }
        return state;
    }

    private AttachFace nextAttachFace(AttachFace face) {
        return switch(face) {
            case FLOOR -> AttachFace.CEILING;
            case CEILING -> AttachFace.FLOOR;
            case WALL -> AttachFace.FLOOR; // optional, beliebig
        };
    }
    public BlockState toggleOtherVisualProperties(BlockState state) {
        for(Property<?> prop : state.getProperties()) {
            if(prop == BlockStateProperties.POWERED) continue;

            if(prop instanceof BooleanProperty boolProp) {
                state = state.setValue(boolProp, !state.getValue(boolProp));
            } else if(prop instanceof EnumProperty<?> enumProp) {
                Class<?> type = enumProp.getValueClass();
                if(type == StairsShape.class) {
                    StairsShape shape = state.getValue((EnumProperty<StairsShape>) enumProp);
                    state = state.setValue((EnumProperty<StairsShape>) enumProp, nextStairsShape(shape));
                } else if(type == DoorHingeSide.class) {
                    DoorHingeSide hinge = state.getValue((EnumProperty<DoorHingeSide>) enumProp);
                    state = state.setValue((EnumProperty<DoorHingeSide>) enumProp, hinge == DoorHingeSide.LEFT ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);
                } else if(type == Direction.Axis.class) {
                    Axis axis = state.getValue((EnumProperty<Axis>) enumProp);
                    state = state.setValue((EnumProperty<Axis>) enumProp, nextAxis(axis));
                }
            }
        }
        return state;
    }
    private StairsShape nextStairsShape(StairsShape current) {
        return switch(current) {
            case STRAIGHT -> StairsShape.INNER_LEFT;
            case INNER_LEFT -> StairsShape.INNER_RIGHT;
            case INNER_RIGHT -> StairsShape.OUTER_LEFT;
            case OUTER_LEFT -> StairsShape.OUTER_RIGHT;
            case OUTER_RIGHT -> StairsShape.STRAIGHT;
        };
    }

    private Axis nextAxis(Axis current) {
        return switch(current) {
            case X -> Axis.Y;
            case Y -> Axis.Z;
            case Z -> Axis.X;
        };
    }
}
