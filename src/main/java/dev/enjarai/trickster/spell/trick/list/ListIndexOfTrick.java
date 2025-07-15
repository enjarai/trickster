package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.ArgType;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;
import java.util.Optional;

public class ListIndexOfTrick extends DistortionTrick<ListIndexOfTrick> {
    public ListIndexOfTrick() {
        super(Pattern.of(8, 5, 2, 0, 3, 6, 4, 2, 1), Signature.of(ArgType.ANY.listOfArg(), ArgType.ANY, ListIndexOfTrick::indexOf, FragmentType.NUMBER.optionalOfRet()));
    }

    public Optional<NumberFragment> indexOf(SpellContext ctx, List<Fragment> list, Fragment el) throws BlunderException {
        var index = list.indexOf(el);

        return index == -1 ? Optional.empty() : Optional.of(new NumberFragment(index));
    }
}
