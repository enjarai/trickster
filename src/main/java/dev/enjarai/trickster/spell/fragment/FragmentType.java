package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.PatternGlyph;
import dev.enjarai.trickster.spell.SpellPart;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.OptionalInt;

public record FragmentType<T extends Fragment>(MapCodec<T> codec, OptionalInt color) {
    public static final RegistryKey<Registry<FragmentType<?>>> REGISTRY_KEY = RegistryKey.ofRegistry(Trickster.id("fragment_type"));
    public static final Registry<FragmentType<?>> REGISTRY = new SimpleRegistry<>(REGISTRY_KEY, Lifecycle.stable());

    public static final FragmentType<NumberFragment> NUMBER = register("number", NumberFragment.CODEC, 0xddaa00);
    public static final FragmentType<BooleanFragment> BOOLEAN = register("boolean", BooleanFragment.CODEC, 0xaa3355);
    public static final FragmentType<VectorFragment> VECTOR = register("vector", VectorFragment.CODEC);
    public static final FragmentType<ListFragment> LIST = register("list", ListFragment.CODEC);
    public static final FragmentType<VoidFragment> VOID = register("void", VoidFragment.CODEC, 0x4400aa);
    public static final FragmentType<PatternGlyph> PATTERN = register("pattern", PatternGlyph.MAP_CODEC, 0x6644aa);
    public static final FragmentType<SpellPart> SPELL_PART = register("spell_part", SpellPart.MAP_CODEC, 0xaa44aa);
    public static final FragmentType<EntityFragment> ENTITY = register("entity", EntityFragment.CODEC, 0x338888);
    public static final FragmentType<ZalgoFragment> ZALGO = register("zalgo", ZalgoFragment.CODEC, 0x444444);
    public static final FragmentType<ItemTypeFragment> ITEM_TYPE = register("item_type", ItemTypeFragment.CODEC, 0x2266aa);
    public static final FragmentType<ItemStackFragment> ITEM_STACK = register("item_stack", ItemStackFragment.CODEC, 0x2266aa);
    public static final FragmentType<BlockTypeFragment> BLOCK_TYPE = register("block_type", BlockTypeFragment.CODEC, 0x44aa33);

    private static <T extends Fragment> FragmentType<T> register(String name, MapCodec<T> codec, int color) {
        return Registry.register(REGISTRY, Trickster.id(name), new FragmentType<>(codec, OptionalInt.of(color)));
    }

    private static <T extends Fragment> FragmentType<T> register(String name, MapCodec<T> codec) {
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
}
