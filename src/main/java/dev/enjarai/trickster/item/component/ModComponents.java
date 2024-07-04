package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.Trickster;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class ModComponents {
    public static final ComponentType<SpellComponent> SPELL =
            register("spell", builder -> builder.codec(SpellComponent.CODEC).cache());
    public static final ComponentType<SelectedSlotComponent> SELECTED_SLOT =
            register("selected_slot", builder -> builder.codec(SelectedSlotComponent.CODEC).cache());
    public static final ComponentType<WrittenScrollMetaComponent> WRITTEN_SCROLL_META =
            register("selected_slot", builder -> builder
                    .codec(WrittenScrollMetaComponent.CODEC)
                    .packetCodec(WrittenScrollMetaComponent.PACKET_CODEC)
                    .cache());

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Trickster.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static void register() {

    }
}
