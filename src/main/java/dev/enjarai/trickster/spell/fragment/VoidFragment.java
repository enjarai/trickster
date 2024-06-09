package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;

public record VoidFragment() implements Fragment {
    public static final VoidFragment INSTANCE = new VoidFragment();
    public static final MapCodec<VoidFragment> CODEC = MapCodec.unit(INSTANCE);

    @Override
    public FragmentType<?> type() {
        return FragmentType.VOID;
    }

    @Override
    public String asString() {
        return "void";
    }
}
