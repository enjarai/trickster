package dev.enjarai.trickster.spell.trick.entity.query;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.trick.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public abstract class AbstractLivingEntityQueryTrick extends Trick {
    public AbstractLivingEntityQueryTrick(Pattern pattern) {
        super(pattern);
    }

    protected LivingEntity getLivingEntity(SpellContext ctx, List<Fragment> fragments, int index) {
        var entity = expectInput(fragments, FragmentType.ENTITY, index);
        var target = entity.getEntity(ctx).orElseThrow(() -> new UnknownEntityBlunder(this));

        if (target instanceof LivingEntity living) {
            return living;
        }

        throw new EntityInvalidBlunder(this);
    }
}
