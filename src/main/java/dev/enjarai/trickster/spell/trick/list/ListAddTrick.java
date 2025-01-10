package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.type.Signature;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ListAddTrick extends DistortionTrick<ListAddTrick> {
    public ListAddTrick() {
        super(Pattern.of(0, 4, 6, 3, 0, 2, 5, 8), Signature.of(FragmentType.LIST, ANY_VARIADIC, ListAddTrick::run));
    }

    public Fragment run(SpellContext ctx, ListFragment list, List<Fragment> toAdd) throws BlunderException {
        return new ListFragment(ImmutableList.<Fragment>builder().addAll(list.fragments()).addAll(toAdd).build());
    }
}
