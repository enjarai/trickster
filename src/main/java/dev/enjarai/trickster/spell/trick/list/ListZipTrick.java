package dev.enjarai.trickster.spell.trick.list;

import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.Pattern;
import dev.enjarai.trickster.spell.SpellContext;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.fragment.FragmentType;
import dev.enjarai.trickster.spell.fragment.ListFragment;
import dev.enjarai.trickster.spell.fragment.VoidFragment;
import dev.enjarai.trickster.spell.trick.DistortionTrick;

import java.util.Arrays;
import java.util.List;

public class ListZipTrick extends DistortionTrick {
    public ListZipTrick() {
        super(Pattern.of(6, 3, 0, 2, 4, 5, 8));
    }

    @Override
    public Fragment distort(SpellContext ctx, List<Fragment> fragments) throws BlunderException {
        @SuppressWarnings("unchecked")
        List<Fragment>[] lists = fragments.stream()
                .map(f -> expectType(f, FragmentType.LIST).fragments())
                .toArray(List[]::new);

        int size = Arrays.stream(lists)
                .map(List::size)
                .max(Integer::compare)
                .orElse(0);

        var result = new ListFragment[size];

        for (int i = 0; i < size; i++) {
            var pair = new Fragment[lists.length];

            for (int j = 0; j < lists.length; j++) {
                pair[j] = i < lists[j].size() ? lists[j].get(i) : VoidFragment.INSTANCE;
            }

            result[i] = new ListFragment(Arrays.asList(pair));
        }

        return new ListFragment(Arrays.asList(result));
    }
}
