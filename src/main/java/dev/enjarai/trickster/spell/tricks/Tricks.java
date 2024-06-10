package dev.enjarai.trickster.spell.tricks;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;

public class Tricks {
    public static final RegistryKey<Registry<Trick>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("trick"));
    public static final Registry<Trick> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());
    private static final HashMap<Pattern, Trick> LOOKUP = new HashMap<>();

    public static final ExecuteTrick EXECUTE = register("execute", new ExecuteTrick());
    public static final LoadArgumentTrick LOAD_ARGUMENT_1 = register("load_argument_1", new LoadArgumentTrick(Pattern.of(4, 1), 0));
    public static final LoadArgumentTrick LOAD_ARGUMENT_2 = register("load_argument_2", new LoadArgumentTrick(Pattern.of(4, 2), 1));
    public static final LoadArgumentTrick LOAD_ARGUMENT_3 = register("load_argument_3", new LoadArgumentTrick(Pattern.of(4, 5), 2));
    public static final LoadArgumentTrick LOAD_ARGUMENT_4 = register("load_argument_4", new LoadArgumentTrick(Pattern.of(4, 8), 3));
    public static final LoadArgumentTrick LOAD_ARGUMENT_5 = register("load_argument_5", new LoadArgumentTrick(Pattern.of(4, 7), 4));
    public static final LoadArgumentTrick LOAD_ARGUMENT_6 = register("load_argument_6", new LoadArgumentTrick(Pattern.of(4, 6), 5));
    public static final LoadArgumentTrick LOAD_ARGUMENT_7 = register("load_argument_7", new LoadArgumentTrick(Pattern.of(4, 3), 6));
    public static final LoadArgumentTrick LOAD_ARGUMENT_8 = register("load_argument_8", new LoadArgumentTrick(Pattern.of(4, 0), 7));
    public static final RevealTrick REVEAL = register("reveal", new RevealTrick());
    public static final AddTrick ADD = register("add", new AddTrick());
    public static final SubtractTrick SUBTRACT = register("subtract", new SubtractTrick());
    public static final MultiplyTrick MULTIPLY = register("multiply", new MultiplyTrick());
    public static final DivideTrick DIVIDE = register("divide", new DivideTrick());
    public static final OnePonyTrick ONE = register("one", new OnePonyTrick());

    private static <T extends Trick> T register(String path, T trick) {
        LOOKUP.put(trick.getPattern(), trick);
        return Registry.register(REGISTRY, Trickster.id(path), trick);
    }

    @Nullable
    public static Trick lookup(Pattern pattern) {
        return LOOKUP.get(pattern);
    }

    public static void register() {
        // init the class :brombeere:
    }
}
