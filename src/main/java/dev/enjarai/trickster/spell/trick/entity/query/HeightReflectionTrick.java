package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import net.minecraft.entity.LivingEntity;
import dev.enjarai.trickster.spell.blunder.BlunderException;

public class HeightReflectionTrick extends AbstractLivingEntityQueryTrick {
    public HeightReflectionTrick() {
        super(Pattern.of(1, 2, 8, 7, 4, 1));
    }

    @Override
    protected Fragment run(SpellContext ctx, LivingEntity entity) throws BlunderException {
        return new NumberFragment(entity.getHeight());
    }
}
