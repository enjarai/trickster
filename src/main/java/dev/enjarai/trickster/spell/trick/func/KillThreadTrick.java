package dev.enjarai.trickster.spell.trick.func;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.execution.SpellExecutionManager;
import dev.enjarai.trickster.spell.fragment.BooleanFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.util.OptionalUtils;

public class KillThreadTrick extends Trick {
    public KillThreadTrick() {
        super(Pattern.of(6, 3, 1, 4, 3, 7, 5, 4, 7, 6));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        return BooleanFragment.of(OptionalUtils.lift2(SpellExecutionManager::kill,
                    ctx.source().getExecutionManager(),
                    supposeInput(fragments, FragmentType.NUMBER, 0)
                        .map(NumberFragment::asInt)
                        .or(() -> OptionalUtils.conditional(i -> i >= 0, ctx.data().getSlot()))
        ).orElse(false));
    }
    
}
