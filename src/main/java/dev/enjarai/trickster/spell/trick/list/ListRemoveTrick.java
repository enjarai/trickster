package dev.enjarai.trickster.spell.trick.list;

import com.google.common.collect.ImmutableList;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.NumberFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.IncorrectFragmentBlunder;
import dev.enjarai.trickster.spell.blunder.IndexOutOfBoundsBlunder;
import dev.enjarai.trickster.spell.type.Signature;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ListRemoveTrick extends DistortionTrick<ListRemoveTrick> {
    public ListRemoveTrick() {
        super(Pattern.of(6, 3, 0, 4, 8, 5, 2), Signature.of(FragmentType.LIST, ANY_VARIADIC, ListRemoveTrick::remove));
    }

    public Fragment remove(SpellContext ctx, ListFragment list, List<Fragment> indexes) throws BlunderException {
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
