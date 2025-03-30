package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Lifecycle;
import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;
import java.util.OptionalInt;

public record FragmentType<T extends Fragment>(StructEndec<T> endec, OptionalInt color) implements ArgType<T> {
    public static final RegistryKey<Registry<FragmentType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fragment_type"));
    public static final Int2ObjectMap<Identifier> INT_ID_FALLBACK = new Int2ObjectOpenHashMap<>() {
        {
            put(-777274987, Trickster.id("entity_type"));
            put(-2055452291, Trickster.id("slot"));
            put(719778857, Trickster.id("spell_part"));
            put(1201617608, Trickster.id("number"));
            put(1343995792, Trickster.id("string"));
            put(94838885, Trickster.id("item_type"));
            put(-839744897, Trickster.id("pattern_literal"));
            put(937706338, Trickster.id("entity"));
            put(-2055409991, Trickster.id("type"));
            put(-1943319220, Trickster.id("zalgo"));
            put(1415594178, Trickster.id("vector"));
            put(-772426965, Trickster.id("block_type"));
            put(-2058877733, Trickster.id("map"));
            put(1444891407, Trickster.id("pattern"));
            put(1140968677, Trickster.id("dimension"));
            put(-2055360237, Trickster.id("void"));
            put(-2055663587, Trickster.id("list"));
            put(-1994273881, Trickster.id("boolean"));
        }
    };
    public static final Int2ObjectMap<Identifier> INT_ID_LOOKUP = new Int2ObjectOpenHashMap<>();
    public static final Endec<FragmentType<?>> INT_ID_ENDEC = Endec.INT.xmap(FragmentType::getFromInt, FragmentType::getIntId);
    public static final Registry<FragmentType<?>> REGISTRY = FabricRegistryBuilder.from(new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<FragmentType<?>> add(RegistryKey<FragmentType<?>> key, FragmentType<?> value, RegistryEntryInfo info) {
            var hash = key.getValue().hashCode();
            if (INT_ID_LOOKUP.containsKey(hash)) {
                Trickster.LOGGER.error(
                        "WARNING: Hashcode collision between two fragment types, spell imports and exports may not work as expected. ({} overrode {})",
                        key.getValue(), INT_ID_LOOKUP.get(hash)
                );
                throw new IllegalStateException("WARNING: Hashcode collision between two fragment types");
            }

            INT_ID_LOOKUP.put(hash, key.getValue());
            return super.add(key, value, info);
        }
    }).buildAndRegister();

    public static final FragmentType<TypeFragment> TYPE = register("type", TypeFragment.ENDEC, 0x66cc00);
    public static final FragmentType<NumberFragment> NUMBER = register("number", NumberFragment.ENDEC, 0xddaa00);
    public static final FragmentType<BooleanFragment> BOOLEAN = register("boolean", BooleanFragment.ENDEC, 0xaa3355);
    public static final FragmentType<VectorFragment> VECTOR = register("vector", VectorFragment.ENDEC, 0xaa7711);
    public static final FragmentType<ListFragment> LIST = register("list", ListFragment.ENDEC);
    public static final FragmentType<VoidFragment> VOID = register("void", VoidFragment.ENDEC, 0x4400aa);
    public static final FragmentType<PatternGlyph> PATTERN = register("pattern", PatternGlyph.ENDEC, 0x6644aa);
    public static final FragmentType<Pattern> PATTERN_LITERAL = register(
            "pattern_literal",
            EndecTomfoolery.funnyFieldOf(Pattern.ENDEC, "pattern"), 0xbbbbaa
    );
    public static final FragmentType<SpellPart> SPELL_PART = register("spell_part", SpellPart.ENDEC, 0xaa44aa);
    public static final FragmentType<EntityFragment> ENTITY = register("entity", EntityFragment.ENDEC, 0x338888);
    public static final FragmentType<ZalgoFragment> ZALGO = register("zalgo", ZalgoFragment.ENDEC, 0x444444);
    public static final FragmentType<ItemTypeFragment> ITEM_TYPE = register(
            "item_type", ItemTypeFragment.ENDEC,
            0x2266aa
    );
    public static final FragmentType<SlotFragment> SLOT = register("slot", SlotFragment.ENDEC, 0x77aaee);
    public static final FragmentType<BlockTypeFragment> BLOCK_TYPE = register(
            "block_type", BlockTypeFragment.ENDEC,
            0x44aa33
    );
    public static final FragmentType<EntityTypeFragment> ENTITY_TYPE = register(
            "entity_type", EntityTypeFragment.ENDEC,
            0x8877bb
    );
    public static final FragmentType<DimensionFragment> DIMENSION = register(
            "dimension", DimensionFragment.ENDEC,
            0xdd55bb
    );
    public static final FragmentType<StringFragment> STRING = register("string", StringFragment.ENDEC, 0xaabb77);
    public static final FragmentType<MapFragment> MAP = register("map", MapFragment.ENDEC);

    private static <T extends Fragment> FragmentType<T> register(String name, StructEndec<T> codec, int color) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec, OptionalInt.of(color)));
    }

    private static <T extends Fragment> FragmentType<T> register(String name, StructEndec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec, OptionalInt.of(0xaaaaaa)));
    }

    public static void register() {
        // init the class :brombeere:
    }

    public MutableText getName() {
        var id = REGISTRY.getId(this);
        if (id == null) {
            return Text.literal("Unregistered");
        }
        var text = Text.translatable(Trickster.MOD_ID + ".fragment." + id.getNamespace() + "." + id.getPath());
        if (color.isPresent()) {
            text = text.withColor(color.getAsInt());
        }
        return text;
    }

    @Override
    public MutableText asText() {
        return getName();
    }

    public static FragmentType<?> getFromInt(int intId) {
        var id = INT_ID_LOOKUP.get(intId);
        if (id == null) {
            id = INT_ID_FALLBACK.get(intId);

            if (id == null) {
                throw new IllegalArgumentException("Not a valid int id for fragment type: " + intId);
            }
        }

        return REGISTRY.get(id);
    }

    public int getIntId() {
        return REGISTRY.getId(this).hashCode();
    }

    @Override
    public int argc(List<Fragment> fragments) {
        return 1;
    }

    @Override
    @SuppressWarnings("unchecked")
    public T compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
        return (T) fragments.get(0);
    }

    @Override
    public boolean match(List<Fragment> fragments) {
        return fragments.get(0).type() == this;
    }

    @Override
    public ArgType<T> wardOf() {
        return new ArgType<>() {
            @Override
            public int argc(List<Fragment> fragments) {
                return FragmentType.this.argc(fragments);
            }

            @Override
            public T compose(Trick<?> trick, SpellContext ctx, List<Fragment> fragments) {
                var result = FragmentType.this.compose(trick, ctx, fragments);

                if (result instanceof EntityFragment entity) {
                    ArgType.tryWard(trick, ctx, entity, fragments);
                }

                return result;
            }

            @Override
            public boolean match(List<Fragment> fragments) {
                return FragmentType.this.match(fragments);
            }

            @Override
            public ArgType<T> wardOf() {
                return this;
            }

            @Override
            public MutableText asText() {
                return FragmentType.this.asText();
            }
        };
    }
}
