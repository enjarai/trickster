package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.entity.ModEntities;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.EntityCannotBeDisplacedBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.execution.TickData;
import java.util.HashMap;

public class DisplaceEntityTrick extends Trick<DisplaceEntityTrick> {
    private static final TickData.Key<HashMap<EntityFragment, Float>> COMPOUND_LEN = new TickData.Key<>(
            Trickster.id("displace_compound_len"), null
    );

    public DisplaceEntityTrick() {
        super(
                Pattern.of(1, 5, 7, 3, 1, 8, 3, 2, 7, 0, 5, 6, 1),
                Signature.of(FragmentType.ENTITY.wardOf(), FragmentType.VECTOR, DisplaceEntityTrick::run)
        );
    }

    public Fragment run(SpellContext ctx, EntityFragment target, VectorFragment v) throws BlunderException {
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var vector = v.vector();

        expectCanBuild(ctx, entity.getBlockPos());

        if (entity.getType().isIn(ModEntities.IRREPRESSIBLE))
            throw new EntityCannotBeDisplacedBlunder(this, entity);

        var map = COMPOUND_LEN.set(ctx.data(), COMPOUND_LEN.get(ctx.data()).orElse(new HashMap<>()));
        var length = (float) vector.length() + map.getOrDefault(target, 0f);
        ctx.useMana(this, 20 + (float) Math.pow(1.35, length));
        map.put(target, length);

        ModEntityComponents.DISPLACEMENT.get(entity).modify(vector);
        return target;
    }
}
