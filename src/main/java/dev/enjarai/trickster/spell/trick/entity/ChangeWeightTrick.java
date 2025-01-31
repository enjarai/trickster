package dev.enjarai.trickster.spell.trick.entity;

import dev.enjarai.trickster.cca.ModEntityComponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.*;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.LivingEntity;

public class ChangeWeightTrick extends Trick<ChangeWeightTrick> {
    public ChangeWeightTrick() {
        super(Pattern.of(0, 3, 6, 7, 4, 1, 2, 5, 8), Signature.of(FragmentType.ENTITY.wardOf(), FragmentType.NUMBER, ChangeWeightTrick::change));
    }

    public Fragment change(SpellContext ctx, EntityFragment target, NumberFragment number) throws BlunderException {
        var entity = target
                .getEntity(ctx)
                .orElseThrow(() -> new UnknownEntityBlunder(this));
        var weight = number.number();

        if (weight > 1) {
            throw new NumberTooLargeBlunder(this, 1);
        } else if (weight < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (!(entity instanceof LivingEntity)) {
            throw new EntityInvalidBlunder(this);
        }

        ctx.useMana(this, (float) (60 * (1 - weight)));

        ModEntityComponents.WEIGHT.get(entity).setWeight(weight);
        ModEntityComponents.GRACE.get(entity).triggerGrace("weight", 20);

        return target;
    }
}
