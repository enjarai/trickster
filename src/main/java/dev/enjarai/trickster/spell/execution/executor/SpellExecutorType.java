package dev.enjarai.trickster.spell.execution.executor;

import dev.enjarai.trickster.Trickster;
import io.wispforest.endec.StructEndec;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public record SpellExecutorType<T extends SpellExecutor>(StructEndec<T> endec) {
    public static final RegistryKey<Registry<SpellExecutorType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("spell_executor_type"));
    public static final Registry<SpellExecutorType<?>> REGISTRY = FabricRegistryBuilder.createSimple(REGISTRY_KEY).buildAndRegister();

    public static final SpellExecutorType<DefaultSpellExecutor> DEFAULT = register("default", DefaultSpellExecutor.ENDEC);
    public static final SpellExecutorType<TryCatchSpellExecutor> TRY_CATCH = register("try_catch", TryCatchSpellExecutor.ENDEC);
    public static final SpellExecutorType<ErroredSpellExecutor> ERRORED = register("errored", ErroredSpellExecutor.ENDEC);
    public static final SpellExecutorType<AtomicSpellExecutor> ATOMIC = register("atomic", AtomicSpellExecutor.ENDEC);
    public static final SpellExecutorType<FoldingSpellExecutor> FOLDING = register("folding", FoldingSpellExecutor.ENDEC);
    public static final SpellExecutorType<MessageListenerSpellExecutor> MESSAGE_LISTENER = register("message_listener", MessageListenerSpellExecutor.ENDEC);

    private static <T extends SpellExecutor> SpellExecutorType<T> register(String name, StructEndec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new SpellExecutorType<>(codec));
    }

    public static void register() {
        // init the class :brombeere:
    }
}
