package dev.enjarai.trickster.spell.tricks.entity.query;

import dev.enjarai.trickster.cca.ModEntityCumponents;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.EntityInvalidBlunder;
import dev.enjarai.trickster.spell.tricks.blunder.UnknownEntityBlunder;
import net.minecraft.entity.LivingEntity;

import java.util.List;

public class GetEntityManaTrick extends AbstractLivingEntityQueryTrick {
    public GetEntityManaTrick() {
        super(Pattern.of(3, 4, 0, 3, 6, 8, 5, 2, 4, 5));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return new NumberFragment(ModEntityCumponents.MANA.get(getLivingEntity(ctx, fragments, 0)).get());
    }
}
