package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.util.Identifier;

import java.util.HashMap;

@SuppressWarnings("unused")
public record VariantType<T>(Identifier id, float costMultiplier) {
    private static final HashMap<Identifier, VariantType<?>> registry = new HashMap<>();
    public static final Endec<VariantType<?>> ENDEC = MinecraftEndecs.IDENTIFIER.xmap(registry::get, VariantType::id);

    public static final VariantType<ItemVariant> ITEM = register(Trickster.id("item"), 1);
    public static final VariantType<FluidVariant> FLUID = register(Trickster.id("fluid"), 1 / 100f);

    public static <T> VariantType<T> register(Identifier id, float costMultiplier) {
        var type = new VariantType<T>(id, costMultiplier);
        registry.put(id, type);
        return type;
    }
}
