package dev.enjarai.trickster.spell.tricks.math;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.SubtractableFragment;
import dev.enjarai.trickster.spell.tricks.Trick;
import dev.enjarai.trickster.spell.tricks.blunder.BlunderException;
import dev.enjarai.trickster.spell.tricks.blunder.MissingInputsBlunder;

import java.util.List;

public class SubtractTrick extends Trick {
    public SubtractTrick() {
        super(Pattern.of(1, 4, 8, 7, 6, 4));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = supposeInput(fragments, 0).flatMap(l -> supposeType(l, FragmentType.LIST));

        if (list.isPresent()) {
            fragments = list.get().fragments();
        }

        return fragments.stream()
                .map(a -> expectType(a, SubtractableFragment.class))
                .reduce(SubtractableFragment::subtract)
                .orElseThrow(() -> new MissingInputsBlunder(this));
    }
}
