package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.Entity;

public class HeightReflectionTrick extends AbstractEntityQueryTrick<Entity, NumberFragment> {
    public HeightReflectionTrick() {
        super(Pattern.of(1, 2, 8, 7, 4, 1), Entity.class, FragmentType.NUMBER);
    }

    @Override
    protected NumberFragment run(SpellContext ctx, Entity entity) throws BlunderException {
        return new NumberFragment(entity.getHeight());
    }
}
