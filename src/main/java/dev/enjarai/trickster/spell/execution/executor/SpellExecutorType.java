package dev.enjarai.trickster.spell.execution.executor;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;

public record SpellExecutorType<T extends SpellExecutor>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<SpellExecutorType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("spell_executor_type"));
    public static final Registry<SpellExecutorType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final SpellExecutorType<DefaultSpellExecutor> DEFAULT = register("default", DefaultSpellExecutor.ENDEC);
    public static final SpellExecutorType<IteratorSpellExecutor> ITERATOR = register("iterator", IteratorSpellExecutor.ENDEC);
    public static final SpellExecutorType<TryCatchSpellExecutor> TRY_CATCH = register("try_catch", TryCatchSpellExecutor.ENDEC);
    public static final SpellExecutorType<ErroredSpellExecutor> ERRORED = register("errored", ErroredSpellExecutor.ENDEC);

    private static <T extends SpellExecutor> SpellExecutorType<T> register(String name, StructEndec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new SpellExecutorType<>(codec));
    }
}
