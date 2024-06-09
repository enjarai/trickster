package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.IncompatibleTypesBlunder;

public record NumberFragment(int number) implements Fragment, AddableFragment {
    public static final MapCodec<NumberFragment> CODEC =
            Codec.INT.fieldOf("number").xmap(NumberFragment::new, NumberFragment::number);

    @Override
    public FragmentType<?> type() {
        return FragmentType.NUMBER;
    }

    @Override
    public String asString() {
        return String.valueOf(number);
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number + num.number);
        }
        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }
}
