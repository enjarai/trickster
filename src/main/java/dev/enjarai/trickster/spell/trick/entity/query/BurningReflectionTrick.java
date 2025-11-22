package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import net.minecraft.entity.Entity;

public class BurningReflectionTrick extends AbstractEntityQueryTrick<Entity, BooleanFragment> {
    public BurningReflectionTrick() {
        super(Pattern.of(3, 6, 8, 5, 1, 3, 0, 4, 2, 5), Entity.class, FragmentType.BOOLEAN);
    }

    @Override
    protected BooleanFragment run(SpellContext ctx, Entity entity) {
        return BooleanFragment.of(entity.isOnFire());
    }
}
