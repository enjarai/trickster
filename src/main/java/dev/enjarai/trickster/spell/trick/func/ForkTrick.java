package dev.enjarai.trickster.spell.trick.func;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncompatibleSourceBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ForkTrick extends Trick<ForkTrick> {
    public ForkTrick() {
        super(Pattern.of(7, 4, 1, 0, 3, 6, 7, 8, 5, 4, 2), Signature.of(FragmentType.SPELL_PART, ArgType.ANY.variadicOfArg(), ForkTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, SpellPart spell, List<Fragment> args) throws BlunderException {
        var queued = ctx.source()
                .getExecutionManager()
                .orElseThrow(() -> new IncompatibleSourceBlunder(this))
                .queue(spell, args);
        return new NumberFragment(queued);
    }
}
