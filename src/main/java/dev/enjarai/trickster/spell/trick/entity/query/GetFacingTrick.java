package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import org.joml.Vector3d;

public class GetFacingTrick extends AbstractEntityQueryTrick<LivingEntity> {
    public GetFacingTrick() {
        super(Pattern.of(3, 2, 7), LivingEntity.class);
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        var facing = entity.getRotationVector();

        return new VectorFragment(new Vector3d(facing.x, facing.y, facing.z));
    }
}
