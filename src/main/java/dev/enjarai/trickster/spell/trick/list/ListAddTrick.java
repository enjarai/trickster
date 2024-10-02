package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;

import java.util.List;

public class ListAddTrick extends DistortionTrick {
    public ListAddTrick() {
        super(Pattern.of(0, 4, 6, 3, 0, 2, 5, 8));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var toAdd = fragments.subList(1, fragments.size());

        return new ListFragment(ImmutableList.<Fragment>builder().addAll(list.fragments()).addAll(toAdd).build());
    }
}
