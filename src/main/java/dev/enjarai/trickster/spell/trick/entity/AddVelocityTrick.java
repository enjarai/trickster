package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.execution.TickData;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VectorFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.HashMap;

public class AddVelocityTrick extends Trick<AddVelocityTrick> {
    private static final TickData.Key<HashMap<EntityFragment, Float>> COMPOUND_LEN = new TickData.Key<>(
            Trickster.id("impulse_compound_len"), null
    );

    public AddVelocityTrick() {
        super(
                Pattern.of(4, 6, 0, 1, 2, 8, 4),
                Signature.of(FragmentType.ENTITY.wardOf(), FragmentType.VECTOR, AddVelocityTrick::run, FragmentType.ENTITY)
        );
    }

    public EntityFragment run(SpellContext ctx, EntityFragment target, VectorFragment v) {
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var vector = v.vector();

        var map = COMPOUND_LEN.set(ctx.data(), COMPOUND_LEN.get(ctx.data()).orElse(new HashMap<>()));
        var length = (float) vector.length() + map.getOrDefault(target, 0f);
        ctx.useMana(this, 3 + (float) Math.pow(length, 3) * 2);
        map.put(target, length);

        entity.addVelocity(vector.x(), vector.y(), vector.z());
        entity.limitFallDistance();
        entity.velocityModified = true;

        return target;
    }
}
