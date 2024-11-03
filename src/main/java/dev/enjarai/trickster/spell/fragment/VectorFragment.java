package dev.enjarai.trickster.spell.fragment;

import dev.enjarai.trickster.EndecTomfoolery;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.trick.Tricks;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.DivideByZeroBlunder;
import dev.enjarai.trickster.spell.blunder.IncompatibleTypesBlunder;
import io.wispforest.endec.Endec;
import io.wispforest.endec.StructEndec;
import io.wispforest.endec.impl.StructEndecBuilder;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3d;
import org.joml.Vector3dc;

public record VectorFragment(Vector3dc vector) implements AddableFragment, SubtractableFragment, MultiplicableFragment, DivisibleFragment, RoundableFragment {
    public static final StructEndec<VectorFragment> ENDEC = StructEndecBuilder.of(
            EndecTomfoolery.<Double, Vector3dc>vectorEndec(Endec.DOUBLE, Vector3d::new, Vector3dc::x, Vector3dc::y, Vector3dc::z)
                    .fieldOf("vector", VectorFragment::vector),
            VectorFragment::new
    );
    public static final VectorFragment ZERO = new VectorFragment(new Vector3d());

    @Override
    public FragmentType<?> type() {
        return FragmentType.VECTOR;
    }

    @Override
    public Text asText() {
        return Text.literal("(")
                .append(new NumberFragment(vector.x()).asFormattedText())
                .append(", ")
                .append(new NumberFragment(vector.y()).asFormattedText())
                .append(", ")
                .append(new NumberFragment(vector.z()).asFormattedText())
                .append(")");
    }

    @Override
    public boolean asBoolean() {
        return !this.equals(VectorFragment.ZERO);
    }

    @Override
    public int getWeight() {
        return 24;
    }

    @Override
    public AddableFragment add(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.add(vec.vector, new Vector3d()));
        } else if (other instanceof NumberFragment num) {
            return new VectorFragment(new Vector3d(vector.x() + num.number(), vector.y() + num.number(), vector.z() + num.number()));
        }

        throw new IncompatibleTypesBlunder(Tricks.ADD);
    }

    @Override
    public SubtractableFragment subtract(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.sub(vec.vector, new Vector3d()));
        } else if (other instanceof NumberFragment num) {
            return new VectorFragment(new Vector3d(vector.x() - num.number(), vector.y() - num.number(), vector.z() - num.number()));
        }

        throw new IncompatibleTypesBlunder(Tricks.SUBTRACT);
    }

    @Override
    public MultiplicableFragment multiply(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            return new VectorFragment(vector.mul(vec.vector, new Vector3d()));
        } else if (other instanceof NumberFragment num) {
            return new VectorFragment(vector.mul(num.number(), new Vector3d()));
        }

        throw new IncompatibleTypesBlunder(Tricks.MULTIPLY);
    }

    @Override
    public DivisibleFragment divide(Fragment other) throws BlunderException {
        if (other instanceof VectorFragment vec) {
            if (vec.vector.x() * vec.vector.y() * vec.vector.z() == 0) {
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
        var dPos = pos.toCenterPos();
        return new VectorFragment(new Vector3d(dPos.getX(), dPos.getY(), dPos.getZ()));
    }

    public static VectorFragment of(Vec3d pos) {
        return new VectorFragment(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    public static VectorFragment of(Vec3i pos) {
        return new VectorFragment(new Vector3d(pos.getX(), pos.getY(), pos.getZ()));
    }

    public BlockPos toBlockPos() {
        return BlockPos.ofFloored(vector.x(), vector.y(), vector.z());
    }

    public Direction toDirection() {
        return Direction.getFacing(vector.x(), vector.y(), vector.z());
    }
}
