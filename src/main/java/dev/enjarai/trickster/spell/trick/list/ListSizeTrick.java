package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;

import java.util.List;

public class ListSizeTrick extends DistortionTrick<ListSizeTrick> {
    public ListSizeTrick() {
        super(Pattern.of(0, 2, 5, 4, 3, 0), Signature.of(list(Fragment.class), ListSizeTrick::run, FragmentType.NUMBER));
    }

    public NumberFragment run(SpellContext ctx, List<Fragment> list) throws BlunderException {
        return new NumberFragment(list.size());
    }
}
