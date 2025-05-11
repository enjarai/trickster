package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.Entity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class HeightReflectionTrick extends AbstractEntityQueryTrick<Entity> {
    public HeightReflectionTrick() {
        super(Pattern.of(1, 2, 8, 7, 4, 1), Entity.class);
    }

    @Override
    protected Fragment run(SpellContext ctx, Entity entity) throws BlunderException {
        return new NumberFragment(entity.getHeight());
    }
}
