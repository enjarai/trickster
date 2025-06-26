package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.blunder.UnknownEntityBlunder;
import dev.enjarai.trickster.spell.fragment.EntityFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.entity.Entity;

public abstract class AbstractEntityQueryTrick<T extends Entity, R> extends Trick<AbstractEntityQueryTrick<T, R>> {
    private final Class<T> clazz;

    public AbstractEntityQueryTrick(Pattern pattern, Class<T> clazz, RetType<R> retType) {
        super(pattern, Signature.of(FragmentType.ENTITY, AbstractEntityQueryTrick::run, retType));
        this.clazz = clazz;
    }

    protected abstract R run(SpellContext ctx, T entity) throws BlunderException;

    public R run(SpellContext ctx, EntityFragment entity) throws BlunderException {
        var resolvedEntity = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (clazz.isInstance(resolvedEntity)) {
            return run(ctx, clazz.cast(resolvedEntity));
        }

        throw new EntityInvalidBlunder(this);
    }
}
