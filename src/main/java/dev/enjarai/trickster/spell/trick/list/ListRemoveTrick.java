package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.Trick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.IndexOutOfBoundsBlunder;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListRemoveTrick extends Trick {
    public ListRemoveTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2));
    }

    @Override
    public Fragment activate(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        var list = expectInput(fragments, FragmentType.LIST, 0);
        var indexes = fragments.subList(1, fragments.size());

        for (int i = 0, indexesSize = indexes.size(); i < indexesSize; i++) {
            var index = indexes.get(i);
            if (index.type() != FragmentType.NUMBER) {
                throw new IncorrectFragmentBlunder(this, i, FragmentType.NUMBER.getName(), index);
            }

            var numberIndex = (NumberFragment) index;
            if (numberIndex.number() < 0 || numberIndex.number() >= list.fragments().size()) {
                throw new IndexOutOfBoundsBlunder(this, MathHelper.floor(numberIndex.number()));
            }
        }

        var newList = new ArrayList<Fragment>(list.fragments().size());
        newList.addAll(list.fragments());

        for (var index : indexes) {
            var indexValue = MathHelper.floor(((NumberFragment) index).number());
            newList.set(indexValue, null);
        }
        newList.removeIf(Objects::isNull);

        return new ListFragment(ImmutableList.copyOf(newList));
    }
}
