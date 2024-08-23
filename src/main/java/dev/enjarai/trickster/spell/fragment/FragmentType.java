package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import io.wispforest.endec.StructEndec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryInfo;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.OptionalInt;

public record FragmentType<T extends Fragment>(StructEndec<T> endec, OptionalInt color) {
    public static final RegistryKey<Registry<FragmentType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fragment_type"));
    public static final Int2ObjectMap<Identifier> INT_ID_LOOKUP = new Int2ObjectOpenHashMap<>();
    public static final Registry<FragmentType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable()) {
        @Override
        public RegistryEntry.Reference<FragmentType<?>> add(RegistryKey<FragmentType<?>> key, FragmentType<?> value, RegistryEntryInfo info) {
            var hash = key.getValue().hashCode();
            if (INT_ID_LOOKUP.containsKey(hash)) {
                Trickster.LOGGER.warn(
                        "WARNING: Hashcode collision between two fragment types, spell imports and exports may not work as expected. ({} overrode {})",
                        key.getValue(), INT_ID_LOOKUP.get(hash)
                );
            }

            INT_ID_LOOKUP.put(hash, key.getValue());
            return super.add(key, value, info);
        }
    };

    public static final FragmentType<TypeFragment> TYPE = register("type", TypeFragment.ENDEC, 0x66cc00);
    public static final FragmentType<NumberFragment> NUMBER = register("number", NumberFragment.ENDEC, 0xddaa00);
    public static final FragmentType<BooleanFragment> BOOLEAN = register("boolean", BooleanFragment.ENDEC, 0xaa3355);
    public static final FragmentType<VectorFragment> VECTOR = register("vector", VectorFragment.ENDEC);
    public static final FragmentType<ListFragment> LIST = register("list", ListFragment.ENDEC);
    public static final FragmentType<VoidFragment> VOID = register("void", VoidFragment.ENDEC, 0x4400aa);
    public static final FragmentType<PatternGlyph> PATTERN = register("pattern", PatternGlyph.ENDEC, 0x6644aa);
    public static final FragmentType<SpellPart> SPELL_PART = register("spell_part", SpellPart.ENDEC, 0xaa44aa);
    public static final FragmentType<EntityFragment> ENTITY = register("entity", EntityFragment.ENDEC, 0x338888);
    public static final FragmentType<ZalgoFragment> ZALGO = register("zalgo", ZalgoFragment.ENDEC, 0x444444);
    public static final FragmentType<ItemTypeFragment> ITEM_TYPE = register("item_type", ItemTypeFragment.ENDEC, 0x2266aa);
    public static final FragmentType<SlotFragment> SLOT = register("slot", SlotFragment.ENDEC, 0x77aaee);
    public static final FragmentType<BlockTypeFragment> BLOCK_TYPE = register("block_type", BlockTypeFragment.ENDEC, 0x44aa33);
    public static final FragmentType<EntityTypeFragment> ENTITY_TYPE = register("entity_type", EntityTypeFragment.ENDEC, 0x8877bb);
    public static final FragmentType<DimensionFragment> DIMENSION = register("dimension", DimensionFragment.ENDEC, 0xdd55bb);
    public static final FragmentType<StringFragment> STRING = register("string", StringFragment.ENDEC, 0xaabb77);

    private static <T extends Fragment> FragmentType<T> register(String name, StructEndec<T> codec, int color) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec, OptionalInt.of(color)));
    }

    private static <T extends Fragment> FragmentType<T> register(String name, StructEndec<T> codec) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec, OptionalInt.empty()));
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

    public static FragmentType<?> getFromInt(int intId) {
        var id = INT_ID_LOOKUP.get(intId);
        if (id == null) {
            throw new IllegalArgumentException("Not a valid int id for fragment type");
        }

        return REGISTRY.get(id);
    }

    public int getIntId() {
        return REGISTRY.getId(this).hashCode();
    }
}
