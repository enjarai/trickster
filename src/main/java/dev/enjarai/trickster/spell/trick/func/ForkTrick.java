package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ForkTrick extends Trick<ForkTrick> {
    public ForkTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 6, 7, 8, 5, 4, 2), Signature.of(FragmentType.SPELL_PART, ANY_VARIADIC, ForkTrick::run));
    }

    public Fragment run(SpellContext ctx, SpellPart spell, List<Fragment> args) throws BlunderException {
        var queued = ctx.source()
            .getExecutionManager()
            .map(manager -> manager.queue(spell, args))
            .orElse(-1);
        return new NumberFragment(queued);
    }
}
