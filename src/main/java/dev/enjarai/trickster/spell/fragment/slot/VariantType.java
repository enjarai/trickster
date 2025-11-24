package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.Trickster;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.util.Identifier;

public record VariantType<T>(Identifier id) {
    public static final VariantType<ItemVariant> ITEM = register(Trickster.id("item"));

    public static <T> VariantType<T> register(Identifier id) {

    }
}
