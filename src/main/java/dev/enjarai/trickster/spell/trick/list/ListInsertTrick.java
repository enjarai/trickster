package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.trick.blunder.BlunderException;
import dev.enjarai.trickster.spell.trick.blunder.IndexOutOfBoundsBlunder;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;

public class ListInsertTrick extends Trick {
    public ListInsertTrick() {
        super(Pattern.of(6, 3, 0, 4, 2, 5, 8));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var index = expectInput(fragments, FragmentType.NUMBER, 1);
        var toAdd = fragments.subList(2, fragments.size());

        if (index.number() < 0 || index.number() >= list.fragments().size()) {
            throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(index.number()));
        }

        var newList = new ArrayList<Fragment>(list.fragments().size() + 1);
        newList.addAll(list.fragments());
        newList.addAll((int) Math.floor(index.number()), toAdd);
        return new ListFragment(ImmutableList.copyOf(newList));
    }
}
