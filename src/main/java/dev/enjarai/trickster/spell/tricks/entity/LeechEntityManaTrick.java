package dev.enjarai.trickster.spell.tricks.entity;

import dev.enjarai.trickster.spell.*;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.entity.query.AbstractLivingEntityQueryTrick;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class LeechEntityManaTrick extends AbstractLivingEntityQueryTrick {
    public LeechEntityManaTrick() {
        super(Pattern.of(7, 4, 1, 0, 4, 2, 1, 3, 4, 5, 1, 6, 7, 8, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var target = getLivingEntity(ctx, fragments, 0);
        fragments = tryWard(ctx, target, fragments);

        var limit = expectInput(fragments, FragmentType.NUMBER, 1).number();

        if (target instanceof LivingEntity living) {
            ctx.addManaLink(this, living, (float)limit);
            return VoidFragment.INSTANCE;
        }

        throw new EntityInvalidBlunder(this);
    }
}
