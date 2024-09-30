package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Tricks;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.DivideByZeroBlunder;
import dev.enjarai.trickster.spell.blunder.IncompatibleTypesBlunder;
import dev.enjarai.trickster.spell.blunder.NaNBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import org.joml.Vector3d;
import net.minecraft.text.Text;

import java.util.Objects;

public final class NumberFragment implements AddableFragment, SubtractableFragment, MultiplicableFragment, DivisibleFragment, RoundableFragment {
    public static final StructEndec<NumberFragment> ENDEC = StructEndecBuilder.of(
            Endec.DOUBLE.fieldOf("number", NumberFragment::number),
            NumberFragment::new
    );

    private final double number;

    public NumberFragment(double number) throws BlunderException {
        if (Double.isNaN(number))
            throw new NaNBlunder();

        this.number = number;
    }

    @Override
    public FragmentType<?> type() {
        return FragmentType.NUMBER;
    }

    @Override
    public Text asText() {
        return Text.literal(String.format("%.2f", number));
    }

    @Override
    public boolean asBoolean() {
        return number != 0;
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number + num.number);
        } else if (other instanceof VectorFragment vec) {
            var vector = vec.vector();
            return new VectorFragment(new Vector3d(number + vector.x(), number + vector.y(), number + vector.z()));
        }

        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public SubtractableFragment subtract(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number - num.number);
        } else if (other instanceof VectorFragment vec) {
            var vector = vec.vector();
            return new VectorFragment(new Vector3d(number - vector.x(), number - vector.y(), number - vector.z()));
        }

        throw new IncompatibleTypesBlunder(Tricks.SUBTRACT);
    }

    @Override
    public MultiplicableFragment multiply(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new NumberFragment(number * num.number);
        } else if (other instanceof VectorFragment vec) {
            var vector = vec.vector();
            return new VectorFragment(new Vector3d(number * vector.x(), number * vector.y(), number * vector.z()));
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
        } else if (other instanceof VectorFragment vec) {
            var vector = vec.vector();

            if (vector.x() * vector.y() * vector.z() == 0) {
                throw new DivideByZeroBlunder(Tricks.DIVIDE);
            }

            return new VectorFragment(new Vector3d(number / vector.x(), number / vector.y(), number / vector.z()));
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

    public boolean isInteger() {
        return number - Math.floor(number) == 0;
    }

    public int asInt() {
        return (int) Math.floor(number);
    }

    public double number() {
        return number;
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }

    @Override
    public String toString() {
        return "NumberFragment[" +
                "number=" + number + ']';
    }

}
