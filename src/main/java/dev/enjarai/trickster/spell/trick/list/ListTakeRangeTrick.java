package dev.enjarai.trickster.spell.trick.list;

import java.util.List;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooLargeBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

public class ListTakeRangeTrick extends DistortionTrick {
    public ListTakeRangeTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 4, 8, 5));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        int listSize = list.fragments().size();
        int start = expectInput(fragments, FragmentType.NUMBER, 1).asInt();
        int end = supposeInput(fragments, FragmentType.NUMBER, 2)
                .map(NumberFragment::asInt)
                .orElse(listSize);

        if (start < 0) {
            throw new NumberTooSmallBlunder(this, 0);
        }

        if (start >= listSize) {
            throw new NumberTooLargeBlunder(this, listSize - 1);
        }

        if (end > listSize) {
            throw new NumberTooLargeBlunder(this, listSize);
        }

        if (end < start) {
            throw new NumberTooSmallBlunder(this, start);
        }

        return new ListFragment(list.fragments().subList(start, end));
    }
}
