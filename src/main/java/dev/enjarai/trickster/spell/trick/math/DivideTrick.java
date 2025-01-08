package dev.enjarai.trickster.spell.trick.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.fragment.DivisibleFragment;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.MissingInputsBlunder;

import java.util.List;

public class DivideTrick extends DistortionTrick {
    public DivideTrick() {
        super(Pattern.of(0, 1, 2, 4, 6, 7, 8));
    }

    public Fragment math(SpellContext ctx, ListFragment numbers) throws BlunderException {
        if (numbers.fragments().isEmpty()) {
            throw new MissingInputsBlunder(this);
        }

        for (var number : numbers.fragments()) {
            if (!(number instanceof DivisibleFragment)) {
                throw new IncorrectFragmentBlunder(this, );
            }
        }

        return math(ctx, numbers.fragments().getFirst(), numbers.fragments().subList(0, numbers.fragments().size()));
    }

    public Fragment math(SpellContext ctx, DivisibleFragment base, List<DivisibleFragment> fragments) throws BlunderException {
        for (var fragment : fragments) {
            base = base.divide(fragment);
        }

        return base;
    }
}
