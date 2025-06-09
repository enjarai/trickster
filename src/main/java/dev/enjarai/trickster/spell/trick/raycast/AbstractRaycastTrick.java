package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public abstract class AbstractRaycastTrick<T extends Fragment> extends Trick<AbstractRaycastTrick<T>> {
    public AbstractRaycastTrick(Pattern pattern, RetType<T> retType) {
        super(pattern, Signature.of(FragmentType.VECTOR, FragmentType.VECTOR, ANY.optionalOf(), AbstractRaycastTrick::fromVectors, retType.maybe()));
        overload(Signature.of(FragmentType.ENTITY, ANY.optionalOf(), AbstractRaycastTrick::fromEntity, retType.maybe()));
    }

    public Optional<T> fromVectors(SpellContext ctx, VectorFragment vec1, VectorFragment vec2, Optional<Fragment> bool) throws BlunderException {
        var position = new Vec3d(vec1.x(), vec1.y(), vec1.z());
        var direction = new Vec3d(vec2.x(), vec2.y(), vec2.z()).normalize();
        return run(ctx, Optional.empty(), position, direction, bool);
    }

    public Optional<T> fromEntity(SpellContext ctx, EntityFragment e, Optional<Fragment> bool) throws BlunderException {
        var entity = e.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));
        var position = entity.getEyePos();
        var direction = entity.getRotationVector();
        return run(ctx, Optional.of(entity), position, direction, bool);
    }

    public abstract Optional<T> run(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, Optional<Fragment> bool) throws BlunderException;
}
