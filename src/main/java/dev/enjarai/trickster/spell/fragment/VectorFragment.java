package dev.enjarai.trickster.spell.fragment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.tricks.Tricks;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.DivideByZeroBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.IncompatibleTypesBlunder;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import org.joml.Vector3d;
import org.joml.Vector3dc;

import java.util.List;

public record VectorFragment(Vector3dc vector) implements Fragment, AddableFragment, SubtractableFragment, MultiplicableFragment, DivisibleFragment, RoundableFragment {
    public static final MapCodec<VectorFragment> CODEC = Codec.DOUBLE.listOf(3, 3)
            .<Vector3dc>xmap(list -> new Vector3d(list.get(0), list.get(1), list.get(2)), vec -> List.of(vec.x(), vec.y(), vec.z()))
            .xmap(VectorFragment::new, VectorFragment::vector)
            .fieldOf("vector");

    @Override
    public FragmentType<?> type() {
        return FragmentType.VECTOR;
    }

    @Override
    public Text asText() {
        return Text.of("(" + vector.x() + ", " + vector.y() + ", " + vector.z() + ")");
    }

    @Override
    public BooleanFragment asBoolean() {
        return new BooleanFragment(!new Vector3d().equals(vector));
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.add(vec.vector, new Vector3d()));
        }
        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public SubtractableFragment subtract(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.sub(vec.vector, new Vector3d()));
        }
        throw new IncompatibleTypesBlunder(Tricks.SUBTRACT);
    }

    @Override
    public MultiplicableFragment multiply(Fragment other) throws BlunderException {
        if (other instanceof NumberFragment num) {
            return new VectorFragment(vector.mul(num.number(), new Vector3d()));
        } else if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.mul(vec.vector, new Vector3d()));
        }
        throw new IncompatibleTypesBlunder(Tricks.MULTIPLY);
    }

    @Override
    public DivisibleFragment divide(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            if (vec.vector.x() == 0 || vec.vector.y() == 0 || vec.vector.z() == 0) {
                throw new DivideByZeroBlunder(Tricks.DIVIDE);
            }

            return new VectorFragment(vector.div(vec.vector, new Vector3d()));
        } else if (other instanceof NumberFragment num) {
            if (num.number() == 0) {
                throw new DivideByZeroBlunder(Tricks.DIVIDE);
            }

            return new VectorFragment(vector.div(num.number(), new Vector3d()));
        }
        throw new IncompatibleTypesBlunder(Tricks.DIVIDE);
    }

    @Override
    public RoundableFragment floor() throws BlunderException {
        return new VectorFragment(vector.floor(new Vector3d()));
    }

    @Override
    public RoundableFragment ceil() throws BlunderException {
        return new VectorFragment(vector.ceil(new Vector3d()));
    }

    @Override
    public RoundableFragment round() throws BlunderException {
        return new VectorFragment(vector.round(new Vector3d()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof VectorFragment v) {
            return v.vector.equals(vector, 1 / 16d);
        }
        return false;
    }

    public static VectorFragment of(BlockPos pos) {
        return new VectorFragment(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
    }
}
