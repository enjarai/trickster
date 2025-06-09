package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NumberTooLargeBlunder;
import dev.enjarai.trickster.spell.blunder.NumberTooSmallBlunder;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.RetType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class ListTakeRangeTrick extends DistortionTrick<ListTakeRangeTrick> {
    public ListTakeRangeTrick() {
        super(Pattern.of(3, 6, 4, 0, 1, 2, 4, 8, 5), Signature.of(ArgType.ANY.listOfArg(), FragmentType.NUMBER, FragmentType.NUMBER.optionalOfArg(), ListTakeRangeTrick::take, RetType.ANY.listOfRet()));
    }

    public List<Fragment> take(SpellContext ctx, List<Fragment> list, NumberFragment startFragment, Optional<NumberFragment> endFragment) throws BlunderException {
        int listSize = list.size();
        int start = startFragment.asInt();
        int end = endFragment
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

        return list.subList(start, end);
    }
}
