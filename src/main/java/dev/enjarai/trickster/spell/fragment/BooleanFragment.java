package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;

public record BooleanFragment(boolean bool) implements Fragment {
    public static final MapCodec<BooleanFragment> CODEC = Codec.BOOL
            .fieldOf("bool").xmap(BooleanFragment::new, BooleanFragment::bool);
    public static final BooleanFragment TRUE = new BooleanFragment(true);
    public static final BooleanFragment FALSE = new BooleanFragment(false);

    @Override
    public FragmentType<?> type() {
        return FragmentType.BOOLEAN;
    }

    @Override
    public String asString() {
        return "" + bool;
    }

    @Override
    public BooleanFragment asBoolean() {
        return bool ? TRUE : FALSE;
    }
}
