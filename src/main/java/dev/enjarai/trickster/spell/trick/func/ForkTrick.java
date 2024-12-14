package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ForkTrick extends Trick {
    public ForkTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 6, 7, 8, 5, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var arguments = fragments.subList(1, fragments.size());
        var queued = ctx.source()
            .getExecutionManager()
            .map(manager -> manager.queue(spell, arguments))
            .orElse(-1);
        return new NumberFragment(queued);
    }
}
