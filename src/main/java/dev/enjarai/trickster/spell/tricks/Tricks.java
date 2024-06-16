package dev.enjarai.trickster.spell.tricks;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.tricks.bool.*;
import dev.enjarai.trickster.spell.tricks.event.CreateSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.event.DeleteSpellCircleTrick;
import dev.enjarai.trickster.spell.tricks.list.*;
import dev.enjarai.trickster.spell.tricks.math.*;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Tricks {
    private static final Map<Pattern, Trick> LOOKUP = new HashMap<>();

    public static final RegistryKey<Registry<Trick>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("trick"));
    public static final Registry<Trick> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<Trick> add(RegistryKey<Trick> key, Trick value, RegistryEntryInfo info) {
            LOOKUP.put(value.getPattern(), value);
            return super.add(key, value, info);
        }
    };

    // Meta-programming
    public static final ExecuteTrick EXECUTE = register("execute", new ExecuteTrick());
    public static final LoadArgumentTrick LOAD_ARGUMENT_1 = register("load_argument_1", new LoadArgumentTrick(Pattern.of(4, 1), 0));
    public static final LoadArgumentTrick LOAD_ARGUMENT_2 = register("load_argument_2", new LoadArgumentTrick(Pattern.of(4, 2), 1));
    public static final LoadArgumentTrick LOAD_ARGUMENT_3 = register("load_argument_3", new LoadArgumentTrick(Pattern.of(4, 5), 2));
    public static final LoadArgumentTrick LOAD_ARGUMENT_4 = register("load_argument_4", new LoadArgumentTrick(Pattern.of(4, 8), 3));
    public static final LoadArgumentTrick LOAD_ARGUMENT_5 = register("load_argument_5", new LoadArgumentTrick(Pattern.of(4, 7), 4));
    public static final LoadArgumentTrick LOAD_ARGUMENT_6 = register("load_argument_6", new LoadArgumentTrick(Pattern.of(4, 6), 5));
    public static final LoadArgumentTrick LOAD_ARGUMENT_7 = register("load_argument_7", new LoadArgumentTrick(Pattern.of(4, 3), 6));
    public static final LoadArgumentTrick LOAD_ARGUMENT_8 = register("load_argument_8", new LoadArgumentTrick(Pattern.of(4, 0), 7));

    // Basic
    public static final OnePonyTrick TWO = register("two", new OnePonyTrick());
    public static final RevealTrick REVEAL = register("reveal", new RevealTrick());
    public static final ReflectionTrick REFLECTION = register("reflection", new ReflectionTrick());
    public static final ReadSpellTrick READ_SPELL = register("read_spell", new ReadSpellTrick());
    public static final WriteSpellTrick WRITE_SPELL = register("write_spell", new WriteSpellTrick());

    // Math
    public static final AddTrick ADD = register("add", new AddTrick());
    public static final SubtractTrick SUBTRACT = register("subtract", new SubtractTrick());
    public static final MultiplyTrick MULTIPLY = register("multiply", new MultiplyTrick());
    public static final DivideTrick DIVIDE = register("divide", new DivideTrick());
    public static final ModuloTrick MODULO = register("modulo", new ModuloTrick());
    public static final FloorTrick FLOOR = register("floor", new FloorTrick());
    public static final CeilTrick CEIL = register("ceil", new CeilTrick());
    public static final RoundTrick ROUND = register("round", new RoundTrick());
    public static final MaxTrick MAX = register("max", new MaxTrick());
    public static final MinTrick MIN = register("min", new MinTrick());

    // Boolean
    public static final IfElseTrick IF_ELSE = register("if_else", new IfElseTrick());
    public static final EqualsTrick EQUALS = register("equals", new EqualsTrick());
    public static final NotEqualsTrick NOT_EQUALS = register("not_equals", new NotEqualsTrick());
    public static final AllTrick ALL = register("all", new AllTrick());
    public static final AnyTrick ANY = register("any", new AnyTrick());
    public static final NoneTrick NONE = register("none", new NoneTrick());

    // List
    public static final ListAddTrick LIST_ADD = register("list_add", new ListAddTrick());
    public static final ListGetTrick LIST_GET = register("list_get", new ListGetTrick());
    public static final ListIndexOfTrick LIST_INDEX_OF = register("list_index_of", new ListIndexOfTrick());
    public static final ListInsertTrick LIST_INSERT = register("list_insert", new ListInsertTrick());
    public static final ListRemoveElementTrick LIST_REMOVE_ELEMENT = register("list_remove_element", new ListRemoveElementTrick());
    public static final ListRemoveTrick LIST_REMOVE = register("list_remove", new ListRemoveTrick());

    // Events
    public static final CreateSpellCircleTrick CREATE_SPELL_CIRCLE = register("create_spell_circle", new CreateSpellCircleTrick());
    public static final DeleteSpellCircleTrick DELETE_SPELL_CIRCLE = register("delete_spell_circle", new DeleteSpellCircleTrick());

    private static <T extends Trick> T register(String path, T trick) {
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
