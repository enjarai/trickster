package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooLargeBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class ChangeWeightTrick extends Trick {
    public ChangeWeightTrick() {
        super(Pattern.of(0, 3, 6, 7, 4, 1, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = expectInput(fragments, FragmentType.ENTITY, 0);
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var weight = expectInput(fragments, FragmentType.NUMBER, 1).number();

        if (weight > 1) {
            throw new NumberTooLargeBlunder(this, 1);
        } else if (weight < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        ctx.useMana(this, 60);

        if (entity instanceof LivingEntity) {
            ModEntityComponents.WEIGHT.get(entity).setWeight(weight);
            ModEntityComponents.GRACE.get(entity).triggerGrace("weight", 20);
        }

        return target;
    }
}
