package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import net.minecraft.entity.Entity;

public abstract class AbstractEntityQueryTrick<T extends Entity> extends Trick<AbstractEntityQueryTrick<T>> {
    private final Class<T> clazz;

    public AbstractEntityQueryTrick(Pattern pattern, Class<T> clazz) {
        super(pattern, Signature.of(FragmentType.ENTITY, AbstractEntityQueryTrick::run));
        this.clazz = clazz;
    }

    protected abstract Fragment run(SpellContext ctx, T entity) throws BlunderException;

    public Fragment run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        var resolvedEntity = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (clazz.isInstance(resolvedEntity)) {
            return run(ctx, clazz.cast(resolvedEntity));
        }

        throw new EntityInvalidBlunder(this);
    }
}
