package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.NoFreeSpellSlotBlunder;

import java.util.List;

public class ForkTrick extends Trick {
    public ForkTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 6, 7, 8, 5, 4, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var spell = expectInput(fragments, FragmentType.SPELL_PART, 0);
        var arguments = fragments.subList(1, fragments.size());

        var queued = ctx.source().getExecutionManager()
                .orElseThrow(() -> new NoFreeSpellSlotBlunder(this)).queue(spell, arguments);
        if (!queued) {
            throw new NoFreeSpellSlotBlunder(this);
        }

        return VoidFragment.INSTANCE;
    }
}
