package dev.enjarai.trickster.spell;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.Trickster;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record SpellContextType<T extends SpellContext>(MapCodec<T> codec) {
    public static final RegistryKey<Registry<SpellContextType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("spell_ctx_type"));
    public static final Registry<SpellContextType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final SpellContextType<PlayerSpellContext> PLAYER = register("player", PlayerSpellContext.CODEC);
    public static final SpellContextType<BlockSpellContext> BLOCK = register("block", BlockSpellContext.CODEC);

    private static <T extends SpellContext> SpellContextType<T> register(String name, MapCodec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new SpellContextType<>(codec));
    }
}
