package net.whale.inventory_dimension.render;

import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;

public class BlockRenderState {
    public static BlockState state = Blocks.DIRT.defaultBlockState();
    public static int selectedPropertyIndex = 0;
    public static Property<?> selectedProperty = null;

    public static void cycleSelectedProperty(boolean reverse) {
        List<Property<?>> props = new ArrayList<>(state.getProperties());
        if (props.isEmpty()) return;
        int size = props.size();
        selectedPropertyIndex = reverse
                ? (selectedPropertyIndex - 1 + size) % size
                : (selectedPropertyIndex + 1) % size;
        selectedProperty = props.get(selectedPropertyIndex);
        showMessage();
    }

    public static void swapSelectedProperty(boolean reverse) {
        if (selectedProperty == null || !state.getProperties().contains(selectedProperty)) return;
        cyclePropertyValue(state, selectedProperty, reverse);
    }

    private static <T extends Comparable<T>> void cyclePropertyValue(BlockState blockState, Property<T> prop, boolean reverse) {
        List<T> values = new ArrayList<>(prop.getPossibleValues());
        int index = values.indexOf(blockState.getValue(prop));
        index = reverse
                ? (index - 1 + values.size()) % values.size()
                : (index + 1) % values.size();
        state = blockState.setValue(prop, values.get(index));
        Minecraft.getInstance().player.playSound(
                net.minecraft.sounds.SoundEvents.UI_BUTTON_CLICK.value(), 0.5F, 1.2F
        );
        showMessage();
    }

    private static void showMessage() {
        if (selectedProperty == null) return;
        Minecraft.getInstance().gui.setOverlayMessage(
                net.minecraft.network.chat.Component.literal(
                        "§e" + selectedProperty.getName() +
                                " §7: §b" + state.getValue(selectedProperty).toString().toUpperCase()
                ), false
        );
    }
}