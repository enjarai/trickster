package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.type.TrickSignature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;

public abstract class AbstractLivingEntityQueryTrick extends Trick<AbstractLivingEntityQueryTrick> {
    public AbstractLivingEntityQueryTrick(Pattern pattern) {
        super(pattern, TrickSignature.of(FragmentType.ENTITY, AbstractLivingEntityQueryTrick::run));
    }

    protected abstract Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException;

    private Fragment run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        if (entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this)) instanceof LivingEntity living)
            return run(ctx, living);

        throw new EntityInvalidBlunder(this);
    }
}
