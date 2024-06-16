package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.DivideByZeroBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.IncompatibleTypesBlunder;
import net.minecraft.text.Text;

public record NumberFragment(double number) implements Fragment, AddableFragment, SubtractableFragment, MultiplicableFragment, DivisibleFragment, RoundableFragment {
    public static final MapCodec<NumberFragment> CODEC =
            Codec.DOUBLE.fieldOf("number").xmap(NumberFragment::new, NumberFragment::number);

    @Override
    public FragmentType<?> type() {
        return FragmentType.NUMBER;
    }

    @Override
    public Text asText() {
        return Text.of(String.valueOf(number));
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(number != 0);
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number + num.number);
        }
        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public SubtractableFragment subtract(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number - num.number);
        }
        throw new IncompatibleTypesBlunder(Tricks.SUBTRACT);
    }

    @Override
    public MultiplicableFragment multiply(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number * num.number);
        }
        throw new IncompatibleTypesBlunder(Tricks.MULTIPLY);
    }

    @Override
    public DivisibleFragment divide(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            if (num.number == 0) {
                throw new DivideByZeroBlunder(Tricks.DIVIDE);
            }

            return new NumberFragment(number / num.number);
        }
        throw new IncompatibleTypesBlunder(Tricks.DIVIDE);
    }

    @Override
    public RoundableFragment floor() throws BlunderException {
        return new NumberFragment(Math.floor(number));
    }

    @Override
    public RoundableFragment ceil() throws BlunderException {
        return new NumberFragment(Math.ceil(number));
    }

    @Override
    public RoundableFragment round() throws BlunderException {
        return new NumberFragment(Math.round(number));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NumberFragment n) {
            var precision = 1 / 16d;
            return n.number > number - precision && n.number < number + precision;
        }
        return false;
    }
}
