package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.execution.TickData;
import net.minecraft.entity.player.PlayerEntity;
import org.joml.Vector3d;

import java.util.HashMap;
import java.util.List;

public class AddVelocityTrick extends Trick {
    private static final TickData.Key<HashMap<EntityFragment, Float>> COMPOUND_LEN = new TickData.Key<>(Trickster.id("impulse_compound_len"), null);

    public AddVelocityTrick() {
        super(Pattern.of(4, 6, 0, 1, 2, 8, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var vector = expectInput(fragments, FragmentType.VECTOR, 1).vector();
        tryWard(ctx, entity, fragments);

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
