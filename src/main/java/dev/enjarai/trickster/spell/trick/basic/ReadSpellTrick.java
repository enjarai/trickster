package dev.enjarai.trickster.spell.trick.basic;

import dev.enjarai.trickster.item.component.SpellComponent;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.PlayerSpellSource;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ReadSpellTrick extends Trick {
    public ReadSpellTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 4, 5, 2, 1));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return ctx.source().getOtherHandStack(PlayerSpellSource::isSpellStack)
                .<Fragment>flatMap(SpellComponent::getSpellPart)
                .orElse(VoidFragment.INSTANCE);
    }
}
