package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

public record FragmentType<T extends Fragment>(MapCodec<T> codec) {
    public static final RegistryKey<Registry<FragmentType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fragment_type"));
    public static final Registry<FragmentType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final FragmentType<NumberFragment> NUMBER = register("number", NumberFragment.CODEC);
    public static final FragmentType<ListFragment> LIST = register("list", ListFragment.CODEC);

    private static <T extends Fragment> FragmentType<T> register(String name, MapCodec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec));
    }

    public MutableText getName() {
        var id = REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        return Text.translatable(Trickster.MOD_ID + ".fragment." + id.getNamespace() + "." + id.getPath());
    }
}
