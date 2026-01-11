package dev.enjarai.trickster.spell.trick.block;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import io.vavr.control.Either;
import net.minecraft.block.enums.RailShape;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class GetBlockRotationTrick extends Trick<GetBlockRotationTrick> {
    private static final HashMap<String, Vector3d> NAME_DIRECTION = new HashMap<>(Map.of(
            "x", new Vector3d(1, 0, 0), "y", new Vector3d(0, 1, 0), "z", new Vector3d(0, 0, 1),
            "north_south", new Vector3d(0, 0, 1), "east_west", new Vector3d(1, 0, 0),
            "ascending_east", new Vector3d(1, 1, 0), "ascending_west", new Vector3d(-1, 1, 0), "ascending_north", new Vector3d(0, 1, -1), "ascending_north", new Vector3d(0, 1, 1)
    ));
    static {
        NAME_DIRECTION.put("south_east", new Vector3d(1, 0, 1));
        NAME_DIRECTION.put("south_west", new Vector3d(-1, 0, 1));
        NAME_DIRECTION.put("north_west", new Vector3d(1, 0, -1));
        NAME_DIRECTION.put("north_east", new Vector3d(-1, 0, -1));
    }

    public GetBlockRotationTrick() {
        super(Pattern.of(3, 4, 5), Signature.of(FragmentType.VECTOR, GetBlockRotationTrick::get, FragmentType.VECTOR.or(FragmentType.NUMBER).optionalOfRet()));
    }

    public Optional<Either<VectorFragment, NumberFragment>> get(SpellContext ctx, VectorFragment pos) {
        var blockPos = pos.toBlockPos();
        expectLoaded(ctx, blockPos);

        var state = ctx.source().getWorld().getBlockState(blockPos);

        for (Property property : state.getProperties()) {
            if (property instanceof DirectionProperty directionProperty) {
                return Optional.of(Either.left(VectorFragment.of(state.get(directionProperty).getVector())));
            } else if (property == Properties.AXIS || property == Properties.HORIZONTAL_AXIS) {
                return Optional.of(Either.left(new VectorFragment(NAME_DIRECTION.get(((Direction.Axis) state.get(property)).getName()))));
            } else if (property == Properties.ORIENTATION) {
                var orientation = state.get(Properties.ORIENTATION);
                var rotation = orientation.getRotation().getVector();
                return Optional.of(Either.left(VectorFragment.of(new Vec3i(rotation.getX(), 0, rotation.getZ()).add(orientation.getFacing().getVector()))));
            } else if (property == Properties.RAIL_SHAPE || property == Properties.STRAIGHT_RAIL_SHAPE) {
                return Optional.of(Either.left(new VectorFragment(NAME_DIRECTION.get(((RailShape) state.get(property)).getName()))));
            } else if (property == Properties.ROTATION) {
                return Optional.of(Either.right(new NumberFragment(state.get(Properties.ROTATION))));
            }
        }

        return Optional.empty();

    }
}
