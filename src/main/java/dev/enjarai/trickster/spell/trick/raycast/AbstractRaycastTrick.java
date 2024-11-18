package dev.enjarai.trickster.spell.trick.raycast;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.trick.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public abstract class AbstractRaycastTrick<T> extends AbstractLivingEntityQueryTrick {
    public AbstractRaycastTrick(Pattern pattern) {
        super(pattern);
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        Optional<Entity> perhapsEntity = Optional.empty();
        Vec3d position;
        Vec3d direction;

        try {
            var entity = getLivingEntity(ctx, fragments, 0);
            position = entity.getEyePos();
            direction = entity.getRotationVector();
            perhapsEntity = Optional.of(entity);
        } catch (IncorrectFragmentBlunder blunder) {
            var vec1 = expectInput(fragments, FragmentType.VECTOR, 0).vector();
            var vec2 = expectInput(fragments, FragmentType.VECTOR, 1).vector();
            position = new Vec3d(vec1.x(), vec1.y(), vec1.z());
            direction = new Vec3d(vec2.x(), vec2.y(), vec2.z()).normalize();
        }

        return extraContext(fragments, ctx, perhapsEntity, position, direction);
    }

    protected Fragment extraContext(List<Fragment> fragments, SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction) {
        return activate(ctx, entity, position, direction, null);
    }

    public abstract Fragment activate(SpellContext ctx, Optional<Entity> entity, Vec3d position, Vec3d direction, T extraContext) throws BlunderException;
}
