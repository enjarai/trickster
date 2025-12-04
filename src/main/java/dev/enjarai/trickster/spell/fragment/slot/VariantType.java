package dev.enjarai.trickster.spell.fragment.slot;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.Endec;
import io.wispforest.owo.serialization.endec.MinecraftEndecs;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.minecraft.util.Identifier;

import java.util.HashMap;

@SuppressWarnings("unused")
public abstract class VariantType<T> {
    private static final HashMap<Identifier, VariantType<?>> registry = new HashMap<>();
    public static final Endec<VariantType<?>> ENDEC = MinecraftEndecs.IDENTIFIER.xmap(registry::get, v -> v.id);

    public static final VariantType<ItemVariant> ITEM = register(new VariantType<>(Trickster.id("item"), 1) {
        @Override
        public ResourceVariantFragment<ItemVariant> fragmentFromResource(ItemVariant resource) {
            return new ItemTypeFragment(resource.getItem());
        }
    });
    public static final VariantType<FluidVariant> FLUID = register(new VariantType<>(Trickster.id("fluid"), 1 / 100f) {
        @Override
        public ResourceVariantFragment<FluidVariant> fragmentFromResource(FluidVariant resource) {
            return new FluidTypeFragment(resource.getFluid());
        }
    });

    public final Identifier id;
    public final float costMultiplier;

    public VariantType(Identifier id, float costMultiplier) {
        this.id = id;
        this.costMultiplier = costMultiplier;
    }

    public abstract ResourceVariantFragment<T> fragmentFromResource(T resource);

    public static <T> VariantType<T> register(VariantType<T> type) {
        registry.put(type.id, type);
        return type;
    }
}
