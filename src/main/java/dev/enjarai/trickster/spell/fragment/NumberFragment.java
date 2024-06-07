package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;

public record NumberFragment(int number) implements Fragment {
    public static final MapCodec<NumberFragment> CODEC =
            Codec.INT.fieldOf("number").xmap(NumberFragment::new, NumberFragment::number);

    @Override
    public FragmentType<?> type() {
        return FragmentType.NUMBER;
    }
}
