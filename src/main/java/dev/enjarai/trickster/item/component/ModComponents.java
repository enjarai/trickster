package dev.enjarai.trickster.item.component;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import java.util.function.UnaryOperator;

public class ModComponents {
    public static final ComponentType<FragmentComponent> FRAGMENT = register(
            "spell", builder -> builder
                    .codec(EndecTomfoolery.toCodec(FragmentComponent.ENDEC))
                    .cache()
    );
    public static final ComponentType<SelectedSlotComponent> SELECTED_SLOT = register(
            "selected_slot", builder -> builder
                    .codec(SelectedSlotComponent.CODEC)
                    .cache()
    );
    public static final ComponentType<WrittenScrollMetaComponent> WRITTEN_SCROLL_META = register(
            "written_scroll_meta", builder -> builder
                    .codec(WrittenScrollMetaComponent.CODEC)
                    .packetCodec(WrittenScrollMetaComponent.PACKET_CODEC)
                    .cache()
    );
    public static final ComponentType<EntityStorageComponent> ENTITY_STORAGE = register(
            "entity_storage", builder -> builder
                    .codec(EntityStorageComponent.CODEC)
                    .cache()
    );
    public static final ComponentType<ManaComponent> MANA = register(
            "mana", builder -> builder
                    .codec(ManaComponent.CODEC)
                    .cache()
    );
    public static final ComponentType<CollarLinkComponent> COLLAR_LINK = register(
            "collar_link", builder -> builder
                    .endec(CollarLinkComponent.ENDEC)
                    .cache()
    );

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, Trickster.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }

    public static void register() {
        FragmentComponent.registerWriteConversion(Items.BOOK,
                stack -> stack.withItem(Items.ENCHANTED_BOOK)
        );
        FragmentComponent.registerResetConversion(Items.ENCHANTED_BOOK,
                stack -> stack.get(DataComponentTypes.STORED_ENCHANTMENTS) instanceof ItemEnchantmentsComponent enchants && enchants.isEmpty()
                        ? stack.withItem(Items.BOOK)
                        : stack
        );
    }
}
