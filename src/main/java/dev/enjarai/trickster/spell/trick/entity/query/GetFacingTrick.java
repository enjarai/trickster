package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.entity.Entity;
import org.joml.Vector3d;

public class GetFacingTrick extends AbstractEntityQueryTrick<Entity, VectorFragment> {
    public GetFacingTrick() {
        super(Pattern.of(3, 2, 7), Entity.class, FragmentType.VECTOR);
    }

    @Override
    protected VectorFragment run(SpellContext ctx, Entity entity) throws BlunderException {
        var facing = entity.getRotationVector();

        return new VectorFragment(new Vector3d(facing.x, facing.y, facing.z));
    }
}
